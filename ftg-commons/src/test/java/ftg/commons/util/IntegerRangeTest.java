package ftg.commons.util;

import ftg.commons.test.Integers;
import ftg.commons.test.IntegersInRange;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import static ftg.commons.test.ThrowableAssertion.assertThrown;


@RunWith(Theories.class)
public class IntegerRangeTest {


    @Theory
    public void includesNumbersInBoundaries(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (int i = lower; i <= upper; i++) {
            Assert.assertThat(range.includes(i), CoreMatchers.is(true));
        }
    }

    @Theory
    public void includesNumbersFromIterable(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (Integer i : range) {
            Assert.assertThat(range.includes(i), CoreMatchers.is(true));
        }
    }

    @Theory
    public void includesNumbersFromIterator(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        final PrimitiveIterator.OfInt iterator = range.iterator();
        while (iterator.hasNext()) {
            Assert.assertThat(range.includes(iterator.next()), CoreMatchers.is(true));
        }
        assertThrown(iterator::nextInt).isInstanceOf(NoSuchElementException.class);
    }

    @Theory
    public void includesNumbersFromStream(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);
        Assert.assertThat(range.stream().allMatch(range::includes), CoreMatchers.is(true));
    }

    @Theory
    public void iterableHasSameNumbersAsStream(@Integers({-1}) int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        final List<Integer> intsFromIterable = new ArrayList<>();
        range.forEach(intsFromIterable::add);

        final List<Integer> intsFromStream = new ArrayList<>();
        range.stream().forEach(intsFromStream::add);

        Assert.assertThat(intsFromIterable, CoreMatchers.is(CoreMatchers.equalTo(intsFromStream)));
    }

    @Theory
    public void notIncludesNumbersOutsideBoundaries(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (int i = lower - 10; i < lower; i++) {
            Assert.assertThat(range.includes(i), CoreMatchers.is(false));
        }

        for (int i = upper + 10; i > upper; i--) {
            Assert.assertThat(range.includes(i), CoreMatchers.is(false));
        }
    }

    @Theory
    public void lowerBoundaryIsLessOrEqualToUpper(@Integers({-1, 0, 1}) int lower) {
        assertThrown(() -> IntegerRange.inclusive(lower, lower - 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Theory
    public void predicateConsistentWithInclude(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (int i = lower; i <= upper; i++) {
            Assert.assertThat(range.includes(i), CoreMatchers.is(CoreMatchers.equalTo(range.test(i))));
        }
    }

}