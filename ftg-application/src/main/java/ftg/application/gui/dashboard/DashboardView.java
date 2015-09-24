package ftg.application.gui.dashboard;

import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ftg.application.gui.person.PersonController;
import ftg.commons.Action;
import ftg.model.person.Person;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;
import org.reactfx.EventStreams;

import java.util.Collection;
import java.util.Optional;
import java.util.function.IntConsumer;

import static javafx.collections.FXCollections.observableArrayList;

public final class DashboardView {

    private static final Ordering<Person> BY_NAME = Ordering
            .from((Person o1, Person o2) -> o1.getSurname().compareTo(o2.getSurname()))
            .compound((o1, o2) -> o1.getName().compareTo(o2.getName()));


    private final PersonController selectedPerson;

    @FXML
    private VBox root;

    @FXML
    private ComboBox<Integer> randomPeopleCount;

    @FXML
    private ComboBox<Integer> simulationDuration;

    @FXML
    private Accordion rightAccordion;

    @FXML
    private TitledPane livingPeoplePane;

    @FXML
    private TitledPane deadPeoplePane;

    @FXML
    private ListView<Person> livingPeople;

    @FXML
    private ListView<Person> deadPeople;

    @FXML
    private BorderPane contentPane;

    @FXML
    private ProgressBar progressBar;

    private Optional<Action> onNewSimulation = Optional.empty();

    private Optional<IntConsumer> onPopulateSimulation = Optional.empty();

    private Optional<IntConsumer> onRunSimulation = Optional.empty();

    @Inject
    public DashboardView(Provider<PersonController> personControllerProvider) {
        this.selectedPerson = personControllerProvider.get();
    }

    @FXML
    public void initialize() {

        rightAccordion.setExpandedPane(livingPeoplePane);

        livingPeoplePane.textProperty().bind(Bindings.format("Living people: %s",
                EasyBind.select(livingPeople.itemsProperty())
                        .selectObject(Bindings::size)));

        deadPeoplePane.textProperty().bind(Bindings.format("Dead people: %s",
                EasyBind.select(deadPeople.itemsProperty())
                        .selectObject(Bindings::size)));

        contentPane.setCenter(selectedPerson.getViewRoot());

        final Binding<Person> selection = EventStreams.merge(
                EventStreams.valuesOf(livingPeople.getSelectionModel().selectedItemProperty()),
                EventStreams.valuesOf(deadPeople.getSelectionModel().selectedItemProperty()))
                .toBinding(null);


        selectedPerson.personProperty().bind(selection);

    }

    public DoubleProperty progressProperty() {
        return progressBar.progressProperty();
    }

    public BooleanProperty progressVisibleProperty() {
        return progressBar.visibleProperty();
    }

    public void setLivingPeople(Collection<Person> livingPeople) {
        this.livingPeople.setItems(observableArrayList(BY_NAME.sortedCopy(livingPeople)));
    }

    public void setDeadPeople(Collection<Person> deadPeople) {
        this.deadPeople.setItems(observableArrayList(BY_NAME.sortedCopy(deadPeople)));
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
