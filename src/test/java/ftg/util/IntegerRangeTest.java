package ftg.util;

import ftg.Integers;
import ftg.IntegersInRange;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import static ftg.ThrowableAssertion.assertThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class IntegerRangeTest {


    @Theory
    public void includesNumbersInBoundaries(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (int i = lower; i <= upper; i++) {
            assertThat(range.includes(i), is(true));
        }
    }

    @Theory
    public void includesNumbersFromIterable(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (Integer i : range) {
            assertThat(range.includes(i), is(true));
        }
    }

    @Theory
    public void includesNumbersFromIterator(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        final PrimitiveIterator.OfInt iterator = range.iterator();
        while (iterator.hasNext()) {
            assertThat(range.includes(iterator.next()), is(true));
        }
        assertThrown(iterator::nextInt).isInstanceOf(NoSuchElementException.class);
    }

    @Theory
    public void includesNumbersFromStream(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);
        assertThat(range.stream().allMatch(range::includes), is(true));
    }

    @Theory
    public void iterableHasSameNumbersAsStream(@Integers({-1}) int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        final List<Integer> intsFromIterable = new ArrayList<>();
        range.forEach(intsFromIterable::add);

        final List<Integer> intsFromStream = new ArrayList<>();
        range.stream().forEach(intsFromStream::add);

        assertThat(intsFromIterable, is(equalTo(intsFromStream)));
    }

    @Theory
    public void notIncludesNumbersOutsideBoundaries(@IntegersInRange int lower) {
        final int upper = lower + 128;
        final IntegerRange range = IntegerRange.inclusive(lower, upper);

        for (int i = lower - 10; i < lower; i++) {
            assertThat(range.includes(i), is(false));
        }

        for (int i = upper + 10; i > upper; i--) {
            assertThat(range.includes(i), is(false));
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
            assertThat(range.includes(i), is(equalTo(range.test(i))));
        }
    }

}