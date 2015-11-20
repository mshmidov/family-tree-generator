package ftg.model.person;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.model.person.state.Pregnancy;
import ftg.model.person.state.Residence;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;

import java.util.function.Supplier;

public final class PersonFactory {

    private final Supplier<String> id;

    @Inject
    public PersonFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public Person newPerson(String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, Residence residence) {

        final String personId = String.format("%s %s %s (%s) #%s",
                                              sex.name().charAt(0),
                                              name,
                                              surname.getForm(sex),
                                              TredecimalDateFormat.ISO.format(birthDate),
                                              id.get());

        final Person person = new Person(personId, name, surname, sex, birthDate);
        person.addState(residence);

        return person;
    }

    public Pregnancy newPregnancy(TredecimalDate date, Person father, Person.Sex childSex) {
        return new Pregnancy(date, father, childSex);
    }
}
