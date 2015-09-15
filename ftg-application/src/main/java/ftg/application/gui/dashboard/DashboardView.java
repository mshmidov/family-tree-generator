package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractView;
import ftg.model.person.Person;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Optional;

public class DashboardView extends AbstractView {

    @FXML
    private ComboBox<Integer> randomPeopleCount;

    @FXML
    private ComboBox<Integer> simulationDuration;

    @FXML
    private Label livingPeopleCount;

    @FXML
    private ListView<Person> livingPeople;

    @FXML
    private ListView<Person> deadPeople;

    private Optional<EventHandler<ActionEvent>> onNewSimulation = Optional.empty();

    private Optional<EventHandler<ActionEvent>> onPopulateSimulation = Optional.empty();

    private Optional<EventHandler<ActionEvent>> onRunSimulation = Optional.empty();

    @Inject
    public DashboardView(FXMLLoader fxmlLoader) {
        super(fxmlLoader, "fx/dashboard.fxml");
    }

    ComboBox<Integer> getRandomPeopleCount() {
        return randomPeopleCount;
    }

    ComboBox<Integer> getSimulationDuration() {
        return simulationDuration;
    }

    Label getLivingPeopleCount() {
        return livingPeopleCount;
    }

    ListView<Person> getLivingPeople() {
        return livingPeople;
    }

    ListView<Person> getDeadPeople() {
        return deadPeople;
    }

    public void setOnNewSimulation(EventHandler<ActionEvent> onNewSimulation) {
        this.onNewSimulation = Optional.of(onNewSimulation);
    }

    public void setOnPopulateSimulation(EventHandler<ActionEvent> onPopulateSimulation) {
        this.onPopulateSimulation = Optional.of(onPopulateSimulation);
    }

    public void setOnRunSimulation(EventHandler<ActionEvent> onRunSimulation) {
        this.onRunSimulation = Optional.of(onRunSimulation);
    }

    @FXML
    public void newSimulation(ActionEvent actionEvent) {
        onNewSimulation.ifPresent(handler -> handler.handle(actionEvent));
    }

    @FXML
    public void populateSimulation(ActionEvent actionEvent) {
        onPopulateSimulation.ifPresent(handler -> handler.handle(actionEvent));
    }

    @FXML
    public void runSimulation(ActionEvent actionEvent) {
        onRunSimulation.ifPresent(handler -> handler.handle(actionEvent));
    }
}
