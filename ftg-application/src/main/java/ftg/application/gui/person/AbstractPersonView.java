package ftg.application.gui.person;

import ftg.model.person.Person;
import ftg.model.state.Death;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;
import javafx.beans.property.SimpleObjectProperty;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;

public abstract class AbstractPersonView {

    private final SimpleObjectProperty<Person> personProperty = new SimpleObjectProperty<>();

    private final SimpleObjectProperty<TredecimalDate> currentDateProperty = new SimpleObjectProperty<>();

    protected final EventStream<TredecimalDate> currentDate;
    protected final EventStream<Person> person;
    protected final EventStream<Long> personAge;
    protected final EventStream<Optional<Death>> personDeath;

    public AbstractPersonView() {

        final TredecimalDate defaultDate = new TredecimalDate(0);
        currentDate = EventStreams.valuesOf(currentDateProperty).map(date -> firstNonNull(date, defaultDate));

        person = EventStreams.nonNullValuesOf(personProperty);
        personDeath = person.map(p -> p.getOptionalState(Death.class));

        final EventStream<TredecimalDate> ageReferenceDate = EventStreams.combine(personDeath, currentDate)
                .map((death, date) -> death.map(Death::getDate).orElse(date));

        personAge = EventStreams.combine(person.map(Person::getBirthDate), ageReferenceDate)
                .map(TredecimalDateInterval::intervalBetween)
                .map(TredecimalDateInterval::getYears);
    }


    public final SimpleObjectProperty<Person> personProperty() {
        return personProperty;
    }

    public final SimpleObjectProperty<TredecimalDate> currentDateProperty() {
        return currentDateProperty;
    }
}
