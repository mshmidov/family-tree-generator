package ftg.application;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ftg.application.bootstrap.Configuration;
import ftg.application.cdi.ApplicationModule;
import ftg.application.runners.SimpleRunner;
import ftg.application.runners.SimulationRunner;
import ftg.commons.range.IntegerRange;
import ftg.model.event.EventFactory;
import ftg.model.person.PersonFactory;
import ftg.model.person.Surname;
import ftg.model.person.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.model.world.country.Country;
import ftg.simulation.RandomChoice;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import javaslang.collection.Stream;

import java.io.IOException;
import java.net.URISyntaxException;

public class CommandLineApplication {

    private final Injector injector;

    private final SimulationRunner runner;

    public CommandLineApplication() {
        this.injector = Guice.createInjector(new ApplicationModule());
        this.runner = injector.getInstance(SimpleRunner.class);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        new CommandLineApplication().runSimulation();
    }

    private static World populate(World world, Configuration configuration, EventFactory eventFactory) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        final RandomModel randomModel = new RandomModel(eventFactory);

        final TredecimalDate currentDate = new TredecimalDate(0);

        for (Country country : configuration.countries()) {

            final Stream<Surname> randomNobleSurnames = Stream
                .gen(() -> country.getNativeNames().surnameFrom(RandomChoice.from(configuration.nobleSurnames(country))))
                .distinct();

            randomNobleSurnames
                .take(100)
                .forEach(surname -> randomModel.introduceRandomFamily(world, country, surname, currentDate));
        }

        return world;
    }

    public void runSimulation() {

        final Configuration configuration = inject(Configuration.class);
        final PersonFactory personFactory = inject(PersonFactory.class);
        final RelationFactory relationFactory = inject(RelationFactory.class);
        final EventFactory eventFactory = inject(EventFactory.class);

        final World world = populate(new World(configuration.countries(), personFactory, relationFactory),
                                     configuration,
                                     eventFactory);

        runner.run(inject(Simulation.class), world, 300 * DAYS_IN_YEAR);
    }

    private <T> T inject(Class<? extends T> type) {
        return injector.getInstance(type);
    }
}
