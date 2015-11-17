package ftg.model.relation;

import ftg.model.person.Person;

public class Parentage extends AbstractRelation implements Relation {

    Parentage(Person father, Person mother, Person child) {
        super(Role.FATHER, father, Role.MOTHER, mother, Role.CHILD, child);
    }

    public Person getFather() {
        return getParticipant(Role.FATHER);
    }

    public Person getMother() {
        return getParticipant(Role.MOTHER);
    }

    public Person getChild() {
        return getParticipant(Role.CHILD);
    }
}
