package ftg;

import ftg.model.World;
import ftg.model.culture.surname.Surname;
import ftg.model.event.BirthEvent;
import ftg.model.event.ConceptionEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.MarriageEvent;
import ftg.model.person.Person;
import ftg.model.relation.Marriage;
import ftg.model.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.util.IntegerRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;

public final class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class);

    final Random random = new Random();

    private final World world;

    final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    public Simulation(World world) {
        this.world = world;
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
                .filter(person -> intervalBetween(world.getCurrentDate(), person.getStates().getSingle(Pregnancy.class).get().getConceptionDate()).getDays() == 280)
                .forEach(female -> decideBirth(female, world));

        // deaths
        world.getLivingPersons().stream()
                .forEach(person -> decideDeath(person, world));
    }

    public Person randomPerson(Surname surname) {
        final Person.Sex sex = (random.nextBoolean())
                               ? Person.Sex.MALE
                               : Person.Sex.FEMALE;

        return new Person(
                surname.getCulture().names(sex).get(),
                surname,
                sex,
                world.getOriginDate().minusDays(random.nextInt(50 * DAYS_IN_YEAR)));
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

            final Person.Sex sex = (random.nextBoolean())
                                   ? Person.Sex.MALE
                                   : Person.Sex.FEMALE;

            world.submitEvent(new ConceptionEvent(world.getCurrentDate(),
                    female.getRelations().getSingle(Marriage.class).get().getHusband(),
                    female,
                    sex));
        }
    }

    private void decideBirth(Person female, World world) {
        final Pregnancy pregnancy = female.getStates().getSingle(Pregnancy.class).get();
        final String childName = pregnancy.getFather().getSurnameObject().getCulture().names(pregnancy.getChildSex()).get();
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
