package ftg.model.relation;

import ftg.model.person.Person;

public final class Widowhood extends AbstractRelation implements Relation {

    Widowhood(Person deceased, Person widow) {
        super(Role.DECEASED, deceased, Role.WIDOW, widow);
    }
}
