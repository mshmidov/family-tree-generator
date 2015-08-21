package ftg.model.relation;

import ftg.model.person.Person;

public final class Marriage extends AbstractRelation implements Relation {

    public static Marriage create(Person husband, Person wife) {
        final Marriage marriage = new Marriage(husband, wife);
        husband.getRelations().add(marriage);
        wife.getRelations().add(marriage);
        return marriage;
    }

    public Marriage(Person husband, Person wife) {
        super(Role.HUSBAND, husband, Role.WIFE, wife);
    }

    public Person getHusband() {
        return getParticipant(Role.HUSBAND);
    }

    public Person getWife() {
        return getParticipant(Role.WIFE);
    }
}
