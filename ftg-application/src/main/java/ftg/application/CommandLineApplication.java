package ftg.application;

import static ftg.commons.time.TredecimalCalendar.DAYS_IN_YEAR;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.cdi.ApplicationModule;
import ftg.application.cdi.Neo4JSupportModule;
import ftg.commons.range.IntegerRange;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.event.EventFactory;
import ftg.graph.model.world.WorldFactory;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.SimulatedCountry;
import ftg.simulation.configuration.SimulationConfiguration;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.LongStream;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);

    private Injector injector;

    @Inject
    private SimulationConfiguration configuration;

    @Inject
    private Simulation simulation;

    @Inject
    private SimulatedWorld world;

    @Inject
    private EventFactory eventFactory;

    @Inject
    private WorldFactory worldFactory;

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new Neo4JSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    public void runSimulation() {

        populate(world);

        LongStream.range(0, 300 * DAYS_IN_YEAR).forEach(i -> simulation.nextDay(world, eventFactory));
    }

    private void populate(SimulatedWorld world) {
        final RandomModel randomModel = new RandomModel(eventFactory);

        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final TredecimalDate currentDate = new TredecimalDate(0);

        for (SimulatedCountry simulatedCountry : configuration.getCountries()) {
            world.getOperations().createCountry(worldFactory.newCountry(simulatedCountry.getName()));

            final NamingSystem namingSystem = simulatedCountry.getNamingSystem();


            namingSystem.getUniqueSurnames().stream().limit(100)
                .map(surname -> randomModel.newPersonData(simulatedCountry, surname, age, currentDate))
                .map(personData -> eventFactory.newPersonIntroductionEvent(currentDate, personData))
                    .forEach(world::submitEvent);
        }
    }
}
