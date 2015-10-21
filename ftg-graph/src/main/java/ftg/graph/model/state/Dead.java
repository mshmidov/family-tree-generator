package ftg.graph.model.state;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.TredecimalDateConverter;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public final class Dead extends State {

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate date;

    public Dead() {
    }

    public Dead(TredecimalDate date) {
        this.date = date;
    }

    public TredecimalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("date", date)
                .toString();
    }
}
