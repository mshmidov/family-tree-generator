package ftg.graph.model.event;

import com.google.common.base.MoreObjects;
import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SurnameConverter;
import ftg.graph.db.TredecimalDateConverter;
import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Surname;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.Objects;

public final class PersonData extends DomainObject {

    @Property
    private String name;

    @Property
    @Convert(SurnameConverter.class)
    private Surname surname;

    @Property
    private Person.Sex sex;

    @Property
    @Convert(TredecimalDateConverter.class)
    private TredecimalDate birthDate;

    @Property
    private String residence;

    public PersonData() {
    }

    PersonData(String id, String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, String residence) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.birthDate = birthDate;
        this.residence = residence;
    }

    public String getName() {
        return name;
    }

    public Surname getSurname() {
        return surname;
    }

    public Person.Sex getSex() {
        return sex;
    }

    public TredecimalDate getBirthDate() {
        return birthDate;
    }

    public String getResidence() {
        return residence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, sex, birthDate, residence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersonData that = (PersonData) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(surname, that.surname) &&
            Objects.equals(sex, that.sex) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(residence, that.residence);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("surname", surname)
            .add("sex", sex)
            .add("birthDate", birthDate)
            .add("residence", residence)
            .toString();
    }
}
