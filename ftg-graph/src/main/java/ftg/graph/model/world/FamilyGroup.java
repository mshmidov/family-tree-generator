package ftg.graph.model.world;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Family;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public final class FamilyGroup extends DomainObject {

    @Property
    private String name;

    @Relationship(type = "CONTAINS")
    private Set<Family> families = new HashSet<>();

    public FamilyGroup() {
    }

    public FamilyGroup(String id, String namespace, String name) {
        super(id, namespace);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Family> getFamilies() {
        return families;
    }
}
