package ftg.model.world;

import ftg.model.person.PersonData;
import ftg.model.time.TredecimalDate;

public final class PersonIntroductionEvent implements Event {

    private final TredecimalDate date;

    private final PersonData personData;

    public PersonIntroductionEvent(TredecimalDate date, PersonData personData) {
        this.date = date;
        this.personData = personData;
    }

    @Override
    public TredecimalDate getDate() {
        return date;
    }

    public PersonData getPersonData() {
        return personData;
    }

    @Override
    public void apply(World world) {
        world.addPerson(personData.newPerson());
    }
}
