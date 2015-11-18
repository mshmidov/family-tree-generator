package ftg.model.person;

import ftg.model.Identified;
import ftg.model.relation.Relation;
import ftg.model.state.State;
import ftg.model.time.TredecimalDate;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Option;

public final class Person implements Identified {

    public enum Sex {MALE, FEMALE}

    private final String id;

    private final TredecimalDate birthDate;

    private final Sex sex;

    private final String name;

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

    @Override
    public String getId() {return id;}

    public TredecimalDate getBirthDate() { return birthDate; }

    public Sex getSex() { return sex; }

    public String getName() { return name;}

    public List<Surname> getSurnames() {
        return surnames;
    }

    public String getSurname() {
        return getFullSurname();
    }

    public void setSurname(Surname surname) {
        surnames = surnames.append(surname);
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

    public void addState(State state) {
        states = states.put(state.getClass(), state);
    }

    public void removeState(Class<? extends State> stateClass) {
        states = states.remove(stateClass);
    }

    public void addRelation(Relation relation) {
        relations = relations.put(relation.getClass(),
                                  relations.get(relation.getClass()).orElse(HashSet.empty()).add(relation));
    }

    public void removeRelation(Relation relation) {
        relations = relations.put(relation.getClass(),
                                  relations.get(relation.getClass()).orElse(HashSet.empty()).remove(relation));
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
