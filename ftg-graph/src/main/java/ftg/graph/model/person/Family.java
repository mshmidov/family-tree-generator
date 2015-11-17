package ftg.graph.model.person;

import ftg.graph.db.SurnameConverter;
import ftg.graph.model.DomainObject;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Objects;

@NodeEntity
public final class Family extends DomainObject {

    @Property
    @Convert(SurnameConverter.class)
    private Surname surname;

    public Family() {
    }

    public Family(String namespace, Surname surname) {
        super(surname.getMaleForm(), namespace);
        this.surname = surname;
    }

    public Surname getSurname() {
        return surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Family family = (Family) o;
        return Objects.equals(surname, family.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname);
    }
}
