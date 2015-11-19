package ftg.model.person.relation;

import ftg.model.person.Person;
import javaslang.Tuple;
import javaslang.collection.HashMap;

public class Parentage extends AbstractRelation implements Relation {

    Parentage(Person father, Person mother, Person child) {
        super(HashMap.ofAll(
            Tuple.of(Role.FATHER, father),
            Tuple.of(Role.MOTHER, mother),
            Tuple.of(Role.CHILD, child)));
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
