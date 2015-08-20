package ftg.model.relation;

import ftg.model.Person;

public class Parentage extends AbstractRelation implements Relation {

    public Parentage create(Person father, Person mother, Person child) {
        final Parentage parentage = new Parentage(father, mother, child);
        father.getRelations().add(parentage);
        mother.getRelations().add(parentage);
        child.getRelations().add(parentage);
        return parentage;
    }

    public Parentage(Person father, Person mother, Person child) {
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
