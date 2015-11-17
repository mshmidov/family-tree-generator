package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "World")
public class World extends DomainObject {

    @Property
    private String generated;

    @Relationship(type = "CONTAINS")
    private Population population;

    @Relationship(type = "CONTAINS")
    private Geography geography;

    public World() {

    }

    World(String id, String generated, Population population, Geography geography) {
        super(id, id);
        this.generated = generated;
        this.population = population;
        this.geography = geography;
    }

    public String getGenerated() {
        return generated;
    }

    public Population getPopulation() {
        return population;
    }

    public Geography getGeography() {
        return geography;
    }
}
