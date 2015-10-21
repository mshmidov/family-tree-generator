package ftg.graph.db;

import ftg.commons.time.TredecimalDate;
import org.neo4j.ogm.typeconversion.AttributeConverter;

public final class TredecimalDateConverter implements AttributeConverter<TredecimalDate, Number> {

    @Override public Number toGraphProperty(TredecimalDate value) {
        return (value == null) ? null : value.getDayOfEpoch();
    }

    @Override public TredecimalDate toEntityAttribute(Number value) {
        return (value == null) ? null : new TredecimalDate(value.longValue());
    }
}
