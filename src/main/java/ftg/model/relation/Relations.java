package ftg.model.relation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.List;

public final class Relations {

    private final Multimap<Class<? extends Relation>, Relation> relations = HashMultimap.create();

    public void add(Relation relation) {
        relations.put(relation.getClass(), relation);
    }

    public <R extends Relation> List<R> get(Class<R> relationClass) {
        return ImmutableList.copyOf(relations.get(relationClass).stream().map(relationClass::cast).iterator());
    }

    public void remove(Relation relation) {
        relations.remove(relation.getClass(), relation);
    }

    public void removeAll(Class<? extends Relation> relationClass) {
        relations.removeAll(relationClass);
    }
}
