package ftg.model.person;

import ftg.model.person.relation.Marriage;
import ftg.model.person.state.Death;
import ftg.model.person.state.Pregnancy;

import java.util.function.Predicate;

public final class PersonUtil {

    public static final Predicate<Person> ALIVE = person -> person.state(Death.class).isEmpty();
    public static final Predicate<Person> MALE = person -> person.getSex() == Person.Sex.MALE;
    public static final Predicate<Person> FEMALE = person -> person.getSex() == Person.Sex.FEMALE;
    public static final Predicate<Person> MARRIED = person -> person.relations(Marriage.class).isDefined();
    public static final Predicate<Person> PREGNANT = person -> person.state(Pregnancy.class).isDefined();

    private PersonUtil() {

    }
}
