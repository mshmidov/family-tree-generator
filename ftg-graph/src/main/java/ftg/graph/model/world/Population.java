package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

public class Population extends DomainObject {

    @Relationship(type = "CONTAINS")
    private Set<Person> persons = new HashSet<>();

    public Population() {
    }

    Population(String id, Set<Person> persons) {
        super(id);
        this.persons = persons;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public void addPerson(Person person) {
        persons.add(person);
    }
}
