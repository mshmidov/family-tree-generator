package ftg.application.configuration.demography;

import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;

import java.util.*;

import static ftg.commons.MorePreconditions.checked;
import static ftg.commons.MorePreconditions.isNotNull;

public final class DemographyTable {

    private final TreeSet<IntegerRange> ageRanges = new TreeSet<>((o1, o2) -> Integer.compare(o1.getFirst(), o2.getFirst()));

    private final Map<IntegerRange, Double> maleValues = new HashMap<>();

    private final Map<IntegerRange, Double> femaleValues = new HashMap<>();

    public static DemographyTableBuilder builder() {
        return new DemographyTableBuilder();
    }

    DemographyTable(DemographyTableBuilder builder) {

        ageRanges.addAll(builder.getAgeRanges());
        maleValues.putAll(builder.getMaleValues());
        femaleValues.putAll(builder.getFemaleValues());

    }

    public double get(int age, Person.Sex sex) {
        final Optional<IntegerRange> range = ageRanges.stream().filter(r -> r.includes(age)).findFirst();

        return checked(map(sex).get(range.orElse(ageRanges.last())), isNotNull(), NoSuchElementException::new);
    }

    private Map<IntegerRange, Double> map(Person.Sex sex) {
        return (sex == Person.Sex.MALE) ? maleValues : femaleValues;
    }
}