package ftg.graph.model.event;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonFactory;

public class PersonIntroductionEvent extends Event {

    private PersonData personData;

    public PersonIntroductionEvent() {
    }

    PersonIntroductionEvent(String id, TredecimalDate date, PersonData personData) {
        super(id, date);
        this.personData = personData;
    }

    @Override
    public void apply(SimulatedWorld world, PersonFactory personFactory) {
        final Person person = personFactory.newPerson(personData);
        person.setCountryOfResidence(world.getQueries().getCountry(personData.getResidence()));
        world.getOperations().createPerson(person);
    }
}
