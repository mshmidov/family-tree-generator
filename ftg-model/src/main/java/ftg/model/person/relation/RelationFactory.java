package ftg.model.person.relation;

import ftg.model.person.Person;

public final class RelationFactory {

    public Marriage createMarriage(Person husband, Person wife) {
        final Marriage marriage = new Marriage(husband, wife);
        husband.addRelation(marriage);
        wife.addRelation(marriage);
        return marriage;
    }

    public Parentage createParentage(Person father, Person mother, Person child) {
        final Parentage parentage = new Parentage(father, mother, child);
        father.addRelation(parentage);
        mother.addRelation(parentage);
        child.addRelation(parentage);
        return parentage;
    }

    public Widowhood createWidowhood(Person deceased, Person widow) {
        final Widowhood widowhood = new Widowhood(deceased, widow);

        deceased.addRelation(widowhood);
        widow.addRelation(widowhood);

        return widowhood;
    }

}
