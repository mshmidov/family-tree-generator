package ftg.simulation.lineage;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import ftg.model.person.Person;
import ftg.model.person.relation.Parentage;
import ftg.model.person.relation.Role;
import javaslang.Value;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Match;
import javaslang.control.Option;

import java.util.Objects;
import java.util.Optional;

public final class Lineages {

    private static final Set<Ancestors> PARENTS = HashSet.ofAll(Ancestors.FATHER, Ancestors.MOTHER);

    private final Table<Person, Person, Integer> knownRelation = HashBasedTable.create();

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

    public Option<Integer> findClosestAncestry(Person person, Person ancestor) {
        if (knownRelation.contains(person, ancestor)) {
            return Option.of(knownRelation.get(person, ancestor));
        }

        if (Objects.equals(person, ancestor)) {
            return Option.of(0);
        }

        return PARENTS
            .flatMap(parent -> getAncestor(person, parent))
            .flatMap(parent -> findClosestAncestry(parent, ancestor))
            .map(r -> r + 1)
            .peek(r -> cacheRelation(person, ancestor, r))
            .min();
    }

    public Option<Integer> findClosestRelation(Person a, Person b, int depthLimit) {

        checkState(!Objects.equals(requireNonNull(a), requireNonNull(b)), "Both arguments are same person");

        return recursiveFindClosestRelation(a, b, 0, depthLimit);
    }

    private Option<Integer> recursiveFindClosestRelation(Person a, Person b, int depth, int depthLimit) {
        if (depth > depthLimit) {
            return Option.none();
        }

        if (knownRelation.contains(a, b)) {
            return Option.of(knownRelation.get(a, b));
        }

        if (Objects.equals(requireNonNull(a), requireNonNull(b))) {
            return Option.of(0);
        }


        final List<Person> ancestors = List.ofAll(a, b).crossProduct(PARENTS)
            .map(pair -> getAncestor(pair._1, pair._2))
            .flatMap(Value::toSet);

        return ancestors.combinations(2)
            .map(pair -> recursiveFindClosestRelation(pair.get(0), pair.get(1), depth + 1, depthLimit))
            .flatMap(Value::toSet)
            .map(r -> r + 1)
            .peek(r -> cacheRelation(a, b, r))
            .min();
    }


    private void cacheRelation(Person a, Person b, Integer relation) {
        if (Optional.ofNullable(knownRelation.get(a, b)).orElse(Integer.MAX_VALUE) > requireNonNull(relation)) {
            knownRelation.put(a, b, relation);
        }
    }
}
