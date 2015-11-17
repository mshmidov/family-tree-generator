package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Country extends DomainObject {

    @Property
    private String name;

    @Relationship(type = "LIVES_IN", direction = Relationship.INCOMING)
    private Set<Person> population = new HashSet<>();

    public Country() {
    }

    Country(String id, String namespace, String name) {
        super(id, namespace);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Person> getPopulation() {
        return population;
    }
}
