package ftg.simulation.lineage;

import static com.google.common.base.Preconditions.checkState;
import static ftg.commons.Util.streamFromOptional;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import ftg.commons.UniquePairs;
import ftg.commons.Util;
import ftg.graph.model.person.Person;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Lineages {

    private static final ImmutableSet<Ancestors> PARENTS = ImmutableSet.of(Ancestors.FATHER, Ancestors.MOTHER);

    private final EnumMap<Ancestors, Function<Person, Optional<Person>>> kinshipProviders = new EnumMap<>(Ancestors.class);

    private final Table<Person, Ancestors, Person> knownKinship = HashBasedTable.create();

    private final Table<Person, Person, Integer> knownRelation = HashBasedTable.create();

    public Lineages() {
        kinshipProviders.put(Ancestors.FATHER, person -> Optional.ofNullable(person.getFather()));
        kinshipProviders.put(Ancestors.MOTHER, person -> Optional.ofNullable(person.getMother()));

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

    public Optional<Integer> findClosestAncestry(Person person, Person ancestor) {
        if (knownRelation.contains(person, ancestor)) {
            return Optional.of(knownRelation.get(person, ancestor));
        }

        if (Objects.equals(person, ancestor)) {
            return Optional.of(0);
        }

        return PARENTS.stream()
                .flatMap(parent -> streamFromOptional(getAncestor(person, parent)))
                .flatMap(parent -> streamFromOptional(findClosestAncestry(parent, ancestor)))
                .map(r -> r + 1)
                .peek(r -> cacheRelation(person, ancestor, r))
                .min(Integer::compare);

    }

    public Optional<Integer> findClosestRelation(Person a, Person b, int depthLimit) {

        checkState(!Objects.equals(requireNonNull(a), requireNonNull(b)), "Both arguments are same person");

        return recursiveFindClosestRelation(a, b, 0, depthLimit);
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
                .flatMap(Util::streamFromOptional)
                .collect(Collectors.toList());

        return UniquePairs.of(ancestors).stream()
                .map(pair -> recursiveFindClosestRelation(pair.get(0), pair.get(1), depth + 1, depthLimit))
                .flatMap(Util::streamFromOptional)
                .map(r -> r + 1)
                .peek(r -> cacheRelation(a, b, r))
                .min(Integer::compare);
    }


    private void cacheRelation(Person a, Person b, Integer relation) {
        if (Optional.ofNullable(knownRelation.get(a, b)).orElse(Integer.MAX_VALUE) > requireNonNull(relation)) {
            knownRelation.put(a, b, relation);
        }
    }
}
