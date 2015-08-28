package ftg.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ftg.model.culture.Culture;
import ftg.model.event.Event;
import ftg.model.person.Person;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public final class World {

    private final Map<Country, Culture> cultures;

    private final Set<Person> livingPersons = new LinkedHashSet<>();

    private final Set<Person> deadPersons = new LinkedHashSet<>();

    private final List<Event> events = new ArrayList<>();

    public World(Map<Country, Culture> cultures) {
        this.cultures = ImmutableMap.copyOf(cultures);
    }

    public Set<Country> getCountries() {
        return cultures.keySet();
    }

    public Culture getCulture(Country key) {
        return cultures.get(key);
    }

    public Set<Person> getLivingPersons() {
        return ImmutableSet.copyOf(livingPersons);
    }

    public Set<Person> getDeadPersons() {
        return ImmutableSet.copyOf(deadPersons);
    }

    public void addlivingPerson(Person person) {
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
        events.add(event);
    }
}
