package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "World")
public class World extends DomainObject {

    @Property
    private String generated;

    @Relationship(type = "POPULATED_BY")
    private Population population;

    @Relationship(type = "CONTAINS")
    private Set<Country> countries = new HashSet<>();

    public World() {

    }

    World(String id, String generated, Population population) {
        super(id);
        this.generated = generated;
        this.population = population;
    }

    public String getGenerated() {
        return generated;
    }

    public Population getPopulation() {
        return population;
    }

    public Set<Country> getCountries() {
        return countries;
    }
}
