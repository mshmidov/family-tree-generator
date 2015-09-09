package ftg.model.world;

import ftg.model.person.Person;

public final class PersonIntroductionEvent implements Event {

    private final Person person;

    public PersonIntroductionEvent(Person person) {
        this.person = person;
    }

    @Override
    public void apply(World world) {
        world.addLivingPerson(person);
    }
}
