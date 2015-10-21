package ftg.graph.model;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.commons.time.TredecimalDate;
import ftg.commons.time.TredecimalDateFormat;
import ftg.graph.model.event.BirthEvent;
import ftg.graph.model.event.ConceptionEvent;
import ftg.graph.model.event.DeathEvent;
import ftg.graph.model.event.MarriageEvent;
import ftg.graph.model.event.PersonIntroductionEvent;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonData;
import ftg.graph.model.person.Pregnancy;
import ftg.graph.model.person.Surname;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.graph.model.world.Population;
import ftg.graph.model.world.World;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public final class DomainObjectFactory {

    private final Supplier<String> id;

    @Inject
    public DomainObjectFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public PersonIntroductionEvent newPersonIntroductionEvent(TredecimalDate date, PersonData personData) {
        return withId(new PersonIntroductionEvent(date, personData));
    }

    public MarriageEvent newMarriageEvent(TredecimalDate date, String husbandId, String wifeId) {
        return withId(new MarriageEvent(date, husbandId, wifeId));
    }

    public ConceptionEvent newConceptionEvent(TredecimalDate date, String fatherId, String motherId, Person.Sex childSex) {
        return withId(new ConceptionEvent(date, fatherId, motherId, childSex));
    }

    public BirthEvent newBirthEvent(TredecimalDate date, PersonData childData, String motherId, String fatherId) {
        return withId(new BirthEvent(date, childData, motherId, fatherId));
    }

    public DeathEvent newDeathEvent(TredecimalDate date, String deceasedId) {
        return withId(new DeathEvent(date, deceasedId));
    }

    public World newWorld() {
        final Population population = withId(new Population());

        return withId(new World(LocalDateTime.now().toString(), population));
    }

    public Country newCountry(String name) {
        return withId(new Country(name));
    }

    public PersonData newPersonData(String name, Surname surname, Person.Sex sex, TredecimalDate birthDate, String residence) {
        return withId(new PersonData(name, surname, sex, birthDate, residence));
    }

    public Person newPerson(PersonData personData) {
        final Person person = (personData.getSex() == Person.Sex.MALE)
                              ? new Man(personData.getName(), personData.getSurname(), personData.getBirthDate())
                              : new Woman(personData.getName(), personData.getSurname(), personData.getBirthDate());

        return withId(person, newPersonId(person));
    }

    public Pregnancy newPregnancy(TredecimalDate date, Man father, Person.Sex childSex) {
        return withId(new Pregnancy(date, father, childSex));
    }

    private <T extends DomainObject> T withId(T domainObject) {
        domainObject.setId(id.get());
        return domainObject;
    }

    private <T extends DomainObject> T withId(T domainObject, String customId) {
        domainObject.setId(customId);
        return domainObject;
    }

    private String newPersonId(Person person) {
        return String.format("%s %s %s (%s) #%s",
            person.getSex().name().charAt(0),
            person.getName(),
            person.getSurname().getForm(person.getSex()),
            TredecimalDateFormat.ISO.format(person.getBirthDate()),
            id.get());
    }
}
