package ftg.model.time;

import ftg.Integers;
import ftg.IntegersInRange;
import ftg.Longs;
import ftg.LongsInRange;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static ftg.ThrowableAssertion.assertThrown;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TredecimalDateTest {

    @DataPoints
    public static Month[] months() {
        return Month.values();
    }

    @Theory
    public void canBeCreatedFromAnyDayOfEpoch(@LongsInRange(from = -800, to = 800, step = 10) @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);

        assertThat(date.getDayOfEpoch(), is(equalTo(day)));
    }

    @Theory
    public void canBeCreatedFromYearAndDay(@LongsInRange(from = -800, to = 800, step = 10) @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        final TredecimalDate date2 = new TredecimalDate(date.getYear(), date.getDayOfYear());

        assertThat(date.getDayOfEpoch(), is(equalTo(day)));
        assertThat(date2.getDayOfEpoch(), is(equalTo(day)));
    }

    @Theory
    public void canBeCreatedFromYearMonthAndDay(@LongsInRange(from = -800, to = 800, step = 10) @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        if (!date.isZeroDay()) {
            final TredecimalDate date2 = new TredecimalDate(date.getYear(), date.getMonth(), date.getDayOfMonth());

            assertThat(date.getDayOfEpoch(), is(equalTo(day)));
            assertThat(date2.getDayOfEpoch(), is(equalTo(day)));
        }
    }

    @Theory
    public void cannotBeCreatedWithWrongDayOfYear(@Integers({-1, 365}) int day) {
        assertThrown(() -> new TredecimalDate(0, day)).isInstanceOf(IllegalArgumentException.class);
    }

    @Theory
    public void cannotBeCreatedWithWrongDayOfMonth(@Integers({-1, 0, 29}) int day) {
        assertThrown(() -> new TredecimalDate(0, Month.ONE, day)).isInstanceOf(IllegalArgumentException.class);
    }


    @Theory
    public void comparesAsDayOfEpoch(@LongsInRange @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        final TredecimalDate date2 = new TredecimalDate(day + 1);

        assertThat(date.compareTo(date2), is(equalTo(Long.compare(date.getDayOfEpoch(), date2.getDayOfEpoch()))));
        assertThat(date.compareTo(date), is(equalTo(Long.compare(date.getDayOfEpoch(), date.getDayOfEpoch()))));
        assertThat(date2.compareTo(date), is(equalTo(Long.compare(date2.getDayOfEpoch(), date.getDayOfEpoch()))));

        assertThat(date.isBefore(date2), is(true));
        assertThat(date2.isAfter(date), is(true));

        assertThat(date.isAfter(date2), is(false));
        assertThat(date2.isBefore(date), is(false));

        assertThat(date.isAfter(date), is(false));
        assertThat(date.isBefore(date), is(false));
    }

    @Theory
    public void calculatesCorrectMonth(@IntegersInRange @Integers int year, Month month) {
        final TredecimalDate firstDayOfMonth = new TredecimalDate(year, month.getDays().getFirst());
        final TredecimalDate lastDayOfMonth = new TredecimalDate(year, month.getDays().getLast());

        assertThat(firstDayOfMonth.getMonth(), is(equalTo(month)));
        assertThat(lastDayOfMonth.getMonth(), is(equalTo(month)));
    }

    @Theory
    public void calculatesCorrectDayOfMonth(@Integers @IntegersInRange int year,
                                            Month month,
                                            @IntegersInRange(from = 1, to = 28) int day) {

        final int dayOfYear = month.getDays().getFirst() + day - 1;
        final TredecimalDate date = new TredecimalDate(year, dayOfYear);

        assertThat(date.getDayOfMonth(), is(equalTo(day)));
    }

    @Theory
    public void zeroDayIsSpecial(@Integers @IntegersInRange int year) {
        final TredecimalDate date = new TredecimalDate(year, 0);

        assertThat(date.isZeroDay(), is(true));
        assertThrown(date::getMonth).isInstanceOf(IllegalStateException.class);
        assertThrown(date::getDayOfMonth).isInstanceOf(IllegalStateException.class);
        assertThrown(() -> date.plusMonths(0)).isInstanceOf(IllegalStateException.class);
        assertThrown(() -> date.minusMonths(0)).isInstanceOf(IllegalStateException.class);
    }

    @Theory
    public void addsDays(@IntegersInRange(from = -1, to = 1) int year,
                         Month month,
                         @IntegersInRange(from = 0, to = 2) int days) {

        final TredecimalDate date = new TredecimalDate(year, month.getDays().getLast());
        final TredecimalDate date1 = date.plusDays(days);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + days)));
    }

    @Theory
    public void addsMonths(@IntegersInRange(from = -1, to = 1) int year,
                           @IntegersInRange(from = 0, to = 27) int delta,
                           @IntegersInRange(from = 1, to = 28) int day) {

        final TredecimalDate date = new TredecimalDate(year, day);
        final TredecimalDate date1 = date.plusMonths(delta);

        Month m = date.getMonth();
        long y = year;
        for (int i = 0; i < delta; i++) {
            m = m.next();
            if (m == Month.first()) {
                y++;
            }
        }

        assertThat(date1.getYear(), is(equalTo(y)));
        assertThat(date1.getMonth(), is(equalTo(m)));
        assertThat(date1.getDayOfMonth(), is(equalTo(date.getDayOfMonth())));
    }

    @Theory
    public void addsYears(@IntegersInRange(from = -1, to = 1) int year, @IntegersInRange(from = 0, to = 5) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 10);
        final TredecimalDate date1 = date.plusYears(delta);

        assertThat(date1.getYear(), is(equalTo(date.getYear() + delta)));
        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + delta * DAYS_IN_YEAR)));
    }

    @Theory
    public void subtractsDays(@IntegersInRange(from = -1, to = 1) int year,
                              Month month,
                              @IntegersInRange(from = 0, to = 2) int days) {

        final TredecimalDate date = new TredecimalDate(year, month.getDays().getLast());
        final TredecimalDate date1 = date.minusDays(days);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() - days)));
    }

    @Theory
    public void subtractsMonths(@IntegersInRange(from = -1, to = 1) int year,
                                @IntegersInRange(from = 0, to = 27) int delta,
                                @IntegersInRange(from = 1, to = 28) int day) {

        final TredecimalDate date = new TredecimalDate(year, day);
        final TredecimalDate date1 = date.minusMonths(delta);

        Month m = date.getMonth();
        long y = year;
        for (int i = 0; i < delta; i++) {
            m = m.previous();
            if (m == Month.last()) {
                y--;
            }
        }

        assertThat(date1.getYear(), is(equalTo(y)));
        assertThat(date1.getMonth(), is(equalTo(m)));
        assertThat(date1.getDayOfMonth(), is(equalTo(date.getDayOfMonth())));
    }

    @Theory
    public void subtractsYears(@IntegersInRange(from = -1, to = 1) int year, @IntegersInRange(from = 0, to = 5) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 10);
        final TredecimalDate date1 = date.minusYears(delta);

        assertThat(date1.getYear(), is(equalTo(date.getYear() - delta)));
        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() - delta * DAYS_IN_YEAR)));
    }


    @Theory
    public void additionIsSubtractionWithDifferentSign(@IntegersInRange(from = 1, to = 10) int delta) {

        final TredecimalDate date = new TredecimalDate(1);

        assertThat(date.plusDays(-delta).getDayOfEpoch(), is(equalTo(date.minusDays(delta).getDayOfEpoch())));
        assertThat(date.plusMonths(-delta).getDayOfEpoch(), is(equalTo(date.minusMonths(delta).getDayOfEpoch())));
        assertThat(date.plusYears(-delta).getDayOfEpoch(), is(equalTo(date.minusYears(delta).getDayOfEpoch())));

        assertThat(date.plusDays(delta).getDayOfEpoch(), is(equalTo(date.minusDays(-delta).getDayOfEpoch())));
        assertThat(date.plusMonths(delta).getDayOfEpoch(), is(equalTo(date.minusMonths(-delta).getDayOfEpoch())));
        assertThat(date.plusYears(delta).getDayOfEpoch(), is(equalTo(date.minusYears(-delta).getDayOfEpoch())));
    }

    @Test
    public void shouldReturnSelfOnZeroAdditionsAndSubtractions() {

        // given
        final TredecimalDate date = new TredecimalDate(1);

        assertThat(date.plusDays(0), is(sameInstance(date)));
        assertThat(date.plusMonths(0), is(sameInstance(date)));
        assertThat(date.plusYears(0), is(sameInstance(date)));
        assertThat(date.minusDays(0), is(sameInstance(date)));
        assertThat(date.minusMonths(0), is(sameInstance(date)));
        assertThat(date.minusYears(0), is(sameInstance(date)));
    }
}

