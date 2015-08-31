package ftg.model.person;

import com.google.common.collect.ImmutableList;
import ftg.model.culture.surname.Surname;
import ftg.model.relation.Relation;
import ftg.model.relation.Relations;
import ftg.model.state.State;
import ftg.model.state.States;
import ftg.model.time.TredecimalDate;

import java.util.LinkedList;
import java.util.List;

public final class Person {

    public enum Sex {MALE, FEMALE}

    private final String name;

    private final LinkedList<Surname> surnames = new LinkedList<>();

    private final Sex sex;

    private final TredecimalDate birthDate;

    private final Relations relations = new Relations();

    private final States states = new States();

    public Person(String name, Surname surname, Sex sex, TredecimalDate birthDate) {
        this.name = name;
        this.surnames.add(surname);
        this.sex = sex;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return getFullSurname();
    }

    public Surname getSurnameObject() {
        return surnames.getLast();
    }

    public void setSurname(Surname surname) {
        surnames.add(surname);
    }

    public Sex getSex() {
        return sex;
    }

    public List<Surname> getSurnames() {
        return ImmutableList.copyOf(surnames);
    }

    public TredecimalDate getBirthDate() {
        return birthDate;
    }

    public Relations getRelations() {
        return relations;
    }

    public States getStates() {
        return states;
    }

    public <R extends Relation> boolean hasRelation(Class<R> relationClass) {
        return relations.contains(relationClass);
    }

    public <S extends State> boolean hasState(Class<S> stateClass) {
        return getStates().contains(stateClass);
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, getFullSurname());
    }

    private String getFullSurname() {
        return (surnames.size() > 1 && sex == Sex.FEMALE)
               ? String.format("%s (%s)", surnames.getLast().getFemaleForm(), surnames.getFirst().getFemaleForm())
               : surnames.getLast().getForm(sex);
    }
}
