package ftg.model.relation;

import ftg.model.person.Person;

public final class Widowhood extends AbstractRelation implements Relation {

    public static Widowhood create(Person widower, Person widow) {
        final Widowhood widowhood = new Widowhood(widower, widow);

        widower.getRelations().add(widowhood);
        widow.getRelations().add(widowhood);

        return widowhood;
    }

    public Widowhood(Person widower, Person widow) {
        super(Role.WIDOWER, widower, Role.WIDOW, widow);
    }
}
