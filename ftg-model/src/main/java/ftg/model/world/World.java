package ftg.model.world;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static ftg.model.time.TredecimalDateFormat.ISO;

public final class World {

    private static final Logger LOGGER = LogManager.getLogger(World.class);

    private final Set<Person> livingPersons = new HashSet<>();
    private final List<Person> deadPersons = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();

    private TredecimalDate currentDate;

    public World(TredecimalDate currentDate) {
        submitEvent(new NewDateEvent(currentDate));
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public List<Person> getLivingPersons() {
        return ImmutableList.copyOf(livingPersons);
    }

    public List<Person> getDeadPersons() {
        return ImmutableList.copyOf(deadPersons);
    }

    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    @Subscribe
    public void submitEvent(Event event) {
        LOGGER.debug("Received {}", event);
        events.add(event);
        event.apply(this);
    }

    void setDate(TredecimalDate currentDate) {
        this.currentDate = currentDate;
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
