package ftg.model.world;

import static com.google.common.base.Preconditions.checkArgument;
import static ftg.commons.MorePreconditions.checked;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import ftg.model.event.Event;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.relation.RelationFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class World {

    private final PersonFactory personFactory;

    private final RelationFactory relationFactory;

    private final List<Event> events = new ArrayList<>();

    private final Map<String, Person> persons = new HashMap<>();

    @Inject
    public World(PersonFactory personFactory, RelationFactory relationFactory) {
        this.personFactory = personFactory;
        this.relationFactory = relationFactory;
    }

    public Person getPerson(String id) {
        return checked(persons.get(id), Objects::nonNull, () -> new IllegalArgumentException("No person found by id " + id));
    }

    public Stream<Person> persons() {
        return ImmutableList.copyOf(persons.values()).stream();
    }

    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }


    public void submitEvent(Event event) {
        events.add(event);
        event.apply(this, personFactory, relationFactory);
    }

    public void addPerson(Person person) {
        checkArgument(!persons.containsKey(person.getId()));
        persons.put(person.getId(), person);
    }
}
