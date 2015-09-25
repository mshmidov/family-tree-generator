package ftg.model.person;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import ftg.model.relation.Relation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Relations {

    private final Multimap<Class<? extends Relation>, Relation> relations = HashMultimap.create();

    private final ObservableList<Relation> relationValues = FXCollections.observableArrayList();

    public <R extends Relation> boolean contains(Class<R> relationClass) {
        return relations.get(relationClass).stream().map(relationClass::cast).collect(Collectors.counting()) > 0;
    }

    public void add(Relation relation) {
        relations.put(relation.getClass(), relation);
        relationValues.add(relation);
    }

    public <R extends Relation> List<R> get(Class<R> relationClass) {
        return ImmutableList.copyOf(relations.get(relationClass).stream().map(relationClass::cast).iterator());
    }

    public <R extends Relation> Optional<R> getSingle(Class<R> relationClass) {
        return relations.get(relationClass).stream().map(relationClass::cast).findFirst();
    }

    public void remove(Relation relation) {
        relations.remove(relation.getClass(), relation);
        relationValues.remove(relation);
    }

    public void removeAll(Class<? extends Relation> relationClass) {
        relationValues.removeAll(relations.removeAll(relationClass));
    }

    public ObservableList<Relation> getAllRelations() {
        return FXCollections.unmodifiableObservableList(relationValues);
    }
}
