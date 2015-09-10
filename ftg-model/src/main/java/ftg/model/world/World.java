package ftg.model.world;

import com.google.common.eventbus.Subscribe;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import static com.google.common.base.Preconditions.checkState;
import static ftg.model.time.TredecimalDateFormat.ISO;

public final class World {

    private static final Logger LOGGER = LogManager.getLogger(World.class);

    private final ObservableList<Person> livingPersons = FXCollections.observableArrayList();
    private final ObservableList<Person> deadPersons = FXCollections.observableArrayList();
    private final ObservableList<Event> events = FXCollections.observableArrayList();

    private ReadOnlyObjectWrapper<TredecimalDate> currentDate;

    public World(TredecimalDate currentDate) {
        this.currentDate = new ReadOnlyObjectWrapper<>(currentDate);
    }

    public ObservableValue<TredecimalDate> getCurrentDate() {
        return currentDate.getReadOnlyProperty();
    }

    public ObservableList<Person> getLivingPersons() {
        return FXCollections.unmodifiableObservableList(livingPersons);
    }

    public ObservableList<Person> getDeadPersons() {
        return FXCollections.unmodifiableObservableList(deadPersons);
    }

    public ObservableList<Event> getEvents() {
        return FXCollections.unmodifiableObservableList(events);
    }

    @Subscribe
    public void submitEvent(Event event) {
        LOGGER.debug("Received {}", event);
        events.add(event);
        event.apply(this);
    }

    void setDate(TredecimalDate currentDate) {
        this.currentDate.setValue(currentDate);
        ThreadContext.put("date", ISO.format(currentDate));
    }

    void addLivingPerson(Person person) {
        livingPersons.add(person);
    }

    void killPerson(Person person) {
        checkState(livingPersons.contains(person), String.format("%s is not among living persons", person));
        livingPersons.remove(person);
        deadPersons.add(person);
    }
}
