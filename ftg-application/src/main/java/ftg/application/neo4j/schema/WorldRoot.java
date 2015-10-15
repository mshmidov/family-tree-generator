package ftg.application.neo4j.schema;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.time.LocalDateTime;

@NodeEntity(label = "World")
public class WorldRoot {

    @GraphId
    private Long id;

    @Property
    private String generated;

    public WorldRoot() {
        generated = LocalDateTime.now().toString();
    }

    public Long getId() {
        return id;
    }

    public String getGenerated() {
        return generated;
    }
}
