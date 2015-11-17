package ftg.model.person;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import ftg.model.Identified;
import ftg.model.relation.Relation;
import ftg.model.state.State;
import ftg.model.time.TredecimalDate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class Person implements Identified {

    public enum Sex {MALE, FEMALE}

    private final String id;

    private final String name;

    private final LinkedList<Surname> surnames = new LinkedList<>();

    private final Sex sex;

    private final TredecimalDate birthDate;

    private final Multimap<Class<? extends Relation>, Relation> relations = HashMultimap.create();

    private final Map<Class<? extends State>, State> states = new HashMap<>();

    Person(String id, String name, Surname surname, Sex sex, TredecimalDate birthDate) {
        this.id = id;
        this.name = name;
        this.surnames.add(surname);
        this.sex = sex;
        this.birthDate = birthDate;
    }

    @Override
    public String getId() {
        return id;
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

    public <S extends State> Optional<S> state(Class<S> stateClass) {
        return Optional.ofNullable(states.get(stateClass)).map(stateClass::cast);
    }

    public <R extends Relation> Stream<R> relations(Class<R> relationClass) {
        return ImmutableList.copyOf(relations.get(relationClass)).stream().map(relationClass::cast);
    }

    public void addState(State state) {
        states.put(state.getClass(), state);
    }

    public void removeState(Class<? extends State> stateClass) {
        states.remove(stateClass);
    }

    public void addRelation(Relation relation) {
        relations.put(relation.getClass(), relation);
    }

    public void removeRelation(Relation relation) {
        relations.remove(relation.getClass(), relation);
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
