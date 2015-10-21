package ftg.graph.model.event;

import ftg.commons.time.TredecimalDate;
import ftg.graph.db.SimulatedWorld;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.PersonData;

public class PersonIntroductionEvent extends Event {

    private PersonData personData;

    public PersonIntroductionEvent() {
    }

    public PersonIntroductionEvent(TredecimalDate date, PersonData personData) {
        super(date);
        this.personData = personData;
    }

    @Override
    public void apply(SimulatedWorld world) {
        final Person person = world.getFactory().newPerson(personData);
        person.setCountryOfResidence(world.getQueries().getCountry(personData.getResidence()));
        world.getOperations().createPerson(person);
    }
}
