package ftg.graph.model.state;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.person.Person;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public final class Pregnant extends State {

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate conceptionDate;

    @Relationship(type = "FROM")
    private Person father;

    @Property
    private Person.Sex childSex;

    public Pregnant() {
    }

    public Pregnant(TredecimalDate conceptionDate, Person father, Person.Sex childSex) {
        this.conceptionDate = conceptionDate;
        this.father = father;
        this.childSex = childSex;
    }

    public TredecimalDate getConceptionDate() {
        return conceptionDate;
    }

    public Person getFather() {
        return father;
    }

    public Person.Sex getChildSex() {
        return childSex;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conceptionDate", conceptionDate)
                .add("father", father)
                .add("childSex", childSex)
                .toString();
    }
}
