package ftg;

import ftg.model.Country;
import ftg.model.World;
import ftg.model.culture.Culture;
import ftg.model.culture.surname.Surname;
import ftg.model.person.Person;
import ftg.model.time.TredecimalCalendar;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateRange;

import java.util.Random;

public class Application {

    final Random random = new Random();

    public static void main(String[] args) {

        Application application = new Application();
        application.simulation();

    }

    public void simulation() {

        final ResourceLoader resourceLoader = new ResourceLoader();

        final World world = new World(resourceLoader.loadCultures());

        final TredecimalDate simulationStart = new TredecimalDate(0);
        final TredecimalDate simulationEnd = simulationStart.plusYears(100);

        final Culture culture = world.getCulture(Country.OMORJE);

        culture.uniqueSurnames().stream()
                .limit(100)
                .map(surname -> randomPerson(simulationStart, surname))
                .forEach(world::addlivingPerson);


        for (TredecimalDate date : TredecimalDateRange.inclusive(simulationStart, simulationEnd)) {

        }
    }

    private Person randomPerson(TredecimalDate simulationStart, Surname surname) {
        final Person.Sex sex = (random.nextBoolean())
                ? Person.Sex.MALE
                : Person.Sex.FEMALE;

        return new Person(
                surname.getCulture().names(sex).get(),
                surname.getForm(sex),
                sex,
                simulationStart.minusDays(random.nextInt(50 * TredecimalCalendar.DAYS_IN_YEAR)));
    }


}
