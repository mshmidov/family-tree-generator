package ftg.graph.model.person;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.model.event.PersonData;

import java.util.function.Supplier;

public final class PersonFactory {

    private final Supplier<String> id;

    @Inject
    public PersonFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public Person newPerson(PersonData personData) {
        return (personData.getSex() == Person.Sex.MALE)
               ? new Man(newPersonId(personData), personData.getName(), personData.getSurname(), personData.getBirthDate())
               : new Woman(newPersonId(personData), personData.getName(), personData.getSurname(), personData.getBirthDate());
    }

    public Pregnancy newPregnancy(TredecimalDate date, Man father, Person.Sex childSex) {
        return new Pregnancy(id.get(), date, father, childSex);
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
