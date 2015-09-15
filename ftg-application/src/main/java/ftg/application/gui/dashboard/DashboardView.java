package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractView;
import ftg.commons.Action;
import ftg.model.person.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Optional;
import java.util.function.IntConsumer;

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

    private Optional<Action> onNewSimulation = Optional.empty();

    private Optional<IntConsumer> onPopulateSimulation = Optional.empty();

    private Optional<IntConsumer> onRunSimulation = Optional.empty();

    @Inject
    public DashboardView(FXMLLoader fxmlLoader) {
        super(fxmlLoader, "fx/dashboard.fxml");
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

    public void setOnNewSimulation(Action handler) {
        this.onNewSimulation = Optional.of(handler);
    }

    public void setOnPopulateSimulation(IntConsumer handler) {
        this.onPopulateSimulation = Optional.of(handler);
    }

    public void setOnRunSimulation(IntConsumer handler) {
        this.onRunSimulation = Optional.of(handler);
    }

    @FXML
    public void newSimulation(ActionEvent actionEvent) {
        onNewSimulation.ifPresent(Action::perform);
    }

    @FXML
    public void populateSimulation(ActionEvent actionEvent) {
        onPopulateSimulation.ifPresent(handler -> handler.accept(randomPeopleCount.getValue()));
    }

    @FXML
    public void runSimulation(ActionEvent actionEvent) {
        onRunSimulation.ifPresent(handler -> handler.accept(simulationDuration.getValue()));
    }
}
