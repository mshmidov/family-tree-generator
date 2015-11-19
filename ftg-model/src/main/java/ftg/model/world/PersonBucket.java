package ftg.model.world;

import static ftg.model.person.PersonUtil.ALIVE;
import static ftg.model.person.PersonUtil.FEMALE;
import static ftg.model.person.PersonUtil.MALE;
import static ftg.model.person.PersonUtil.MARRIED;
import static ftg.model.person.PersonUtil.PREGNANT;

import ftg.model.person.Person;
import javaslang.control.Option;

import java.util.Comparator;
import java.util.function.Predicate;

public enum PersonBucket {

    ALL_LIVING(ALIVE, Option.none()),
    LIVING_MALES(ALIVE.and(MALE), Option.none()),
    LIVING_FEMALES(ALIVE.and(FEMALE), Option.none()),
    SINGLE_MALES(ALIVE.and(MALE).and(MARRIED.negate()), Option.none()),
    SINGLE_FEMALES(ALIVE.and(FEMALE).and(MARRIED.negate()), Option.of(Comparator.comparing(Person::getBirthDate))),
    MARRIED_MALES(ALIVE.and(MALE).and(MARRIED), Option.none()),
    MARRIED_NON_PREGNANT_FEMALES(ALIVE.and(FEMALE).and(MARRIED).and(PREGNANT.negate()), Option.none()),
    MARRIED_PREGNANT_FEMALES(ALIVE.and(FEMALE).and(MARRIED).and(PREGNANT), Option.none());

    public final Predicate<Person> criteria;

    public final Option<Comparator<Person>> orderBy;

    PersonBucket(Predicate<Person> criteria, Option<Comparator<Person>> orderBy) {
        this.criteria = criteria;
        this.orderBy = orderBy;
    }

}


