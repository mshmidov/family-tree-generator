package ftg.application.gui.dashboard;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.SimulationStepEvent;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import javax.inject.Provider;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class DashboardController {

    private final EventBus eventBus;
    private final Configuration configuration;
    private final DashboardView dashboardView;
    private final Provider<Simulation> simulationProvider;

    private final RandomModel randomModel = new RandomModel();

    private final Supplier<Parent> viewInitializer = Suppliers.memoize(this::initView);

    private final IntegerProperty livingPersonsCount = new SimpleIntegerProperty();

    private final ObjectProperty<ObservableList<Person>> livingPersons = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Person>> deadPersons = new SimpleObjectProperty<>();
    private Simulation simulation;

    @Inject
    public DashboardController(EventBus eventBus, Configuration configuration, Provider<Simulation> simulationProvider, DashboardView dashboardView) {
        this.eventBus = eventBus;
        this.configuration = configuration;
        this.dashboardView = dashboardView;
        this.simulationProvider = simulationProvider;
    }

    public Parent getViewRoot() {
        return viewInitializer.get();
    }

    private Parent initView() {
        final Parent parent = dashboardView.getParent();

        dashboardView.setOnNewSimulation(this::newSimulation);
        dashboardView.setOnPopulateSimulation(this::populateSimulation);
        dashboardView.setOnRunSimulation(this::runSimulation);

        return parent;
    }

    public void newSimulation(ActionEvent event) {
        simulation = simulationProvider.get();
    }

    public void populateSimulation(ActionEvent event) {
        simulationMustExist();
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(dashboardView.getRandomPeopleCount().getValue())
                    .map(surname -> randomModel.newPerson(country, surname, age, simulation.getCurrentDate()))
                    .map(PersonIntroductionEvent::new).collect(Collectors.toList())
                    .forEach(eventBus::post);
        }
    }

    public void runSimulation(ActionEvent event) {
        simulationMustExist();

        LongStream.range(0, dashboardView.getSimulationDuration().getValue() * DAYS_IN_YEAR)
                .forEach(i -> eventBus.post(new SimulationStepEvent()));
    }

    private Simulation simulationMustExist() {
        if (simulation == null) {
            simulation = simulationProvider.get();
        }

        return simulation;
    }
}


