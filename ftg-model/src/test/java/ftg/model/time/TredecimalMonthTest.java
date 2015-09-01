package ftg.model.time;

import ftg.commons.util.IntegerRange;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TredecimalMonthTest {

    @Theory
    public void monthHaveDateRange(TredecimalMonth month) {

        final IntegerRange days = month.getDays();
        assertThat(days, is(equalTo(IntegerRange.inclusive(
                month.ordinal() * TredecimalCalendar.DAYS_IN_MONTH + 1,
                (month.ordinal() + 1) * TredecimalCalendar.DAYS_IN_MONTH))));
    }

    @Theory
    public void previousMonthIsCircular(TredecimalMonth month) {

        final TredecimalMonth previous = month.previous();

        if (month == TredecimalMonth.first()) {
            assertThat(previous, is(equalTo(TredecimalMonth.last())));
        } else {
            assertThat(previous.ordinal(), is(equalTo(month.ordinal() - 1)));
        }
    }

    @Theory
    public void nextMonthIsCircular(TredecimalMonth month) {

        final TredecimalMonth next = month.next();

        if (month == TredecimalMonth.last()) {
            assertThat(next, is(equalTo(TredecimalMonth.first())));
        } else {
            assertThat(next.ordinal(), is(equalTo(month.ordinal() + 1)));
        }
    }
}