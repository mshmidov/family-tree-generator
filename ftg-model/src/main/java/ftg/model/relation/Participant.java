package ftg.model.relation;

import ftg.model.person.Person;

public final class Participant {

    private final Person person;

    private final Role role;

    public Participant(Person person, Role role) {
        this.person = person;
        this.role = role;
    }

    public Person getPerson() {
        return person;
    }

    public Role getRole() {
        return role;
    }
}
