package ftg.application;

import com.google.common.collect.ImmutableMap;
import ftg.application.configuration.Country;
import ftg.commons.generator.Generator;
import ftg.commons.generator.RandomChoice;
import ftg.commons.range.IntegerRange;
import ftg.model.World;
import ftg.model.event.BirthEvent;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.MarriageEvent;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.relation.Marriage;
import ftg.model.state.Pregnancy;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    private final Random random = new Random();

    private final Generator<Person.Sex> randomSex = RandomChoice.ofEnum(Person.Sex.class);

    final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    private final Map<String, Country> countries;

    private final World world;

    public Simulation(List<Country> countries, World world) {
        this.countries = ImmutableMap.copyOf(countries.stream().collect(Collectors.toMap(Country::getName, c -> c)));
        this.world = world;
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

    public void nextDay() {
        world.setCurrentDate(world.getCurrentDate().plusDays(1));

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
                .filter(female -> fertileAge.includes(intervalBetween(female.getBirthDate(), world.getCurrentDate()).getYears()))
                .filter(female -> !female.hasState(Pregnancy.class))
                .forEach(female -> decidePregnancyInMarriage(female, world));

        // births
        world.getLivingPersons().stream()
                .filter(person -> person.hasState(Pregnancy.class))
                .filter(person -> intervalBetween(world.getCurrentDate(), person.getState(Pregnancy.class).getConceptionDate()).getDays() == 280)
                .forEach(female -> decideBirth(female, world));

        // deaths
        world.getLivingPersons().stream()
                .forEach(person -> decideDeath(person, world));
    }

    public Person randomPerson(Country country, Surname surname, IntegerRange age) {

        final Person.Sex sex = randomSex.get();
        int days = random.nextInt(age.getLast() * DAYS_IN_YEAR) - age.getFirst() * DAYS_IN_YEAR + 1;

        final Person newPerson = new Person(
                country.getNamingSystem().getNames(sex).get(),
                surname,
                sex,
                world.getCurrentDate().minusDays(days));

        newPerson.addState(new Residence(country.getName()));

        return newPerson;
    }

    private void decideMarriage(Person male, List<Person> unmarriedFemales, World world) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (!unmarriedFemales.isEmpty() && random.nextDouble() >= 1 - chance) {
            world.submitEvent(new MarriageEvent(male, unmarriedFemales.remove(0)));
        }
    }

    private void decidePregnancyInMarriage(Person female, World world) {
        final double chance = 60D / 1000D / DAYS_IN_YEAR;
        if (random.nextDouble() >= 1 - chance) {

            final Person.Sex sex = randomSex.get();

            world.submitEvent(new ConceptionEvent(world.getCurrentDate(),
                    female.getRelations().getSingle(Marriage.class).get().getHusband(),
                    female,
                    sex));
        }
    }

    private void decideBirth(Person female, World world) {
        final Pregnancy pregnancy = female.getState(Pregnancy.class);
        final String fatherCountry = pregnancy.getFather().getState(Residence.class).getCountry();
        final String childName = countries.get(fatherCountry).getNamingSystem().getNames(pregnancy.getChildSex()).get();
        world.submitEvent(new BirthEvent(world.getCurrentDate(), female, pregnancy, childName));
    }

    private void decideDeath(Person person, World world) {
        final long age = intervalBetween(person.getBirthDate(), world.getCurrentDate()).getYears();
        final double chance = (age < 1)
                ? 100D / 1000D / DAYS_IN_YEAR
                : 15D / 1000D / DAYS_IN_YEAR;

        if (random.nextDouble() >= 1 - chance) {
            world.submitEvent(new DeathEvent(person));
        }
    }
}
