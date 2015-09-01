package ftg.model.relation;

import ftg.commons.MorePreconditions;
import ftg.model.person.Person;

import java.util.function.Predicate;

public enum Role {
    FATHER(p -> p.getSex() == Person.Sex.MALE),
    MOTHER(p -> p.getSex() == Person.Sex.FEMALE),
    CHILD(p -> true),
    HUSBAND(p -> p.getSex() == Person.Sex.MALE),
    WIFE(p -> p.getSex() == Person.Sex.FEMALE),
    WIDOWER(p -> p.getSex() == Person.Sex.MALE),
    WIDOW(p -> p.getSex() == Person.Sex.FEMALE);

    public final Predicate<Person> acceptable;

    Role(Predicate<Person> acceptable) {
        this.acceptable = acceptable;
    }

    public Person check(Person person) {
        return MorePreconditions.checkedArgument(person, acceptable);
    }
}
