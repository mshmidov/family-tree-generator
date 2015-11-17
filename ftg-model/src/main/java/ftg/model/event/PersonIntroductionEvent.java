package ftg.model.event;

import ftg.model.person.PersonFactory;
import ftg.model.relation.RelationFactory;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;

public final class PersonIntroductionEvent extends Event {

    private final TredecimalDate date;

    private final PersonData personData;

    PersonIntroductionEvent(String id, TredecimalDate date, PersonData personData) {
        super(id, date);
        this.date = date;
        this.personData = personData;
    }

    public PersonData getPersonData() {
        return personData;
    }

    @Override
    public void apply(World world, PersonFactory personFactory, RelationFactory relationFactory) {
        world.addPerson(personFactory.newPerson(personData));
    }
}
