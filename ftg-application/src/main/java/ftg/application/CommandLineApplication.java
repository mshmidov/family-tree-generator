package ftg.application;

import ftg.application.bootstrap.ConfigurationLoader;
import ftg.commons.range.IntegerRange;
import ftg.model.World;
import ftg.model.time.TredecimalDate;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);

    public static void main(String[] args) throws IOException, URISyntaxException {

        final Configuration configuration = new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml");

        final World world = new World(new TredecimalDate(0));

        final Simulation simulation = new Simulation(configuration, world);

        populate(simulation);

        final TredecimalDate simulationEnd = world.getCurrentDate().plusYears(300);

        while (!simulation.getCurrentDate().isAfter(simulationEnd)) {
            simulation.nextDay();
        }

    }

    private static void populate(Simulation simulation) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final World world = simulation.getWorld();

        for (Country country : simulation.getCountries().values()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(100)
                    .map(surname -> simulation.randomPerson(country, surname, age))
                    .forEach(world::addLivingPerson);
        }
    }
}
