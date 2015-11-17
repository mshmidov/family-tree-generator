package ftg.model.relation;

import ftg.model.person.Person;

public final class Widowhood extends AbstractRelation implements Relation {

    Widowhood(Person widower, Person widow) {
        super(Role.WIDOWER, widower, Role.WIDOW, widow);
    }
}
