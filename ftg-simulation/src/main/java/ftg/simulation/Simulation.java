package ftg.simulation;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ftg.commons.generator.Generator;
import ftg.commons.generator.RandomChoice;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.relation.Marriage;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalCalendar;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;
import ftg.model.world.*;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.lineage.Lineages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.util.Objects.requireNonNull;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final EventBus eventBus;

    private final Provider<World> worldProvider;

    private final Map<String, Country> countries;

    private final Lineages lineages = new Lineages();

    private World world;


    private final Random random = new Random();

    private final Generator<Person.Sex> randomSex = RandomChoice.ofEnum(Person.Sex.class);

    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    @Inject
    public Simulation(EventBus eventBus, Configuration configuration, Provider<World> worldProvider) {
        this.eventBus = eventBus;
        this.worldProvider = worldProvider;

        this.countries = ImmutableMap.copyOf(configuration.getCountries().stream().collect(Collectors.toMap(Country::getName, c -> c)));
        this.world = worldProvider.get();

        this.eventBus.register(this);
    }

    public Map<String, Country> getCountries() {
        return countries;
    }

    public World getWorld() {
        return world;
    }

    public TredecimalDate getCurrentDate() {
        return world.getCurrentDate();
    }

    @Subscribe
    public void nextDay(SimulationStepEvent event) {
        eventBus.post(new NewDateEvent(world.getCurrentDate().plusDays(1)));
        // marriages
        final List<Person> unmarriedFemales = world.getLivingPersons().stream()
                .filter(person -> person.getSex() == Person.Sex.FEMALE)
                .filter(female -> !female.hasRelation(Marriage.class))
                .sorted((o1, o2) -> o1.getBirthDate().compareTo(o2.getBirthDate()))
                .collect(Collectors.toList());

        world.getLivingPersons().stream()
                .filter(person -> person.getSex() == Person.Sex.MALE)
                .filter(male -> !male.hasRelation(Marriage.class))
                .forEach(male -> decideMarriage(male, unmarriedFemales, world));


        // pregnancies
        world.getLivingPersons().stream()
                .filter(person -> person.getSex() == Person.Sex.FEMALE)
                .filter(female -> female.getRelations().getSingle(Marriage.class).isPresent())
                .filter(female -> fertileAge.includes(TredecimalDateInterval.intervalBetween(female.getBirthDate(), world.getCurrentDate()).getYears()))
                .filter(female -> !female.hasState(Pregnancy.class))
                .forEach(female -> decidePregnancyInMarriage(female, world));

        // births
        world.getLivingPersons().stream()
                .filter(person -> person.hasState(Pregnancy.class))
                .filter(person -> TredecimalDateInterval.intervalBetween(world.getCurrentDate(), person.getState(Pregnancy.class).getConceptionDate()).getDays() == 280)
                .forEach(female -> decideBirth(female, world));

        // deaths
        world.getLivingPersons().stream()
                .forEach(person -> decideDeath(person, world));
    }

    public Person randomPerson(Country country, Surname surname, IntegerRange age) {

        final Person.Sex sex = randomSex.get();
        int days = random.nextInt(age.getLast() * TredecimalCalendar.DAYS_IN_YEAR) - age.getFirst() * TredecimalCalendar.DAYS_IN_YEAR + 1;

        final Person newPerson = new Person(
                country.getNamingSystem().getNames(sex).get(),
                surname,
                sex,
                world.getCurrentDate().minusDays(days));

        newPerson.addState(new Residence(country.getName()));

        return newPerson;
    }

    private void decideMarriage(Person male, List<Person> unmarriedFemales, World world) {
        final double chance = 60D / 1000D / TredecimalCalendar.DAYS_IN_YEAR;
        if (random.nextDouble() >= 1 - chance) {

            final List<Person> candidates = unmarriedFemales.stream()
                    .filter(f -> !lineages.findClosestAncestry(male, f).isPresent()) // male is not descendant of female
                    .filter(f -> !lineages.findClosestAncestry(f, male).isPresent()) // female is not descendant of male
                    .filter(f -> lineages.findClosestRelation(male, f, 2).orElse(2) >= 2) // no siblings
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) {
                // TODO introduce new random female
            } else {
                int index = min((int) floor(abs(random.nextGaussian() * (candidates.size() - 1) / 3)), candidates.size() - 1);
                eventBus.post(new MarriageEvent(male, unmarriedFemales.remove(index)));
            }


        }
    }

    private void decidePregnancyInMarriage(Person female, World world) {
        final double chance = 60D / 1000D / TredecimalCalendar.DAYS_IN_YEAR;
        if (random.nextDouble() >= 1 - chance) {

            final Person.Sex sex = randomSex.get();

            eventBus.post(new ConceptionEvent(world.getCurrentDate(),
                    female.getRelations().getSingle(Marriage.class).get().getHusband(),
                    female,
                    sex));
        }
    }

    private void decideBirth(Person female, World world) {
        final Pregnancy pregnancy = female.getState(Pregnancy.class);
        final String countryOfNaming = female.getState(Residence.class).getCountry();
        final String childName = countries.get(countryOfNaming).getNamingSystem().getNameForNewborn(female, pregnancy);

        world.submitEvent(new BirthEvent(world.getCurrentDate(), female, pregnancy, childName));
    }

    private void decideDeath(Person person, World world) {
        final long age = TredecimalDateInterval.intervalBetween(person.getBirthDate(), world.getCurrentDate()).getYears();
        final Country country = requireNonNull(countries.get(person.getState(Residence.class).getCountry()));
        final double chance = 1 / country.getDemography().getDeathRisk(age, person.getSex()) / TredecimalCalendar.DAYS_IN_YEAR;

        if (random.nextDouble() >= 1 - chance) {
            eventBus.post(new DeathEvent(person));
        }
    }
}
