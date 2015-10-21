package ftg.graph.model.person;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public final class Pregnancy extends DomainObject {

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate conceptionDate;

    @Relationship(type = "BEGOTTEN")
    private Person father;

    @Property
    private Person.Sex childSex;

    public Pregnancy() {
    }

    public Pregnancy(TredecimalDate conceptionDate, Person father, Person.Sex childSex) {
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
