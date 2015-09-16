package ftg.application.gui.dashboard;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import ftg.application.gui.support.AbstractController;
import ftg.application.gui.support.QuickPlatformTask;
import ftg.application.gui.support.QuickTask;
import ftg.commons.range.IntegerRange;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.fxml.FXMLLoader;

import javax.inject.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class DashboardController extends AbstractController<DashboardView> {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("dashboard-controller-tasks-%d").build());

    private final RandomModel randomModel = new RandomModel();

    private final EventBus eventBus;
    private final Provider<Simulation> simulationProvider;

    private Simulation simulation;

    @Inject
    public DashboardController(EventBus eventBus, FXMLLoader fxmlLoader, Provider<Simulation> simulationProvider) {
        super(fxmlLoader, "fx/dashboard.fxml");
        this.eventBus = eventBus;
        this.simulationProvider = simulationProvider;

        view.setOnNewSimulation(this::newSimulation);
        view.setOnPopulateSimulation(this::populateSimulation);
        view.setOnRunSimulation(this::runSimulation);
    }


    private void newSimulation() {
        simulation = simulationProvider.get();
        updateView();
    }

    private void populateSimulation(int people) {
        simulationMustExist();

        executor.submit(QuickTask.of(() -> {
            final IntegerRange age = IntegerRange.inclusive(17, 50);

            for (Country country : simulation.getConfiguration().getCountries()) {
                final NamingSystem namingSystem = country.getNamingSystem();

                namingSystem.getUniqueSurnames().stream()
                        .limit(people)
                        .map(surname -> randomModel.newPerson(country, surname, age, simulation.getCurrentDate()))
                        .map(PersonIntroductionEvent::new).collect(Collectors.toList())
                        .forEach(eventBus::post);
            }
        }));

        executor.submit(QuickPlatformTask.of(this::updateView));
    }

    private void runSimulation(int years) {
        simulationMustExist();

        final int totalDays = years * DAYS_IN_YEAR;

        LongStream.range(0, totalDays).
                forEach(day -> executor.submit(QuickTask.of(simulation::nextDay)));

        executor.submit(QuickPlatformTask.of(this::updateView));
    }

    private Simulation simulationMustExist() {
        if (simulation == null) {
            simulation = simulationProvider.get();
            updateView();
        }

        return simulation;
    }

    private void updateView() {
        view.setLivingPeople(simulation.getWorld().getLivingPersons());
        view.setDeadPeople(simulation.getWorld().getDeadPersons());
    }
}


