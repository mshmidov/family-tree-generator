package ftg.model.event;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.state.Residence;
import ftg.model.time.TredecimalDate;

import java.util.function.Supplier;

public final class EventFactory {

    private final Supplier<String> id;

    @Inject
    public EventFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public PersonData newPersonData(String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, Residence residence) {
        return new PersonData(id.get(), name, surname, sex, birthDate, residence);
    }

    public PersonIntroductionEvent newPersonIntroductionEvent(TredecimalDate date, PersonData personData) {
        return new PersonIntroductionEvent(id.get(), date, personData);
    }

    public MarriageEvent newMarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        return new MarriageEvent(id.get(),date, husbandId, wifeId);
    }

    public ConceptionEvent newConceptionEvent(TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        return new ConceptionEvent(id.get(),date, fatherId, motherId, childSex);
    }

    public BirthEvent newBirthEvent(TredecimalDate date, PersonData childData, String motherId, String fatherId) {
        return new BirthEvent(id.get(),childData, motherId, fatherId);
    }

    public DeathEvent newDeathEvent(TredecimalDate date, String deceasedId) {
        return new DeathEvent(id.get(),date, deceasedId);
    }

}
