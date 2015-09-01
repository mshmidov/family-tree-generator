package ftg;

import ftg.commons.range.IntegerRange;
import ftg.model.Country;
import ftg.model.World;
import ftg.model.culture.Culture;
import ftg.model.time.TredecimalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);


    public static void main(String[] args) {

        final ResourceLoader resourceLoader = new ResourceLoader();
        final World world = new World(new TredecimalDate(0), resourceLoader.loadCultures());

        final Simulation simulation = new Simulation(world);

        populate(simulation);

        final TredecimalDate simulationEnd = world.getCurrentDate().plusYears(300);

        while (!simulation.getCurrentDate().isAfter(simulationEnd)) {
            simulation.nextDay();
        }

    }

    private static void populate(Simulation simulation) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final World world = simulation.getWorld();

        for (Country country : world.getCountries()) {
            final Culture culture = world.getCulture(country);

            culture.uniqueSurnames().stream()
                    .limit(100)
                    .map(surname -> simulation.randomPerson(surname, age))
                    .forEach(world::addLivingPerson);
        }
    }
}
