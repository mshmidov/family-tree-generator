package ftg.graph.model.event;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public abstract class Event extends DomainObject {

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate date;

    public abstract void apply(SimulatedWorld simulatedWorld);

    public Event() {
    }

    public Event(TredecimalDate date) {
        this.date = date;
    }

    public final TredecimalDate getDate() {
        return date;
    }

}
