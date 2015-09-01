package ftg.model.time;

import ftg.commons.test.Longs;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static ftg.commons.test.ThrowableAssertion.assertThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TredecimalDateRangeTest {

    @Theory
    public void iteratesThroughAllValues(@Longs({-730, 0, 730}) long day) {
        final TredecimalDate first = new TredecimalDate(day);
        final TredecimalDate last = new TredecimalDate(day + 28);
        final TredecimalDateRange range = TredecimalDateRange.inclusiveDateRange(first, last);

        TredecimalDate d = first;
        for (TredecimalDate date : range) {

            assertThat(date, is(equalTo(d)));
            d = d.plusDays(1);
        }
    }

    @Theory
    public void includesDatesFromIterator(@Longs({-730, 0, 730}) long day) {
        final TredecimalDate first = new TredecimalDate(day);
        final TredecimalDate last = new TredecimalDate(day + 2);
        final TredecimalDateRange range = TredecimalDateRange.inclusiveDateRange(first, last);

        final Iterator<TredecimalDate> iterator = range.iterator();
        while (iterator.hasNext()) {
            final TredecimalDate next = iterator.next();
            assertThat(range.includes(next), is(true));
        }
        assertThrown(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Theory
    public void notIncludesDatesOutsideBoundaries(@Longs({-730, 0, 730}) long day) {
        final TredecimalDate first = new TredecimalDate(day);
        final TredecimalDate last = new TredecimalDate(day + 700);
        final TredecimalDateRange range = TredecimalDateRange.inclusiveDateRange(first, last);

        for (int i = 1; i <= 3; i++) {
            assertThat(range.includes(first.minusDays(i)), is(false));
        }

        for (int i = 1; i <= 3; i++) {
            assertThat(range.includes(last.plusDays(i)), is(false));
        }
    }

    @Theory
    public void lowerBoundaryIsNotAfterThenUpper(@Longs({-730, 0, 730}) long day) {
        final TredecimalDate lower = new TredecimalDate(day);
        assertThrown(() -> TredecimalDateRange.inclusiveDateRange(lower, lower.minusDays(1))).isInstanceOf(IllegalArgumentException.class);
    }

}