package ftg.application.gui.dashboard;

import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

import javax.inject.Provider;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static javafx.collections.FXCollections.observableArrayList;

public class DashboardController {

    private static final Ordering<Person> BY_STRING = Ordering.from((o1, o2) -> o1.toString().compareTo(o2.toString()));

    private final EventBus eventBus;
    private final DashboardView dashboardView;
    private final Provider<Simulation> simulationProvider;

    private final RandomModel randomModel = new RandomModel();

    private final IntegerProperty livingPersonsCount = new SimpleIntegerProperty();

    private final ObjectProperty<ObservableList<Person>> livingPersons = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Person>> deadPersons = new SimpleObjectProperty<>();
    private Simulation simulation;

    @Inject
    public DashboardController(EventBus eventBus, Provider<Simulation> simulationProvider, DashboardView dashboardView) {
        this.eventBus = eventBus;
        this.dashboardView = dashboardView;
        this.simulationProvider = simulationProvider;

        dashboardView.setOnNewSimulation(this::newSimulation);
        dashboardView.setOnPopulateSimulation(this::populateSimulation);
        dashboardView.setOnRunSimulation(this::runSimulation);
    }

    public Parent getViewRoot() {
        return dashboardView.getRoot();
    }

    public void newSimulation() {
        simulation = simulationProvider.get();
        updateView();
    }

    public void populateSimulation(int people) {
        simulationMustExist();
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        for (Country country : simulation.getConfiguration().getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            namingSystem.getUniqueSurnames().stream()
                    .limit(people)
                    .map(surname -> randomModel.newPerson(country, surname, age, simulation.getCurrentDate()))
                    .map(PersonIntroductionEvent::new).collect(Collectors.toList())
                    .forEach(eventBus::post);
        }

        updateView();
    }

    public void runSimulation(int years) {
        simulationMustExist();

        LongStream.range(0, years * DAYS_IN_YEAR)
                .forEach(i -> simulation.nextDay());

        updateView();
    }

    private Simulation simulationMustExist() {
        if (simulation == null) {
            simulation = simulationProvider.get();
            updateView();
        }

        return simulation;
    }

    private void updateView() {
        dashboardView.getLivingPeopleCount().setText(String.valueOf(simulation.getWorld().getLivingPersons().size()));
        dashboardView.getLivingPeople().setItems(observableArrayList(BY_STRING.sortedCopy(simulation.getWorld().getLivingPersons())));
        dashboardView.getDeadPeople().setItems(observableArrayList(BY_STRING.sortedCopy(simulation.getWorld().getDeadPersons())));
    }
}


