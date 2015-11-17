package ftg.model.relation;

import ftg.model.person.Person;

public final class Marriage extends AbstractRelation implements Relation {

    Marriage(Person husband, Person wife) {
        super(Role.HUSBAND, husband, Role.WIFE, wife);
    }

    public Person getHusband() {
        return getParticipant(Role.HUSBAND);
    }

    public Person getWife() {
        return getParticipant(Role.WIFE);
    }

    public void remove() {
        getHusband().removeRelation(this);
        getWife().removeRelation(this);
    }
}
