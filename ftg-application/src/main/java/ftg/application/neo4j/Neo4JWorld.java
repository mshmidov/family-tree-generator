package ftg.application.neo4j;

import com.google.inject.Inject;
import ftg.application.neo4j.schema.WorldRoot;
import ftg.model.person.Person;
import ftg.model.world.Event;
import ftg.model.world.World;

import java.util.List;
import java.util.Set;

public final class Neo4JWorld implements World {

    private final Neo4jRepository repository;
    private final WorldRoot worldRoot;

    @Inject
    public Neo4JWorld(Neo4jRepository repository) {
        this.repository = repository;
        this.worldRoot = repository.createWorldRoot();
    }

    @Override
    public void submitEvent(Event event) {
        // store event in db
        event.apply(this);
    }

    @Override
    public void addPerson(Person person) {

    }

    @Override
    public Person getPerson(String id) {
        return null;
    }

    @Override
    public List<Person> getPersons() {
        return null;
    }

    @Override
    public List<Event> getEvents() {
        return null;
    }

    @Override
    public Set<Person> getLivingPersons() {
        return null;
    }

    @Override
    public Set<Person> getLivingMales() {
        return null;
    }

    @Override
    public Set<Person> getLivingFemales() {
        return null;
    }

    @Override
    public Set<Person> getDeadPersons() {
        return null;
    }
}
