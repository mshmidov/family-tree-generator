package ftg.model.person;

import com.google.common.collect.ImmutableList;
import ftg.model.relation.Relations;
import ftg.model.time.TredecimalDate;

import java.util.LinkedList;
import java.util.List;

public final class Person {

    public enum Sex {MALE, FEMALE}

    private final String name;

    private final LinkedList<String> surnames = new LinkedList<>();

    private final Sex sex;

    private final TredecimalDate birthYear;

    private final Relations relations = new Relations();

    public Person(String name, String surname, Sex sex, TredecimalDate birthYear) {
        this.name = name;
        this.surnames.add(surname);
        this.sex = sex;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surnames.getLast();
    }

    public Sex getSex() {
        return sex;
    }

    public List<String> getSurnames() {
        return ImmutableList.copyOf(surnames);
    }

    public TredecimalDate getBirthYear() {
        return birthYear;
    }

    public Relations getRelations() {
        return relations;
    }
}
