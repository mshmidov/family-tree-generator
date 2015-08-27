package ftg.model.time;

import ftg.*;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static ftg.ThrowableAssertion.assertThrown;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static java.lang.Math.abs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TredecimalDateIntervalTest {

    @Theory
    public void canBeCreatedFromPositiveDays(@Longs({0, Long.MAX_VALUE}) long days) {
        final TredecimalDateInterval interval = new TredecimalDateInterval(days);

        assertThat(interval.getDays(), is(equalTo(days)));
    }

    @Theory
    public void cannotBeCreatedFromNegativeDays(@Longs({-1, Long.MIN_VALUE}) long days) {

        assertThrown(() -> new TredecimalDateInterval(days)).isInstanceOf(IllegalArgumentException.class);
    }

    @Theory
    public void canBeCreatedFromPositiveYearsAndDays(@LongsInRange(from = 0, to = 100, step = 10) long years,
                                                     @IntegersInRange(from = 0, to = 364, step = 25) int days) {


        final TredecimalDateInterval interval = new TredecimalDateInterval(years, days);
        final TredecimalDateInterval interval2 = new TredecimalDateInterval(years * DAYS_IN_YEAR + days);

        assertThat(interval.getYears(), is(equalTo(years)));
        assertThat(interval.getDaysOfYear(), is(equalTo(days)));
        assertThat(interval2.getYears(), is(equalTo(years)));
        assertThat(interval2.getDaysOfYear(), is(equalTo(days)));


        assertThat(interval.getDays(), is(equalTo(interval2.getDays())));

        assertThat(interval.getYears(), is(equalTo(interval2.getYears())));
        assertThat(interval.getDaysOfYear(), is(equalTo(interval2.getDaysOfYear())));
    }

    @Theory
    public void cannotBeCreatedFromNegativeYears(@Longs({-1, Long.MIN_VALUE}) long years) {

        assertThrown(() -> new TredecimalDateInterval(years, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Theory
    public void cannotBeCreatedFromNegativeDaysOfYear(@Integers({-1, Integer.MIN_VALUE}) int days) {

        assertThrown(() -> new TredecimalDateInterval(0, days)).isInstanceOf(IllegalArgumentException.class);
    }

    @Theory
    public void representsDifferenceBetweenDates(@RandomLongs long a, @RandomLongs long b) {
        final TredecimalDate dateA = new TredecimalDate(a);
        final TredecimalDate dateB = new TredecimalDate(b);

        final TredecimalDateInterval interval = TredecimalDateInterval.between(dateA, dateB);

        assertThat(interval.getDays(), is(equalTo(abs(a - b))));
    }

    @Theory
    public void comparesAsDay(@RandomLongs long a, @RandomLongs long b) {

        final TredecimalDateInterval intervalA = new TredecimalDateInterval(abs(a));
        final TredecimalDateInterval intervalB = new TredecimalDateInterval(abs(b));

        assertThat(intervalA.compareTo(intervalB), is(equalTo(Long.compare(intervalA.getDays(), intervalB.getDays()))));
        assertThat(intervalA.compareTo(intervalA), is(equalTo(Long.compare(intervalA.getDays(), intervalA.getDays()))));

    }
}