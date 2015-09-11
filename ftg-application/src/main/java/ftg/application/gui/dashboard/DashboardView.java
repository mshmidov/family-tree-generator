package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractView;
import ftg.application.gui.support.JavaFxInitializationError;
import ftg.model.person.Person;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class DashboardView extends AbstractView {

    private final FXMLLoader fxmlLoader;
    private final DashboardController controller;

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

    @Inject
    public DashboardView(FXMLLoader fxmlLoader, DashboardController controller) {
        this.fxmlLoader = fxmlLoader;
        this.controller = controller;
    }

    @Override
    protected Parent createView() {
        final String fxmlFile = "fx/dashboard.fxml";
        try (InputStream fxml = ClassLoader.getSystemResourceAsStream(fxmlFile)) {
            fxmlLoader.setController(this);
            return fxmlLoader.load(requireNonNull(fxml, "Cannot find " + fxmlFile));
        } catch (Exception e) {
            throw new JavaFxInitializationError(e);
        }
    }

    @Override
    protected void configureView() {
        livingPeopleCount.textProperty().bind(Bindings.convert(controller.livingPersonsCountProperty()));
        livingPeople.itemsProperty().bind(controller.livingPersonsProperty());
        deadPeople.itemsProperty().bind(controller.deadPersonsProperty());
    }

    @FXML
    public void newSimulation(ActionEvent actionEvent) {
        controller.newSimulation();
    }

    @FXML
    public void populateSimulation(ActionEvent actionEvent) {
        controller.populateSimulation(randomPeopleCount.getValue());
    }

    @FXML
    public void runSimulation(ActionEvent actionEvent) {
        controller.runSimulation(simulationDuration.getValue());
    }
}
