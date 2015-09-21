package ftg.application;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.cdi.ApplicationModule;
import ftg.application.cdi.FxSupportModule;
import ftg.commons.cdi.PersonCounter;
import ftg.commons.range.IntegerRange;
import ftg.model.time.TredecimalDate;
import ftg.model.world.PersonIntroductionEvent;
import ftg.model.world.World;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);

    private final RandomModel randomModel = new RandomModel();

    private Injector injector;

    @Inject
    private Configuration configuration;

    @Inject
    private Simulation simulation;

    @Inject
    @PersonCounter
    private AtomicLong personCounter;

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new FxSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    public void runSimulation() {

        final World world = new World();

        populate(world, personCounter);

        LongStream.range(0, 300 * DAYS_IN_YEAR).forEach(i -> simulation.nextDay(world));
    }

    private void populate(World world, AtomicLong personCounter) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final TredecimalDate currentDate = new TredecimalDate(0);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(100)
                    .map(surname -> randomModel.newPersonData(personCounter.incrementAndGet(), country, surname, age, currentDate))
                    .map(personData -> new PersonIntroductionEvent(currentDate, personData))
                    .forEach(world::submitEvent);
        }
    }
}
