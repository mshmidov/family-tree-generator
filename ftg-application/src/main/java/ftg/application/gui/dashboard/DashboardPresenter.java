package ftg.application.gui.dashboard;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.world.PersonIntroductionEvent;
import ftg.model.world.World;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.SimulationStepEvent;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

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

    @FXML
    private ComboBox<Integer> randomPeopleCountField;

    @FXML
    private ComboBox<Integer> simulationDurationField;

    @FXML
    private Label livingPeopleLabel;

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
        // eventBus.register(this);
        simulation = simulationProvider.get();
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

        LongStream.range(0, simulationDurationField.getValue() * DAYS_IN_YEAR)
                .forEach(i -> eventBus.post(new SimulationStepEvent()));

    }

    private void bindToWorld(World world) {
        livingPeopleLabel.textProperty().bind(Bindings.size(world.getLivingPersons()).asString());
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


