package ftg.model.time;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class MonthTest {


    @Theory
    public void previousMonthIsCircular(Month month) {

        final Month previous = month.previous();

        if (month == Month.first()) {
            assertThat(previous, is(equalTo(Month.last())));
        } else {
            assertThat(previous.ordinal(), is(equalTo(month.ordinal() - 1)));
        }
    }

    @Theory
    public void nextMonthIsCircular(Month month) {

        final Month next = month.next();

        if (month == Month.last()) {
            assertThat(next, is(equalTo(Month.first())));
        } else {
            assertThat(next.ordinal(), is(equalTo(month.ordinal() + 1)));
        }
    }
}