package ftg.model.person;

import ftg.model.Identified;
import ftg.model.person.relation.Relation;
import ftg.model.person.state.State;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateInterval;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Option;

import java.util.function.Consumer;

public final class Person implements Identified {

    public enum Sex {MALE, FEMALE}


    private final String id;
    private final TredecimalDate birthDate;
    private final Sex sex;
    private final String name;

    private Set<Consumer<Person>> listeners = HashSet.empty();

    private List<Surname> surnames;
    private Map<Class<? extends State>, State> states = HashMap.empty();
    private Map<Class<? extends Relation>, Set<Relation>> relations = HashMap.empty();

    Person(String id, String name, Surname surname, Sex sex, TredecimalDate birthDate) {
        this.id = id;
        this.name = name;
        this.surnames = List.of(surname);
        this.sex = sex;
        this.birthDate = birthDate;
    }

    public void addListener(Consumer<Person> listener) {
        listeners = listeners.add(listener);
    }

    public void removeListener(Consumer<Person> listener) {
        listeners = listeners.remove(listener);
    }

    @Override
    public String getId() {
        return id;
    }

    public TredecimalDate getBirthDate() {
        return birthDate;
    }

    public TredecimalDateInterval getAge(TredecimalDate now) {
        return TredecimalDateInterval.intervalBetween(now, birthDate);
    }

    public Sex getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public List<Surname> getSurnames() {
        return surnames;
    }

    public String getSurname() {
        return getFullSurname();
    }

    public void setSurname(Surname surname) {
        surnames = surnames.append(surname);
        listeners.forEach(listener -> listener.accept(this));
    }

    public Surname getSurnameObject() {
        return surnames.last();
    }

    public <S extends State> Option<S> state(Class<S> stateClass) {
        return states.get(stateClass).map(stateClass::cast);
    }

    public <R extends Relation> Set<R> relations(Class<R> relationClass) {
        return relations.get(relationClass).orElse(HashSet.empty()).map(relationClass::cast);
    }

    public <T extends State> T addState(T state) {
        states = states.put(state.getClass(), state);
        listeners.forEach(listener -> listener.accept(this));
        return state;
    }

    public void removeState(Class<? extends State> stateClass) {
        states = states.remove(stateClass);
        listeners.forEach(listener -> listener.accept(this));
    }

    public void addRelation(Relation relation) {
        relations = relations.put(relation.getClass(),
                                  relations.get(relation.getClass()).orElse(HashSet.empty()).add(relation));

        listeners.forEach(listener -> listener.accept(this));
    }

    public void removeRelation(Relation relation) {
        relations = relations.put(relation.getClass(),
                                  relations.get(relation.getClass()).orElse(HashSet.empty()).remove(relation));

        listeners.forEach(listener -> listener.accept(this));
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, getFullSurname());
    }

    private String getFullSurname() {
        return (surnames.length() > 1 && sex == Sex.FEMALE)
               ? String.format("%s (%s)", surnames.last().getFemaleForm(), surnames.head().getFemaleForm())
               : surnames.last().getForm(sex);
    }
}
