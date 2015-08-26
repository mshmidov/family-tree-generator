package ftg.model.time;

import com.google.common.testing.EqualsTester;
import ftg.IntegerRange;
import ftg.Integers;
import ftg.LongRange;
import ftg.Longs;
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
    public void canBeCreatedFromAnyDayOfEpoch(@LongRange(from = -800, to = 800, step = 10) @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);

        assertThat(date.getDayOfEpoch(), is(equalTo(day)));
    }

    @Theory
    public void canBeCreatedFromYearAndDay(@LongRange(from = -800, to = 800, step = 10) @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        final TredecimalDate date2 = new TredecimalDate(date.getYear(), date.getDayOfYear());

        assertThat(date.getDayOfEpoch(), is(equalTo(day)));
        assertThat(date2.getDayOfEpoch(), is(equalTo(day)));
    }

    @Theory
    public void canBeCreatedFromYearMonthAndDay(@LongRange(from = -800, to = 800, step = 10) @Longs long day) {

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
    public void comparesAsDayOfEpoch(@LongRange @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        final TredecimalDate date2 = new TredecimalDate(day + 1);

        assertThat(date.compareTo(date2), is(equalTo(Long.compare(date.getDayOfEpoch(), date2.getDayOfEpoch()))));
        assertThat(date.compareTo(date), is(equalTo(Long.compare(date.getDayOfEpoch(), date.getDayOfEpoch()))));
        assertThat(date2.compareTo(date), is(equalTo(Long.compare(date2.getDayOfEpoch(), date.getDayOfEpoch()))));
    }

    @Theory
    public void calculatesCorrectMonth(@IntegerRange @Integers int year, Month month) {
        final TredecimalDate firstDayOfMonth = new TredecimalDate(year, month.getDays().getFirst());
        final TredecimalDate lastDayOfMonth = new TredecimalDate(year, month.getDays().getLast());

        assertThat(firstDayOfMonth.getMonth(), is(equalTo(month)));
        assertThat(lastDayOfMonth.getMonth(), is(equalTo(month)));
    }

    @Theory
    public void calculatesCorrectDayOfMonth(@Integers @IntegerRange int year,
                                            Month month,
                                            @IntegerRange(from = 1, to = 28) int day) {

        final int dayOfYear = month.getDays().getFirst() + day - 1;
        final TredecimalDate date = new TredecimalDate(year, dayOfYear);

        assertThat(date.getDayOfMonth(), is(equalTo(day)));
    }

    @Theory
    public void zeroDayIsSpecial(@Integers @IntegerRange int year) {
        final TredecimalDate date = new TredecimalDate(year, 0);

        assertThat(date.isZeroDay(), is(true));
        assertThrown(date::getMonth).isInstanceOf(IllegalStateException.class);
        assertThrown(date::getDayOfMonth).isInstanceOf(IllegalStateException.class);
        assertThrown(() -> date.plusMonths(0)).isInstanceOf(IllegalStateException.class);
        assertThrown(() -> date.minusMonths(0)).isInstanceOf(IllegalStateException.class);
    }

    @Theory
    public void addsDays(@IntegerRange(from = -1, to = 1) int year,
                         Month month,
                         @IntegerRange(from = 0, to = 2) int days) {

        final TredecimalDate date = new TredecimalDate(year, month.getDays().getLast());
        final TredecimalDate date1 = date.plusDays(days);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + days)));
    }

    @Theory
    public void addsMonths(@IntegerRange(from = -1, to = 1) int year,
                           @IntegerRange(from = 0, to = 27) int delta,
                           @IntegerRange(from = 1, to = 28) int day) {

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
    public void addsYears(@IntegerRange(from = -1, to = 1) int year, @IntegerRange(from = 0, to = 5) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 10);
        final TredecimalDate date1 = date.plusYears(delta);

        assertThat(date1.getYear(), is(equalTo(date.getYear() + delta)));
        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + delta * DAYS_IN_YEAR)));
    }

    @Theory
    public void subtractsDays(@IntegerRange(from = -1, to = 1) int year,
                              Month month,
                              @IntegerRange(from = 0, to = 2) int days) {

        final TredecimalDate date = new TredecimalDate(year, month.getDays().getLast());
        final TredecimalDate date1 = date.minusDays(days);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() - days)));
    }

    @Theory
    public void subtractsMonths(@IntegerRange(from = -1, to = 1) int year,
                                @IntegerRange(from = 0, to = 27) int delta,
                                @IntegerRange(from = 1, to = 28) int day) {

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
    public void subtractsYears(@IntegerRange(from = -1, to = 1) int year, @IntegerRange(from = 0, to = 5) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 10);
        final TredecimalDate date1 = date.minusYears(delta);

        assertThat(date1.getYear(), is(equalTo(date.getYear() - delta)));
        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() - delta * DAYS_IN_YEAR)));
    }


    @Theory
    public void additionIsSubtractionWithDifferentSign(@IntegerRange(from = 1, to = 10) int delta) {

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

    @Theory
    public void equalsIsWorking(@IntegerRange(from = 1, to = 28) int day) {
        new EqualsTester()
                .addEqualityGroup(new TredecimalDate(day), new TredecimalDate(0, day), new TredecimalDate(0, Month.ONE, day))
                .addEqualityGroup(new TredecimalDate(512), new TredecimalDate(1, 147), new TredecimalDate(1, Month.SIX, 7))
                .addEqualityGroup(new TredecimalDate(-364), new TredecimalDate(-1, 1), new TredecimalDate(-1, Month.ONE, 1))
                .addEqualityGroup(new TredecimalDate(365), new TredecimalDate(1, 0))
                .testEquals();
    }

    @Theory
    public void toStringIsWorking(@IntegerRange(from = 0, to = 364, step = 20) int day) {
        final TredecimalDate date = new TredecimalDate(day);
        assertThat(date.toString(), containsString("dayOfEpoch="+date.getDayOfEpoch()));
        assertThat(date.toString(), containsString("year="+date.getYear()));
        assertThat(date.toString(), containsString("dayOfYear="+date.getDayOfYear()));
    }
}

