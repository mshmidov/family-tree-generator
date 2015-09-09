package ftg.application;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.gui.support.FxSupportModule;
import ftg.commons.range.IntegerRange;
import ftg.model.World;
import ftg.model.time.TredecimalDate;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);

    private Injector injector;

    @Inject
    private Simulation simulation;

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    public void runSimulation() {
        populate(simulation);

        final TredecimalDate simulationEnd = simulation.getWorld().getCurrentDate().plusYears(300);

        while (!simulation.getCurrentDate().isAfter(simulationEnd)) {
            simulation.nextDay();
        }
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new FxSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
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
