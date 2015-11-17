package ftg.model.world;

import static com.google.common.base.Preconditions.checkArgument;
import static ftg.commons.MorePreconditions.checked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import ftg.model.event.BirthEvent;
import ftg.model.event.DeathEvent;
import ftg.model.event.Event;
import ftg.model.event.PersonIntroductionEvent;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.relation.RelationFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class World {

    private final PersonFactory personFactory;

    private final RelationFactory relationFactory;

    private final Map<String, Person> persons = new HashMap<>();

    private final List<Event> events = new ArrayList<>();

    private final Set<Person> livingPersons = new HashSet<>();

    private final Set<Person> livingMales = new HashSet<>();

    private final Set<Person> livingFemales = new HashSet<>();

    private final Set<Person> deadPersons = new HashSet<>();

    @Inject
    public World(PersonFactory personFactory, RelationFactory relationFactory) {
        this.personFactory = personFactory;
        this.relationFactory = relationFactory;
    }

    public Person getPerson(String id) {
        return checked(persons.get(id), Objects::nonNull, () -> new IllegalArgumentException("No person found by id " + id));
    }

    public List<Person> getPersons() {
        return ImmutableList.copyOf(persons.values());
    }

    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    public Set<Person> getLivingPersons() {
        return ImmutableSet.copyOf(livingPersons);
    }

    public Set<Person> getLivingMales() {
        return ImmutableSet.copyOf(livingMales);
    }

    public Set<Person> getLivingFemales() {
        return ImmutableSet.copyOf(livingFemales);
    }

    public Set<Person> getDeadPersons() {
        return ImmutableSet.copyOf(deadPersons);
    }

    public void submitEvent(Event event) {
        events.add(event);
        event.apply(this, personFactory, relationFactory);

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

    public void addPerson(Person person) {
        checkArgument(!persons.containsKey(person.getId()));
        persons.put(person.getId(), person);
    }

    private Set<Person> livingBucket(Person.Sex sex) {
        return (sex == Person.Sex.MALE) ? livingMales : livingFemales;
    }
}
