package ftg.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ftg.model.event.Event;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static ftg.model.time.TredecimalDateFormat.ISO;

public final class World {

    private static final Logger LOGGER = LogManager.getLogger(World.class);

    private final TredecimalDate originDate;

    private TredecimalDate currentDate;

    private final Set<Person> livingPersons = new LinkedHashSet<>();

    private final Set<Person> deadPersons = new LinkedHashSet<>();

    private final List<Event> events = new ArrayList<>();

    public World(TredecimalDate currentDate) {
        this.originDate = currentDate;
        this.currentDate = currentDate;
    }

    public TredecimalDate getOriginDate() {
        return originDate;
    }

    public TredecimalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(TredecimalDate currentDate) {
        this.currentDate = currentDate;
        ThreadContext.put("date", ISO.format(currentDate));
    }

    public Set<Person> getLivingPersons() {
        return ImmutableSet.copyOf(livingPersons);
    }

    public Set<Person> getDeadPersons() {
        return ImmutableSet.copyOf(deadPersons);
    }

    public void addLivingPerson(Person person) {
        livingPersons.add(person);
    }

    public void killPerson(Person person) {
        checkState(livingPersons.contains(person), String.format("%s is not among living persons", person));
        livingPersons.remove(person);
        deadPersons.add(person);
    }

    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    public void submitEvent(Event event) {
        LOGGER.debug("Adding event {}", event);
        events.add(event);
        event.apply(this);
    }
}
