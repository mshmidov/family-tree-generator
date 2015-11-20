package ftg.model.event;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.model.person.Person;
import ftg.model.person.Surname;
import ftg.model.time.TredecimalDate;
import ftg.model.world.country.Country;

import java.util.function.Supplier;

public final class EventFactory {

    private final Supplier<String> id;

    @Inject
    public EventFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public PersonIntroductionEvent newPersonIntroductionEvent(TredecimalDate date, String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, Country country) {
        return new PersonIntroductionEvent(id.get(), date, name, surname, sex, birthDate, country);
    }

    public MarriageEvent newMarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        return new MarriageEvent(id.get(),date, husbandId, wifeId);
    }

    public ConceptionEvent newConceptionEvent(TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        return new ConceptionEvent(id.get(),date, fatherId, motherId, childSex);
    }

    public BirthEvent newBirthEvent(TredecimalDate date, String motherId, String childName, Surname childSurname) {
        return new BirthEvent(id.get(), date, motherId, childName, childSurname);
    }

    public DeathEvent newDeathEvent(TredecimalDate date, String deceasedId) {
        return new DeathEvent(id.get(),date, deceasedId);
    }

}
