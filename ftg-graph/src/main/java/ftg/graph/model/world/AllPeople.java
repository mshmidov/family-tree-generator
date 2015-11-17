package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public final class AllPeople extends DomainObject {

    @Property
    private String label = "All People";

    @Relationship
    private Set<Person> persons = new HashSet<>();

    public AllPeople() {
    }

    public AllPeople(String id, String namespace) {
        super(id, namespace);
    }

    public void addPerson(Person person) {
        persons.add(person);
    }
}
