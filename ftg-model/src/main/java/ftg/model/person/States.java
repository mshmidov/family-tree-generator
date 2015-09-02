package ftg.model.person;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import ftg.model.state.State;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class States {

    private final Multimap<Class<? extends State>, State> states = HashMultimap.create();

    public void add(State relation) {
        states.put(relation.getClass(), relation);
    }

    public <S extends State> boolean contains(Class<S> stateClass) {
        return states.get(stateClass).stream().map(stateClass::cast).collect(Collectors.counting()) > 0;
    }

    public <S extends State> List<S> get(Class<S> stateClass) {
        return ImmutableList.copyOf(states.get(stateClass).stream().map(stateClass::cast).iterator());
    }

    public <S extends State> Optional<S> getOptionalSingle(Class<S> stateClass) {
        return states.get(stateClass).stream().map(stateClass::cast).findFirst();
    }

    public <S extends State> S getSingle(Class<S> stateClass) {
        return getOptionalSingle(stateClass).get();
    }

    public void remove(State state) {
        states.remove(state.getClass(), state);
    }

    public void removeAll(Class<? extends State> stateClass) {
        states.removeAll(stateClass);
    }

    public void removeAll() {
        states.clear();
    }
}
