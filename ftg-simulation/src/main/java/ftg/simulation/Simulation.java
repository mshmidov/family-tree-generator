package ftg.simulation;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.Util;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.world.*;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.lineage.Lineages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;
import static java.util.Objects.requireNonNull;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final RandomChoice randomChoice = new RandomChoice();
    private final Lineages lineages = new Lineages();
    private final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final EventBus eventBus;

    private final Configuration configuration;
    private final World world;

    private final Map<String, Country> countries;

    @Inject
    public Simulation(EventBus eventBus, Configuration configuration, World world) {

        this.eventBus = eventBus;
        this.configuration = configuration;
        this.world = world;
        this.countries = ImmutableMap.copyOf(configuration.getCountries().stream().collect(Collectors.toMap(Country::getName, c -> c)));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public World getWorld() {
        return world;
    }

    public TredecimalDate getCurrentDate() {
        return world.getCurrentDate();
    }

    public void nextDay() {
        final TredecimalDate currentDate = world.getCurrentDate();

        eventBus.post(new NewDateEvent(currentDate.plusDays(1)));
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
                .filter(female -> fertileAge.includes(intervalBetween(female.getBirthDate(), currentDate).getYears()))
                .filter(female -> !female.hasState(Pregnancy.class))
                .forEach(female -> decidePregnancyInMarriage(female, world));

        // births

        world.getLivingPersons().stream()
                .filter(person -> person.hasState(Pregnancy.class))
                .filter(person -> intervalBetween(currentDate, person.getState(Pregnancy.class).getConceptionDate()).getDays() == 280)
                .map(female -> decideBirth(female, world))
                .collect(Collectors.toList())
                .forEach(eventBus::post);

        // deaths
        world.getLivingPersons().stream()
                .map(person -> decideDeath(person, world))
                .flatMap(Util::streamFromOptional)
                .collect(Collectors.toList())
                .forEach(eventBus::post);
    }

    private void decideMarriage(Person male, List<Person> unmarriedFemales, World world) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final List<Person> candidates = unmarriedFemales.stream()
                    .filter(f -> !lineages.findClosestAncestry(male, f).isPresent()) // male is not descendant of female
                    .filter(f -> !lineages.findClosestAncestry(f, male).isPresent()) // female is not descendant of male
                    .filter(f -> lineages.findClosestRelation(male, f, 2).orElse(2) >= 2) // no siblings
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) {
                // TODO introduce new random female
            } else {
                int index = randomChoice.fromRangeByGaussian(candidates.size());
                eventBus.post(new MarriageEvent(male, unmarriedFemales.remove(index)));
            }


        }
    }

    private void decidePregnancyInMarriage(Person female, World world) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (randomChoice.byChance(chance)) {

            final Person.Sex sex = randomChoice.from(Person.Sex.class);

            eventBus.post(new ConceptionEvent(world.getCurrentDate(),
                    female.getRelations().getSingle(Marriage.class).get().getHusband(),
                    female,
                    sex));
        }
    }

    private Event decideBirth(Person female, World world) {
        final Pregnancy pregnancy = female.getState(Pregnancy.class);
        final String countryOfNaming = female.getState(Residence.class).getCountry();
        final String childName = countries.get(countryOfNaming).getNamingSystem().getNameForNewborn(female, pregnancy);

        return new BirthEvent(world.getCurrentDate(), female, pregnancy, childName);
    }

    private Optional<DeathEvent> decideDeath(Person person, World world) {
        final long age = intervalBetween(person.getBirthDate(), world.getCurrentDate()).getYears();
        final Country country = requireNonNull(countries.get(person.getState(Residence.class).getCountry()));
        final double chance = 1 / country.getDemography().getDeathRisk(age, person.getSex()) / DAYS_IN_YEAR;

        return randomChoice.byChance(chance) ? Optional.of(new DeathEvent(person)) : Optional.empty();
    }
}
