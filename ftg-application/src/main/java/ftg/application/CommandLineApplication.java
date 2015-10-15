package ftg.application;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.cdi.ApplicationModule;
import ftg.application.neo4j.cdi.Neo4JSupportModule;
import ftg.commons.cdi.Identifier;
import ftg.commons.range.IntegerRange;
import ftg.model.time.TredecimalDate;
import ftg.model.world.PersonIntroductionEvent;
import ftg.model.world.World;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.SimulationConfiguration;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Supplier;
import java.util.stream.LongStream;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);

    private final RandomModel randomModel = new RandomModel();

    private Injector injector;

    @Inject
    private SimulationConfiguration configuration;

    @Inject
    private Simulation simulation;

    @Inject
    @Identifier
    private Supplier<String> identifier;

    @Inject
    private World world;

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new Neo4JSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    public void runSimulation() {

        populate(world, identifier);

        LongStream.range(0, 300 * DAYS_IN_YEAR).forEach(i -> simulation.nextDay(world));
    }

    private void populate(World world, Supplier<String> identifier) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final TredecimalDate currentDate = new TredecimalDate(0);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(100)
                .map(surname -> randomModel.newPersonData(identifier.get(), country, surname, age, currentDate))
                .map(personData -> new PersonIntroductionEvent(identifier.get(), currentDate, personData))
                    .forEach(world::submitEvent);
        }
    }
}
