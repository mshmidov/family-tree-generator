package ftg.application.gui.person;

import ftg.model.person.Person;
import ftg.model.person.Relations;
import ftg.model.relation.Relation;
import ftg.model.state.Death;
import ftg.model.state.State;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static ftg.model.time.TredecimalDateFormat.ISO;
import static org.reactfx.util.Tuples.t;

public final class PersonView {

    private final SimpleObjectProperty<Person> personProperty = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<TredecimalDate> currentDateProperty = new SimpleObjectProperty<>();

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

        final TredecimalDate defaultDate = new TredecimalDate(0);
        final EventStream<TredecimalDate> currentDate = EventStreams.valuesOf(currentDateProperty).map(date -> firstNonNull(date, defaultDate));

        root.visibleProperty().bind(Bindings.isNotNull(personProperty));

        final EventStream<Person> person = EventStreams.nonNullValuesOf(personProperty);

        person.mapToBi(p -> t(p.getName(), p.getSurname()))
                .map((name, surname) -> String.format("Name: %s %s", name, surname))
                .feedTo(name.textProperty());

        person.map(Person::getBirthDate).map(ISO::format).map(d -> String.format("Birth: %s", d))
                .feedTo(birthDate.textProperty());


        final EventStream<Optional<Death>> personDeath = person.map(p -> p.getOptionalState(Death.class));
        final EventStream<TredecimalDate> ageReferenceDate = EventStreams.combine(personDeath, currentDate)
                .map((death, date) -> death.map(Death::getDate).orElse(date));

        final EventStream<Long> personAge = EventStreams.combine(person.map(Person::getBirthDate), ageReferenceDate)
                .map(TredecimalDateInterval::intervalBetween)
                .map(TredecimalDateInterval::getYears);

        EventStreams.combine(personDeath, personAge)
                .map((death, age) -> String.format("%s: %s", death.map(d -> "Age at death").orElse("Age"), age))
                .feedTo(age.textProperty());

        person.map(Person::getStates).feedTo(states.itemsProperty());
        person.map(Person::getRelations).map(Relations::getAllRelations).feedTo(relations.itemsProperty());
    }

    public SimpleObjectProperty<Person> personProperty() {
        return personProperty;
    }

    public SimpleObjectProperty<TredecimalDate> currentDateProperty() {
        return currentDateProperty;
    }
}
