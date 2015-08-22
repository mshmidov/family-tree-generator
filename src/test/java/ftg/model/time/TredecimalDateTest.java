package ftg.model.time;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TredecimalDateTest {

    @Test
    public void shouldCalculateMonthAndDay() {

        // given
        for (final int month : TredecimalDate.MONTHS.values().toArray()) {
            for (final int day_of_month : TredecimalDate.MONTH_DAYS.values().toArray()) {

                final int day = (month-1)* 28 + day_of_month;
                // when
                final TredecimalDate date = new TredecimalDate(0, day);

                // then
                assertThat(String.format("Month for day %s should be correct", day),
                        date.getMonth().get(), is(equalTo(month)));
                assertThat(String.format("Day of month for day %s should be correct", day),
                        date.getDayOfMonth(), is(equalTo(day_of_month)));
                assertThat(String.format("%s should be not Year Day", day),
                        date.IsYearDay(), is(false));
            }
        }

        final int day = TredecimalDate.YEAR_LAST_DAY;
        final TredecimalDate date = new TredecimalDate(0, day);

        assertThat(String.format("Should be no month for day %s", day),
                date.getMonth().isPresent(), is(false));
        assertThat(String.format("Day of month for day %s should be correct", day),
                date.getDayOfMonth(), is(equalTo(1)));
        assertThat(String.format("%s should be Year Day", day),
                date.IsYearDay(), is(true));

    }
}