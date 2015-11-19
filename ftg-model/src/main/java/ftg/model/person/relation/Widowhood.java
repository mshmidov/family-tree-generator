package ftg.model.person.relation;

import ftg.model.person.Person;
import javaslang.Tuple;
import javaslang.collection.HashMap;

public final class Widowhood extends AbstractRelation implements Relation {

    Widowhood(Person deceased, Person widow) {
        super(HashMap.ofAll(
            Tuple.of(Role.DECEASED, deceased),
            Tuple.of(Role.WIDOW, widow)));
    }
}
