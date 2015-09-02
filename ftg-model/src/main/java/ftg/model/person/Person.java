package ftg.model.person;

import com.google.common.collect.ImmutableList;
import ftg.model.relation.Relation;
import ftg.model.state.State;
import ftg.model.time.TredecimalDate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public final class Person {

    public enum Sex {MALE, FEMALE}

    private final String name;

    private final LinkedList<Surname> surnames = new LinkedList<>();

    private final Sex sex;

    private final TredecimalDate birthDate;

    private final Relations relations = new Relations();

    private final Map<Class<? extends State>, State> states = new HashMap<>();

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

    public boolean hasState(Class<? extends State> stateClass) {
        return states.containsKey(stateClass);
    }

    public void addState(State state) {
        states.put(state.getClass(), state);
    }

    public <S extends State> S getState(Class<S> stateClass) {
        checkState(hasState(stateClass));
        return stateClass.cast(states.get(stateClass));
    }

    public void removeState(Class<? extends State> stateClass) {
        states.remove(stateClass);
    }

    public Relations getRelations() {
        return relations;
    }

    public <R extends Relation> boolean hasRelation(Class<R> relationClass) {
        return relations.contains(relationClass);
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
