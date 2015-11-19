package ftg.model.world;

import static ftg.commons.functional.BooleanLogic.not;

import com.google.inject.Inject;
import ftg.commons.Bucket;
import ftg.commons.functional.Checked;
import ftg.model.event.Event;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.RelationFactory;
import javaslang.Tuple;
import javaslang.collection.Array;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Set;
import javaslang.collection.Stream;
import javaslang.collection.Vector;

public final class World {

    private final HashMap<PersonBucket, Bucket<Person>> buckets = HashMap.ofAll(
        Array.ofAll(PersonBucket.values()).map(personBucket -> Tuple.of(personBucket, new Bucket<>(personBucket.criteria, personBucket.orderBy))));

    private final PersonFactory personFactory;
    private final RelationFactory relationFactory;

    private Vector<Event> events = Vector.empty();
    private Map<String, Person> persons = HashMap.empty();

    @Inject
    public World(PersonFactory personFactory, RelationFactory relationFactory) {
        this.personFactory = personFactory;
        this.relationFactory = relationFactory;

    }

    public Seq<Event> events() {
        return events;
    }

    public Stream<Person> persons() {
        return Stream.ofAll(persons.values());
    }

    public Set<Person> persons(PersonBucket fromBucket) {
        return buckets.get(fromBucket).get().values();
    }

    public Person getPerson(String id) {
        return persons.get(id).orElseThrow(() -> new IllegalArgumentException("No person found by id " + id));
    }

    public void submitEvent(Event event) {
        events = events.append(event);
        event.apply(this, personFactory, relationFactory);
    }

    public void addPerson(Person person) {
        persons = persons.put(
            Checked.argument(person.getId(), not(persons::containsKey), "Person %s is already added"),
            person);

        buckets.values().forEach(bucket -> bucket.update(person));
        person.addListener(this::changePerson);
    }

    public void changePerson(Person person) {
        buckets.values().forEach(bucket -> bucket.update(person));
    }
}
