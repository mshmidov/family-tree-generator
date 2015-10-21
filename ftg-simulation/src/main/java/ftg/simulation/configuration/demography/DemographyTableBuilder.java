package ftg.simulation.configuration.demography;

import ftg.commons.range.IntegerRange;
import ftg.graph.model.person.Person;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public final class DemographyTableBuilder {

    private final TreeSet<IntegerRange> ageRanges = new TreeSet<>((o1, o2) -> Integer.compare(o1.getFirst(), o2.getFirst()));

    private final Map<IntegerRange, Double> maleValues = new HashMap<>();

    private final Map<IntegerRange, Double> femaleValues = new HashMap<>();

    DemographyTableBuilder() {
    }

    public DemographyTableBuilder put(IntegerRange ageRange, Person.Sex sex, double value) {

        if (!ageRanges.contains(ageRange)) {

            for (IntegerRange range : ageRanges) {
                if (ageRange.intersectsWith(range)) {
                    throw new IllegalArgumentException();
                }
            }

            ageRanges.add(ageRange);
        }

        map(sex).put(ageRange, value);

        return this;
    }

    public DemographyTable build() {

        for (IntegerRange range : ageRanges) {
            if (maleValues.containsKey(range) && !femaleValues.containsKey(range)) {
                throw new IllegalStateException(String.format("Builder contains male value for range %s, but no corresponding female value", range));
            }

            if (femaleValues.containsKey(range) && !maleValues.containsKey(range)) {
                throw new IllegalStateException(String.format("Builder contains female value for range %s, but no corresponding male value", range));
            }
        }

        return new DemographyTable(this);
    }

    TreeSet<IntegerRange> getAgeRanges() {
        return ageRanges;
    }

    Map<IntegerRange, Double> getMaleValues() {
        return maleValues;
    }

    Map<IntegerRange, Double> getFemaleValues() {
        return femaleValues;
    }

    private Map<IntegerRange, Double> map(Person.Sex sex) {
        return (sex == Person.Sex.MALE) ? maleValues : femaleValues;
    }
}
