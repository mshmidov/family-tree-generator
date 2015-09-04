package ftg.application.lineage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import ftg.commons.UniquePairs;
import ftg.model.person.Person;
import ftg.model.relation.Parentage;
import ftg.model.relation.Role;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public final class Lineages {

    private static final ImmutableSet<Ancestors> PARENTS = ImmutableSet.of(Ancestors.FATHER, Ancestors.MOTHER);

    private final EnumMap<Ancestors, Function<Person, Optional<Person>>> kinshipProviders = new EnumMap<>(Ancestors.class);

    private final Table<Person, Ancestors, Person> knownKinship = HashBasedTable.create();

    private final Table<Person, Person, Integer> knownRelation = HashBasedTable.create();


    public Lineages() {
        kinshipProviders.put(Ancestors.FATHER, p -> p.getRelations().get(Parentage.class).stream()
                .filter(parentage -> parentage.getRole(p) == Role.CHILD)
                .findFirst()
                .flatMap(parentage -> Optional.of(parentage.getFather())));

        kinshipProviders.put(Ancestors.MOTHER, p -> p.getRelations().get(Parentage.class).stream()
                .filter(parentage -> parentage.getRole(p) == Role.CHILD)
                .findFirst()
                .flatMap(parentage -> Optional.of(parentage.getMother())));

        kinshipProviders.put(Ancestors.PATERNAL_GRANDFATHER, p -> getFather(p).flatMap(this::getFather));
        kinshipProviders.put(Ancestors.PATERNAL_GRANDMOTHER, p -> getFather(p).flatMap(this::getMother));
        kinshipProviders.put(Ancestors.MATERNAL_GRANDFATHER, p -> getMother(p).flatMap(this::getFather));
        kinshipProviders.put(Ancestors.MATERNAL_GRANDMOTHER, p -> getMother(p).flatMap(this::getMother));
    }

    public Optional<Person> getAncestor(Person person, Ancestors ancestor) {
        final Optional<Person> knownKin = Optional.ofNullable(knownKinship.get(person, ancestor));
        if (knownKin.isPresent()) {
            return knownKin;
        }

        final Optional<Person> kin = kinshipProviders.get(ancestor).apply(person);
        kin.ifPresent(k -> knownKinship.put(person, ancestor, k));

        return kin;
    }

    public Optional<Person> getFather(Person person) {
        return getAncestor(person, Ancestors.FATHER);
    }

    public Optional<Person> getMother(Person person) {
        return getAncestor(person, Ancestors.MOTHER);
    }

    public Optional<Integer> findClosestRelation(Person a, Person b, int depthLimit) {

        checkState(!Objects.equals(requireNonNull(a), requireNonNull(b)), "Both arguments are same person");

        final Optional<Integer> relation = recursiveFindClosestRelation(a, b, 0, depthLimit);

        relation.ifPresent(r -> cacheRelation(a, b, r));

        return relation;
    }

    private Optional<Integer> recursiveFindClosestRelation(Person a, Person b, int depth, int depthLimit) {
        if (depth > depthLimit) {
            return Optional.empty();
        }

        if (knownRelation.contains(a, b)) {
            return Optional.of(knownRelation.get(a, b));
        }

        if (Objects.equals(requireNonNull(a), requireNonNull(b))) {
            return Optional.of(0);
        }

        final List<Person> ancestors = UniquePairs.of(ImmutableList.of(a, b), PARENTS).stream()
                .map(pair -> getAncestor(pair.getLeft(), pair.getRight()))
                .flatMap(this::streamFromOptional)
                .collect(Collectors.toList());

        final Optional<Integer> recursiveResult = UniquePairs.of(ancestors).stream()
                .map(pair -> recursiveFindClosestRelation(pair.get(0), pair.get(1), depth + 1, depthLimit))
                .flatMap(this::streamFromOptional)
                .peek(r -> cacheRelation(a, b, r + 1))
                .collect(Collectors.minBy(Integer::compare));

        return recursiveResult.map(r -> r + 1);
    }


    private <T> Stream<T> streamFromOptional(Optional<T> o) {
        return o.isPresent() ? Stream.of(o.get()) : Stream.empty();
    }

    private void cacheRelation(Person a, Person b, Integer relation) {
        if (Optional.ofNullable(knownRelation.get(a, b)).orElse(Integer.MAX_VALUE) > requireNonNull(relation)) {
            knownRelation.put(a, b, relation);
        }
    }
}
