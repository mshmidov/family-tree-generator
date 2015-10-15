package ftg.model.world;

import static com.google.common.base.Preconditions.checkArgument;
import static ftg.commons.MorePreconditions.checked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ftg.model.person.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class LocalWorld implements World {

    private final Map<String, Person> persons = new HashMap<>();

    private final List<Event> events = new ArrayList<>();

    private final Set<Person> livingPersons = new HashSet<>();

    private final Set<Person> livingMales = new HashSet<>();

    private final Set<Person> livingFemales = new HashSet<>();

    private final Set<Person> deadPersons = new HashSet<>();

    public LocalWorld() {
    }

    public LocalWorld(World other) {
        other.getEvents().forEach(this::submitEvent);
    }

    @Override
    public Person getPerson(String id) {
        return checked(persons.get(id), Objects::nonNull, () -> new IllegalArgumentException("No person found by id " + id));
    }

    @Override
    public List<Person> getPersons() {
        return ImmutableList.copyOf(persons.values());
    }

    @Override
    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    @Override
    public Set<Person> getLivingPersons() {
        return ImmutableSet.copyOf(livingPersons);
    }

    @Override
    public Set<Person> getLivingMales() {
        return ImmutableSet.copyOf(livingMales);
    }

    @Override
    public Set<Person> getLivingFemales() {
        return ImmutableSet.copyOf(livingFemales);
    }

    @Override
    public Set<Person> getDeadPersons() {
        return ImmutableSet.copyOf(deadPersons);
    }

    @Override
    public void submitEvent(Event event) {
        events.add(event);
        event.apply(this);

        if (event instanceof BirthEvent) {
            final String id = ((BirthEvent) event).getChildData().getId();
            final Person person = persons.get(id);
            livingPersons.add(person);
            livingBucket(person.getSex()).add(person);

        } else if (event instanceof PersonIntroductionEvent) {
            final String id = ((PersonIntroductionEvent) event).getPersonData().getId();
            final Person person = persons.get(id);
            livingPersons.add(person);
            livingBucket(person.getSex()).add(person);

        } else if (event instanceof DeathEvent) {
            final String id = ((DeathEvent) event).getDeceasedId();
            final Person person = persons.get(id);
            livingPersons.remove(person);
            livingBucket(person.getSex()).remove(person);
            deadPersons.add(person);
        }
    }

    @Override
    public void addPerson(Person person) {
        checkArgument(!persons.containsKey(person.getId()));
        persons.put(person.getId(), person);
    }

    private Set<Person> livingBucket(Person.Sex sex) {
        return (sex == Person.Sex.MALE) ? livingMales : livingFemales;
    }
}
