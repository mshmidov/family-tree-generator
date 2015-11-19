package ftg.model.person.relation;

import ftg.model.person.Person;
import javaslang.Tuple;
import javaslang.collection.HashMap;

public final class Marriage extends AbstractRelation implements Relation {

    Marriage(Person husband, Person wife) {
        super(HashMap.ofAll(
            Tuple.of(Role.HUSBAND, husband),
            Tuple.of(Role.WIFE, wife)));
    }

    public Person getHusband() {
        return getParticipant(Role.HUSBAND);
    }

    public Person getWife() {
        return getParticipant(Role.WIFE);
    }

    public Person getOther(Person participant) {
        return (getRole(participant) == Role.HUSBAND) ? getWife() : getHusband();
    }

    public void remove() {
        getHusband().removeRelation(this);
        getWife().removeRelation(this);
    }
}
