package ftg.application.gui.dashboard;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.world.PersonIntroductionEvent;
import ftg.model.world.World;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.SimulationStepEvent;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Provider;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class DashboardPresenter {

    private final EventBus eventBus;
    private final Configuration configuration;
    private final Provider<Simulation> simulationProvider;

    private final RandomModel randomModel = new RandomModel();

    private final IntegerProperty livingPersonsCount = new SimpleIntegerProperty();
    private final ObjectProperty<ObservableList<Person>> livingPersons = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Person>> deadPersons = new SimpleObjectProperty<>();

    @FXML
    private ComboBox<Integer> randomPeopleCountField;

    @FXML
    private ComboBox<Integer> simulationDurationField;

    @FXML
    private Label livingPeopleLabel;

    @FXML
    private ListView<Person> livingPeopleList;

    @FXML
    private ListView<Person> deadPeopleList;

    private Simulation simulation;

    @Inject
    public DashboardPresenter(EventBus eventBus, Configuration configuration, Provider<Simulation> simulationProvider) {
        this.eventBus = eventBus;
        this.configuration = configuration;
        this.simulationProvider = () -> {
            final Simulation simulation = simulationProvider.get();
            bindToWorld(simulation.getWorld());
            return simulation;
        };
    }

    @FXML
    public void initialize() {
        simulation = simulationProvider.get();
        bindControls();
    }

    @FXML
    public void newSimulation(ActionEvent actionEvent) {
        simulation = simulationProvider.get();
    }

    @FXML
    public void populateSimulation(ActionEvent actionEvent) {
        simulationMustExist();
        populateSimulation(randomPeopleCountField.getValue());
    }

    @FXML
    public void runSimulation(ActionEvent event) {
        simulationMustExist();

        unbindControls();

        LongStream.range(0, simulationDurationField.getValue() * DAYS_IN_YEAR)
                .forEach(i -> eventBus.post(new SimulationStepEvent()));

        bindControls();
    }

    private void bindToWorld(World world) {
        livingPersonsCount.bind(Bindings.size(world.getLivingPersons()));
        livingPersons.setValue(world.getLivingPersons());
        deadPersons.setValue(world.getDeadPersons());
    }

    private void bindControls() {
        livingPeopleLabel.textProperty().bind(Bindings.convert(livingPersonsCount));
        livingPeopleList.itemsProperty().set(new SortedList<>(livingPersons.get(), (o1, o2) -> o1.toString().compareTo(o2.toString())));
        deadPeopleList.itemsProperty().set(new SortedList<>(deadPersons.get(), (o1, o2) -> o1.toString().compareTo(o2.toString())));
    }

    private void unbindControls() {
        livingPeopleLabel.textProperty().unbind();
        livingPeopleList.itemsProperty().unbind();
        deadPeopleList.itemsProperty().unbind();
    }

    private void populateSimulation(int people) {
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            final List<PersonIntroductionEvent> events = namingSystem.getUniqueSurnames().stream()
                    .limit(people)
                    .map(surname -> randomModel.newPerson(country, surname, age, simulation.getCurrentDate()))
                    .map(PersonIntroductionEvent::new).collect(Collectors.toList());

            events.forEach(eventBus::post);
        }
    }

    private void simulationMustExist() {
        if (simulation == null) {
            simulation = simulationProvider.get();
        }
    }
}


