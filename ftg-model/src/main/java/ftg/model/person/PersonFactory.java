package ftg.model.person;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.model.event.PersonData;
import ftg.model.person.state.Pregnancy;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateFormat;

import java.util.function.Supplier;

public final class PersonFactory {

    private final Supplier<String> id;

    @Inject
    public PersonFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public Person newPerson(PersonData personData) {
        final Person person =
            new Person(newPersonId(personData), personData.getName(), personData.getSurname(), personData.getSex(), personData.getBirthDate());
        person.addState(personData.getResidence());
        return person;
    }

    public Pregnancy newPregnancy(TredecimalDate date, Person father, Person.Sex childSex) {
        return new Pregnancy(date, father, childSex);
    }

    private String newPersonId(PersonData personData) {
        return String.format("%s %s %s (%s) #%s",
                             personData.getSex().name().charAt(0),
                             personData.getName(),
                             personData.getSurname().getForm(personData.getSex()),
                             TredecimalDateFormat.ISO.format(personData.getBirthDate()),
                             id.get());
    }
}
