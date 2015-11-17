package ftg.model.event;

import ftg.model.person.PersonFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;

public final class PersonIntroductionEvent implements Event {

    private final String id;

    private final TredecimalDate date;

    private final PersonData personData;

    PersonIntroductionEvent(String id, TredecimalDate date, PersonData personData) {
        this.id = id;
        this.date = date;
        this.personData = personData;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
    }

    public PersonData getPersonData() {
        return personData;
    }

    @Override
    public void apply(World world, PersonFactory personFactory) {
        world.addPerson(personFactory.newPerson(personData));
    }
}
