package ftg.model.event;

import com.google.common.base.MoreObjects;
import ftg.model.Identified;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;

import java.util.Objects;

public final class PersonData implements Identified {

    private final String id;

    private final String name;

    private final Surname surname;

    private final Person.Sex sex;

    private final TredecimalDate birthDate;

    private final Residence residence;

    PersonData(String personCounter, String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, Residence residence) {
        this.id = String.format("%s %s %s (%s)", personCounter, name, surname.getForm(sex), TredecimalDateFormat.ISO.format(birthDate));
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.birthDate = birthDate;
        this.residence = residence;
    }

    @Override
    public String getId() {
        return id;
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

    public Residence getResidence() {
        return residence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonData that = (PersonData) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(residence, that.residence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, sex, birthDate, residence);
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
