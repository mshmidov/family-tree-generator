package ftg.application.gui.person;

import ftg.model.person.Person;
import ftg.model.person.Relations;
import ftg.model.relation.Relation;
import ftg.model.state.State;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.reactfx.EventStreams;

import static ftg.model.time.TredecimalDateFormat.ISO;
import static org.reactfx.util.Tuples.t;

public final class PersonDetailsView extends AbstractPersonView {

    @FXML
    private VBox root;

    @FXML
    private Label name;

    @FXML
    private Label birthDate;

    @FXML
    private Label age;

    @FXML
    private ListView<State> states;

    @FXML
    private ListView<Relation> relations;

    @FXML
    public void initialize() {

        root.visibleProperty().bind(Bindings.isNotNull(personProperty()));

        person.mapToBi(p -> t(p.getName(), p.getSurname()))
                .map((name, surname) -> String.format("Name: %s %s", name, surname))
                .feedTo(name.textProperty());

        person.map(Person::getBirthDate).map(ISO::format).map(d -> String.format("Birth: %s", d))
                .feedTo(birthDate.textProperty());

        EventStreams.combine(personDeath, personAge)
                .map((death, age) -> String.format("%s: %s", death.map(d -> "Age at death").orElse("Age"), age))
                .feedTo(age.textProperty());

        person.map(Person::getStates).feedTo(states.itemsProperty());
        person.map(Person::getRelations).map(Relations::getAllRelations).feedTo(relations.itemsProperty());
    }

}
