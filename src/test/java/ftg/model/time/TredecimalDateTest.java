package ftg.model.time;

import ftg.IntegerRange;
import ftg.Integers;
import ftg.LongRange;
import ftg.Longs;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static ftg.ThrowableAssertion.assertThrown;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_MONTH;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TredecimalDateTest {

    @DataPoints
    public static TredecimalCalendar.Month[] months() {
        return TredecimalCalendar.Month.values();
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
    public void comparesAsDayOfEpoch(@LongRange @Longs long day) {

        final TredecimalDate date = new TredecimalDate(day);
        final TredecimalDate date2 = new TredecimalDate(day + 1);

        assertThat(date.compareTo(date2), is(equalTo(Long.compare(date.getDayOfEpoch(), date2.getDayOfEpoch()))));
        assertThat(date.compareTo(date), is(equalTo(Long.compare(date.getDayOfEpoch(), date.getDayOfEpoch()))));
        assertThat(date2.compareTo(date), is(equalTo(Long.compare(date2.getDayOfEpoch(), date.getDayOfEpoch()))));
    }

    @Theory
    public void calculatesCorrectMonth(@IntegerRange @Integers int year, TredecimalCalendar.Month month) {
        final TredecimalDate firstDayOfMonth = new TredecimalDate(year, month.getDays().getFirst());
        final TredecimalDate lastDayOfMonth = new TredecimalDate(year, month.getDays().getLast());

        assertThat(firstDayOfMonth.getMonth(), is(equalTo(month)));
        assertThat(lastDayOfMonth.getMonth(), is(equalTo(month)));
    }

    @Theory
    public void calculatesCorrectDayOfMonth(@Integers @IntegerRange int year,
                                            TredecimalCalendar.Month month,
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
    }

    @Theory
    public void addsDays(@IntegerRange(from = -1, to = 1) int year, TredecimalCalendar.Month month) {

        final TredecimalDate date = new TredecimalDate(year, month.getDays().getLast());
        final TredecimalDate date1 = date.plusDays(1);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + 1)));

        if (month == TredecimalCalendar.Month.THIRTEEN) {
            assertThat(date1.isZeroDay(), is(true));
            assertThat(date1.getYear(), is(equalTo(date.getYear() + 1)));
        } else {
            assertThat(date1.getMonth().ordinal(), is(equalTo(date.getMonth().ordinal() + 1)));
        }
    }

    @Theory
    public void addsMonths(@IntegerRange(from = -1, to = 1) int year, @IntegerRange(from = -20, to = 20) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 1);
        final TredecimalDate date1 = date.plusMonths(delta);

        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + delta * DAYS_IN_MONTH)));
    }

    @Theory
    public void addsYears(@IntegerRange(from = -1, to = 1) int year, @IntegerRange(from = -5, to = 5) int delta) {

        final TredecimalDate date = new TredecimalDate(year, 10);
        final TredecimalDate date1 = date.plusYears(delta);

        assertThat(date1.getYear(), is(equalTo(date.getYear() + delta)));
        assertThat(date1.getDayOfEpoch(), is(equalTo(date.getDayOfEpoch() + delta * DAYS_IN_YEAR)));
    }


}

