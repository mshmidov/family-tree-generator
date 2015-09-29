package ftg.application.gui.person;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ftg.model.person.Person;
import ftg.simulation.lineage.Lineages;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.fxmisc.easybind.EasyBind;
import org.reactfx.EventStreams;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class PersonLineageView extends AbstractPersonView {


    private final Provider<PersonSummaryController> personSummaryPool;

    private final PersonDetailsController selectedPerson;

    private final PersonSummaryController selectedPersonMother;
    private final PersonSummaryController selectedPersonFather;

    private final SimpleObjectProperty<List<Person>> selectedPersonChildren = new SimpleObjectProperty<>(Collections.emptyList());

    private final LinkedList<PersonSummaryController> usedPersonControllers = new LinkedList<>();
    private final LinkedList<PersonSummaryController> unusedPersonControllers = new LinkedList<>();

    @FXML
    private BorderPane root;

    @FXML
    private FlowPane parentsArea;

    @FXML
    private FlowPane childrenArea;

    @Inject
    public PersonLineageView(Provider<PersonDetailsController> personDetailsProvider,
                             Provider<PersonSummaryController> personSummaryProvider) {
        this.personSummaryPool = () -> {
            if (unusedPersonControllers.isEmpty()) {
                final PersonSummaryController provider = personSummaryProvider.get();
                unusedPersonControllers.push(provider);
                return provider;
            } else {
                return unusedPersonControllers.pop();
            }
        };

        this.selectedPerson = personDetailsProvider.get();
        this.selectedPersonFather = personSummaryProvider.get();
        this.selectedPersonMother = personSummaryProvider.get();
    }


    @FXML
    public void initialize() {
        root.setCenter(selectedPerson.getViewRoot());
        parentsArea.getChildren().add(selectedPersonFather.getViewRoot());
        parentsArea.getChildren().add(selectedPersonMother.getViewRoot());

        selectedPerson.personProperty().bind(personProperty());

        EventStreams.nonNullValuesOf(personProperty())
                .map(Lineages::findFather)
                .map(p -> p.orElse(null))
                .feedTo(selectedPersonFather.personProperty());

        EventStreams.nonNullValuesOf(personProperty())
                .map(Lineages::findMother)
                .map(p -> p.orElse(null))
                .feedTo(selectedPersonMother.personProperty());

        selectedPersonChildren.bind(EasyBind.monadic(personProperty())
                .map(Lineages::findChildren)
                .orElse(Collections.emptyList()));

        selectedPersonChildren.addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                childrenArea.getChildren().clear();
                usedPersonControllers.forEach(controller -> {
                    controller.personProperty().set(null);
                    unusedPersonControllers.push(controller);
                });
            } else {
                newValue.forEach(person -> {
                    final PersonSummaryController controller = personSummaryPool.get();
                    usedPersonControllers.push(controller);
                    childrenArea.getChildren().add(controller.getViewRoot());
                    controller.personProperty().set(person);
                });
            }
        });
    }
}
