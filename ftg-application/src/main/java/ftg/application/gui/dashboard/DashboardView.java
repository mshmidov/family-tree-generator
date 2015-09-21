package ftg.application.gui.dashboard;

import com.google.common.collect.Ordering;
import ftg.commons.Action;
import ftg.model.person.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Collection;
import java.util.Optional;
import java.util.function.IntConsumer;

import static javafx.collections.FXCollections.observableArrayList;

public class DashboardView {

    private static final Ordering<Person> BY_STRING = Ordering.from((o1, o2) -> o1.toString().compareTo(o2.toString()));

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

    public void setLivingPeople(Collection<Person> livingPeople) {
        this.livingPeople.setItems(observableArrayList(BY_STRING.sortedCopy(livingPeople)));
        this.livingPeopleCount.setText(String.valueOf(livingPeople.size()));
    }

    public void setDeadPeople(Collection<Person> deadPeople) {
        this.deadPeople.setItems(observableArrayList(BY_STRING.sortedCopy(deadPeople)));
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
    private void newSimulation(ActionEvent actionEvent) {
        onNewSimulation.ifPresent(Action::perform);
    }

    @FXML
    private void populateSimulation(ActionEvent actionEvent) {
        onPopulateSimulation.ifPresent(handler -> handler.accept(randomPeopleCount.getValue()));
    }

    @FXML
    private void runSimulation(ActionEvent actionEvent) {
        onRunSimulation.ifPresent(handler -> handler.accept(simulationDuration.getValue()));
    }
}
