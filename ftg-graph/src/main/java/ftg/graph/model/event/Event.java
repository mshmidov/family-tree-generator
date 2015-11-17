package ftg.graph.model.event;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.DomainObject;
import ftg.graph.model.person.PersonFactory;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

@NodeEntity
public abstract class Event<T> extends DomainObject {

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate date;

    public abstract T apply(SimulatedWorld simulatedWorld, PersonFactory personFactory);

    public Event() {
    }

    Event(String id, String namespace, TredecimalDate date) {
        super(id, namespace);
        this.date = date;
    }

    public final TredecimalDate getDate() {
        return date;
    }

}
