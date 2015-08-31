package ftg;

import ftg.model.Country;
import ftg.model.World;
import ftg.model.culture.Culture;
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
import org.apache.logging.log4j.ThreadContext;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalDateFormat.ISO;
import static ftg.model.time.TredecimalDateInterval.intervalBetween;
import static ftg.model.time.TredecimalDateRange.inclusiveDateRange;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    final Random random = new Random();

    final IntegerRange fertileAge = IntegerRange.inclusive(17, 49);

    public static void main(String[] args) {

        Application application = new Application();
        application.simulation();

    }

    public void simulation() {

        final ResourceLoader resourceLoader = new ResourceLoader();

        final World world = new World(new TredecimalDate(0), resourceLoader.loadCultures());

        final Culture culture = world.getCulture(Country.OMORJE);

        culture.uniqueSurnames().stream()
                .limit(100)
                .map(surname -> randomPerson(world.getCurrentDate(), surname))
                .forEach(world::addLivingPerson);


        for (TredecimalDate date : inclusiveDateRange(world.getCurrentDate(), world.getCurrentDate().plusYears(300))) {

            ThreadContext.put("date", ISO.format(date));
            world.setCurrentDate(date);

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
                    .filter(female -> fertileAge.includes(intervalBetween(female.getBirthDate(), date).getYears()))
                    .filter(female -> !female.hasState(Pregnancy.class))
                    .forEach(female -> decidePregnancyInMarriage(female, world));

            // births
            world.getLivingPersons().stream()
                    .filter(person -> person.hasState(Pregnancy.class))
                    .filter(person -> intervalBetween(date, person.getStates().getSingle(Pregnancy.class).get().getConceptionDate()).getDays() == 280)
                    .forEach(female -> decideBirth(female, world));

            // deaths
            world.getLivingPersons().stream()
                    .forEach(person -> decideDeath(person, world));
        }

        LOGGER.info("{} living persons", world.getLivingPersons().size());
        LOGGER.info("{} dead persons", world.getDeadPersons().size());
    }

    private Person randomPerson(TredecimalDate simulationStart, Surname surname) {
        final Person.Sex sex = (random.nextBoolean())
                               ? Person.Sex.MALE
                               : Person.Sex.FEMALE;

        return new Person(
                surname.getCulture().names(sex).get(),
                surname,
                sex,
                simulationStart.minusDays(random.nextInt(50 * DAYS_IN_YEAR)));
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
