package ftg.commons;

import javaslang.collection.HashSet;
import javaslang.collection.Set;
import javaslang.collection.TreeSet;
import javaslang.control.Option;

import java.util.Comparator;
import java.util.function.Predicate;


public final class Bucket<T> {

    private final Predicate<T> storeIf;

    private Set<T> persons;

    public Bucket(Predicate<T> storeIf, Option<Comparator<T>> orderBy) {
        this.storeIf = storeIf;
        this.persons = (orderBy.isDefined())
                       ? TreeSet.empty(orderBy.get())
                       : HashSet.empty();
    }


    public Set<T> values() {
        return persons;
    }

    public void update(T value) {
        persons = (storeIf.test(value)) ? persons.add(value) : persons.remove(value);
    }
}
