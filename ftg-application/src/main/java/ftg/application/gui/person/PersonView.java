package ftg.application.gui.person;

import ftg.model.person.Person;
import ftg.model.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.reactfx.EventStreams;

import java.util.Objects;

import static ftg.application.gui.support.ReactSupport.selectMonadicObject;
import static ftg.application.gui.support.ReactSupport.selectMonadicString;
import static ftg.model.time.TredecimalDateFormat.ISO;

public final class PersonView {

    private final SimpleObjectProperty<Person> person = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<TredecimalDate> currentDate = new SimpleObjectProperty<>();

    @FXML
    private VBox root;

    @FXML
    private Label name;

    @FXML
    private Label birthDate;

    @FXML
    private Label age;

    @FXML
    public void initialize() {

        root.visibleProperty().bind(Bindings.isNotNull(person));

        final MonadicBinding<TredecimalDate> currentDateOrZero = EasyBind.monadic(currentDate).orElse(new TredecimalDate(0));

        final MonadicBinding<String> personName = selectMonadicString(person, Person::getName);
        final MonadicBinding<String> personSurname = selectMonadicString(person, Person::getSurname);
        final MonadicBinding<String> personBirthDate = selectMonadicString(person, p -> ISO.format(p.getBirthDate()));

        final MonadicBinding<Boolean> personIsDead = selectMonadicObject(person, p -> p.hasState(Death.class), false);
        final MonadicBinding<TredecimalDate> referenceDate = selectMonadicObject(person,
                p -> p.hasState(Death.class) ? p.getState(Death.class).getDate() : currentDateOrZero.get(), new TredecimalDate(0));


        final Binding<Long> personAge = EventStreams
                .combine(
                        EventStreams.valuesOf(person).filter(Objects::nonNull).map(Person::getBirthDate),
                        EventStreams.valuesOf(referenceDate))
                .map(TredecimalDateInterval::intervalBetween)
                .map(TredecimalDateInterval::getYears)
                .toBinding(0L);


        name.textProperty().bind(Bindings.format("Name: %s %s", personName, personSurname));
        birthDate.textProperty().bind(Bindings.format("Birth: %s", personBirthDate));
        age.textProperty().bind(Bindings.format("%s: %s",
                Bindings.createStringBinding(() -> personIsDead.get() ? "Age at death" : "Age", personIsDead),
                personAge));


    }


    public SimpleObjectProperty<Person> personProperty() {
        return person;
    }

    public SimpleObjectProperty<TredecimalDate> currentDateProperty() {
        return currentDate;
    }
}
