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
import ftg.graph.model.event.PersonIntroductionEvent;
import ftg.graph.model.world.WorldFactory;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.SimulatedCountry;
import ftg.simulation.configuration.SimulationConfiguration;
import ftg.simulation.configuration.naming.NamingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.session.Session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

    @Inject
    private Session session;

    public static void main(String[] args) throws IOException, URISyntaxException {
        final CommandLineApplication application = new CommandLineApplication();
        application.createIndexes();
        application.runSimulation();
    }

    public CommandLineApplication() {
        injector = Guice.createInjector(new Neo4JSupportModule(), new ApplicationModule());
        injector.injectMembers(this);
    }

    public void createIndexes() {
        session.query("CREATE INDEX ON :DomainObject(id)", Collections.emptyMap());
        session.query("CREATE INDEX ON :DomainObject(namespace)", Collections.emptyMap());

        session.query("CREATE INDEX ON :World(id)", Collections.emptyMap());
        session.query("CREATE INDEX ON :World(namespace)", Collections.emptyMap());

        session.query("CREATE INDEX ON :Person(id)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Person(namespace)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Person(alive)", Collections.emptyMap());

        session.query("CREATE INDEX ON :Man(id)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Man(namespace)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Man(alive)", Collections.emptyMap());

        session.query("CREATE INDEX ON :Woman(id)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Woman(namespace)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Woman(alive)", Collections.emptyMap());

        session.query("CREATE INDEX ON :Country(namespace)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Country(name)", Collections.emptyMap());

        session.query("CREATE INDEX ON :Family(namespace)", Collections.emptyMap());
        session.query("CREATE INDEX ON :Family(id)", Collections.emptyMap());
    }

    public void runSimulation() {

        populate(world);

        LongStream.range(0, 10 * DAYS_IN_YEAR).forEach(i -> simulation.nextDay(world, eventFactory));

        System.out.println("Done");
    }

    private void populate(SimulatedWorld world) {
        final RandomModel randomModel = new RandomModel(eventFactory);

        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final TredecimalDate currentDate = new TredecimalDate(0);

        for (SimulatedCountry simulatedCountry : configuration.getCountries()) {
            world.getOperations().createCountry(worldFactory.newCountry(simulatedCountry.getName()));

            final NamingSystem namingSystem = simulatedCountry.getNamingSystem();


            final List<PersonIntroductionEvent> events = namingSystem.getUniqueSurnames().stream().limit(100)
                .map(surname -> randomModel.newPersonData(simulatedCountry, surname, age, currentDate))
                .map(personData -> eventFactory.newPersonIntroductionEvent(currentDate, personData))
                .collect(Collectors.toList());

            world.submitEvents(events);
        }
    }
}
