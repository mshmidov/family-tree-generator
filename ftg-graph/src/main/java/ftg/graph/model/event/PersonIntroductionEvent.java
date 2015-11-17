package ftg.graph.model.event;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Family;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonFactory;

public class PersonIntroductionEvent extends Event<Person> {

    private PersonData personData;

    public PersonIntroductionEvent() {
    }

    PersonIntroductionEvent(String id, String namespace, TredecimalDate date, PersonData personData) {
        super(id, namespace, date);
        this.personData = personData;
    }

    @Override
    public Person apply(SimulatedWorld world, PersonFactory personFactory) {
        final Family family = world.getOperations().getOrCreateFamily(personData.getSurname());
        final Person person = personFactory.newPerson(personData, family);
        person.setCountryOfResidence(world.getQueries().getCountry(personData.getResidence()));
        return world.getOperations().createPerson(person);
    }
}
