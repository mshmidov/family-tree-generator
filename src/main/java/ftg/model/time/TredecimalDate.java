package ftg.model.time;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Range;
import ftg.util.IntegerRange;
import ftg.util.MorePreconditions;

import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static ftg.util.MorePreconditions.between;
import static ftg.util.MorePreconditions.checkArgument;

public class TredecimalDate {

    public static final int YEAR_FIRST_DAY = 1;
    public static final int YEAR_LAST_DAY = 365;

    public static final IntegerRange YEAR_DAYS = IntegerRange.inclusive(YEAR_FIRST_DAY, YEAR_LAST_DAY);
    public static final IntegerRange MONTHS = IntegerRange.inclusive(1, 13);
    public static final IntegerRange MONTH_DAYS = IntegerRange.inclusive(1, 28);

    private final int year;

    private final int day;

    private final Optional<Integer> month;

    private final int dayOfMonth;

    public TredecimalDate(int year, int day) {
        this.year = year;
        this.day = checkArgument(day, YEAR_DAYS);
        final int monthIndex = ((day - 1) / 28) + 1;
        this.month = MONTH_DAYS.includes(monthIndex)
                ? Optional.of(monthIndex)
                : Optional.empty();
        this.dayOfMonth = day % 28;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public Optional<Integer> getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean IsYearDay() {
        return day == YEAR_DAYS.getUpper();
    }
}
