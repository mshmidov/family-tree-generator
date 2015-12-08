package ftg.simulation.lineage;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import ftg.model.person.Person;
import ftg.model.person.relation.Parentage;
import ftg.model.person.relation.Role;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Match;
import javaslang.control.Option;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class Lineages {

    private static final Set<Ancestors> PARENTS = HashSet.ofAll(Ancestors.FATHER, Ancestors.MOTHER);

    private final Table<Person, Person, Integer> knownRelation = HashBasedTable.create();

    private Map<Person, Set<Tuple2<Person, Integer>>> knownAncestors = HashMap.empty();

    public static Option<Person> findFather(Person person) {
        return person.relations(Parentage.class)
            .findFirst(parentage -> parentage.getRole(person) == Role.CHILD)
            .map(Parentage::getFather);
    }

    public static Option<Person> findMother(Person person) {
        return person.relations(Parentage.class)
            .findFirst(parentage -> parentage.getRole(person) == Role.CHILD)
            .map(Parentage::getMother);
    }

    public static Set<Person> findChildren(Person person) {
        return person.relations(Parentage.class)
            .filter(parentage -> parentage.getRole(person) != Role.CHILD)
            .map(Parentage::getChild);
    }

    public Option<Person> getAncestor(Person person, Ancestors ancestor) {
        return Match.of(ancestor)
            .whenIs(Ancestors.FATHER).then(findFather(person))
            .whenIs(Ancestors.MOTHER).then(findMother(person))
            .orElseThrow(IllegalArgumentException::new);
    }

    public Option<Integer> isDirectAncestor(Person person, Person ancestor) {
        final Map<Person, Integer> allAncestors = HashMap.ofAll(findAllAncestors(person));
        return allAncestors.get(ancestor);
    }

    public Option<Integer> findClosestRelation(Person a, Person b, int depthLimit) {
        checkState(!Objects.equals(requireNonNull(a), requireNonNull(b)), "Both arguments are same person");

        final Set<Tuple2<Person, Integer>> aTree = findAllAncestors(a);
        final Set<Tuple2<Person, Integer>> bTree = findAllAncestors(b);

        final Set<Tuple2<Person, Integer>> commonRelations = aTree.intersect(bTree);

        return commonRelations.map(Tuple2::_2).min().flatMap(relation -> (relation < depthLimit) ? Option.of(relation) : Option.none());
    }

    private void cacheRelation(Person a, Person b, Integer relation) {
        if (Optional.ofNullable(knownRelation.get(a, b)).orElse(Integer.MAX_VALUE) > requireNonNull(relation)) {
            knownRelation.put(a, b, relation);
        }
    }

    private Set<Tuple2<Person, Integer>> findAllAncestors(Person person) {
        if (knownAncestors.containsKey(person)) {
            return knownAncestors.get(person).get();
        }

        Set<Tuple2<Person, Integer>> ancestors = PARENTS
            .flatMap(parent -> getAncestor(person, parent))
            .map(ancestor -> Tuple.of(ancestor, 1));

        ancestors = ancestors.addAll(ancestors
                                         .flatMap(p -> findAllAncestors(p._1))
                                         .map(p -> p.map(Function.identity(), i -> i + 1)));

        knownAncestors = knownAncestors.put(person, ancestors);

        return ancestors;
    }
}
