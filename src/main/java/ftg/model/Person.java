package ftg.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public final class Person {

    public enum Sex {MALE, FEMALE}

    private boolean alive = true;

    private final String name;

    private final LinkedList<String> surnames = new LinkedList<String>();

    private final Sex sex;

    private final int birthYear;

    private final Optional<Person> father;

    private final Optional<Person> mother;

    private final Set<Person> children = new LinkedHashSet<Person>();

    private Optional<Person> spouse;

    public Person(String name, String surname, Sex sex, int birthYear, Optional<Person> father, Optional<Person> mother) {
        this.name = name;
        this.surnames.add(surname);
        this.sex = sex;
        this.birthYear = birthYear;
        this.father = father;
        this.mother = mother;
    }

    public boolean isAlive() {
        return alive;
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

    public int getBirthYear() {
        return birthYear;
    }

    public Optional<Person> getFather() {
        return father;
    }

    public Optional<Person> getMother() {
        return mother;
    }

    public Set<Person> getChildren() {
        return ImmutableSet.copyOf(children);
    }

    public boolean isMarried() {
        return spouse.isPresent();
    }

    public Optional<Person> getSpouse() {
        return spouse;
    }

    public void setDead() {
        this.alive = false;
    }

    public void marry(Person spouse) {

        if (sex == spouse.sex) {
            throw new IllegalArgumentException("Same sex marriage is not available in this model");
        }

        this.spouse = Optional.of(spouse);
        spouse.marry(this);
        if (sex == Sex.FEMALE) {
            surnames.add(spouse.getSurname());
        }
    }
}
