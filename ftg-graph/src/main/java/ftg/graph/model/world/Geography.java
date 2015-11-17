package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public final class Geography extends DomainObject {

    @Property
    private String label = "Geography";

    @Relationship(type = "INCLUDES")
    private Set<Country> countries = new HashSet<>();

    public Geography() {
    }

    public Geography(String id, String namespace) {
        super(id, namespace);
    }

    public Set<Country> getCountries() {
        return countries;
    }
}
