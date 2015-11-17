package ftg.graph.model.event;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.commons.cdi.Namespace;
import ftg.commons.time.TredecimalDate;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Surname;

import java.util.function.Supplier;

public final class EventFactory {

    private final Supplier<String> id;
    private final String namespace;

    @Inject
    public EventFactory(@Identifier Supplier<String> id, @Namespace String namespace) {
        this.id = id;
        this.namespace = namespace;
    }

    public PersonData newPersonData(String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, String residence) {
        return new PersonData(id.get(), namespace, name, surname, sex, birthDate, residence);
    }

    public PersonIntroductionEvent newPersonIntroductionEvent(TredecimalDate date, PersonData personData) {
        return new PersonIntroductionEvent(id.get(), namespace, date, personData);
    }

    public MarriageEvent newMarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        return new MarriageEvent(id.get(), namespace, date, husbandId, wifeId);
    }

    public ConceptionEvent newConceptionEvent(TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        return new ConceptionEvent(id.get(), namespace, date, fatherId, motherId, childSex);
    }

    public BirthEvent newBirthEvent(TredecimalDate date, PersonData childData, String motherId, String fatherId) {
        return new BirthEvent(id.get(), namespace, date, childData, motherId, fatherId);
    }

    public DeathEvent newDeathEvent(TredecimalDate date, String deceasedId) {
        return new DeathEvent(id.get(), namespace, date, deceasedId);
    }

}
