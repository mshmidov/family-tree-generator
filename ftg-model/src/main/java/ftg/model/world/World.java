package ftg.model.world;

import static ftg.commons.functional.BooleanLogic.not;

import ftg.commons.Bucket;
import ftg.commons.functional.Checked;
import ftg.model.event.Event;
import ftg.model.person.Person;
import ftg.model.person.PersonFactory;
import ftg.model.person.relation.RelationFactory;
import ftg.model.world.country.Country;
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

    private final Set<Country> countries;
    private final PersonFactory personFactory;
    private final RelationFactory relationFactory;

    private Vector<Event> events = Vector.empty();
    private Map<String, Person> persons = HashMap.empty();


    public World(Set<Country> countries, PersonFactory personFactory, RelationFactory relationFactory) {
        this.countries = countries;
        this.personFactory = personFactory;
        this.relationFactory = relationFactory;
    }

    public Set<Country> countries() {
        return countries;
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

    public <T> T submitEvent(Event<T> event) {
        events = events.append(event);
        return event.apply(this, personFactory, relationFactory);
    }

    public Person addPerson(Person person) {
        persons = persons.put(
            Checked.argument(person.getId(), not(persons::containsKey), "Person %s is already added"),
            person);

        buckets.values().forEach(bucket -> bucket.update(person));
        person.addListener(this::changePerson);

        return person;
    }

    public void changePerson(Person person) {
        buckets.values().forEach(bucket -> bucket.update(person));
    }
}
