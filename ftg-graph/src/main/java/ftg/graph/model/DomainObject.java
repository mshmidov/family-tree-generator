package ftg.graph.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;


@NodeEntity
public abstract class DomainObject {

    @GraphId
    private Long graphId;

    @Property
    private String id;

    @Property
    private String namespace;

    public DomainObject() {
    }

    protected DomainObject(String id, String namespace) {
        this.id = id;
        this.namespace = namespace;
    }

    public final Long getGraphId() {
        return graphId;
    }

    public final String getId() {
        return id;
    }

    public final String getNamespace() {
        return namespace;
    }
}
