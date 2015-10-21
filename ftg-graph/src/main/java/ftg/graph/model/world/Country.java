package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

public class Country extends DomainObject {

    @Property
    private String name;

    @Relationship(type = "LIVES_IN", direction = Relationship.INCOMING)
    private Set<Person> population = new HashSet<>();

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Person> getPopulation() {
        return population;
    }
}
