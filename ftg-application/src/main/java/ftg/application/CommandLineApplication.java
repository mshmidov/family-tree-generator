package ftg.application;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ftg.application.gui.support.FxSupportModule;
import ftg.commons.range.IntegerRange;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.Simulation;
import ftg.simulation.SimulationStepEvent;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class CommandLineApplication {

    private static final Logger LOGGER = LogManager.getLogger(CommandLineApplication.class);
    private Injector injector;

    @Inject
    private EventBus eventBus;

    @Inject
    private Simulation simulation;

    @Inject
    private Configuration configuration;

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new FxSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    public void runSimulation() {
        populate();

        LongStream.range(0, 300 * DAYS_IN_YEAR).forEach(i -> eventBus.post(new SimulationStepEvent()));
    }

    private void populate() {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(100)
                    .map(surname -> simulation.randomPerson(country, surname, age))
                    .map(PersonIntroductionEvent::new)
                    .forEach(eventBus::post);
        }
    }
}
