package ftg.model.person.relation;

import ftg.commons.MorePreconditions;
import ftg.model.person.Person;
import ftg.model.person.state.Death;

import java.util.function.Predicate;

public enum Role {
    FATHER(p -> p.getSex() == Person.Sex.MALE),
    MOTHER(p -> p.getSex() == Person.Sex.FEMALE),
    CHILD(p -> true),
    HUSBAND(p -> p.getSex() == Person.Sex.MALE),
    WIFE(p -> p.getSex() == Person.Sex.FEMALE),
    DECEASED(p -> p.state(Death.class).isDefined()),
    WIDOW(p -> p.state(Death.class).isEmpty());

    public final Predicate<Person> acceptable;

    Role(Predicate<Person> acceptable) {
        this.acceptable = acceptable;
    }

    public Person check(Person person) {
        return MorePreconditions.checkedArgument(person, acceptable);
    }
}
