package ftg.commons.time;

import static com.google.common.base.Preconditions.checkState;
import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.commons.time.TredecimalCalendar.DAYS_IN_MONTH;
import static ftg.commons.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.commons.time.TredecimalCalendar.MONTH_DAYS;
import static ftg.commons.time.TredecimalCalendar.YEAR_DAYS;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class TredecimalDate implements Comparable<TredecimalDate> {

    private final long dayOfEpoch;

    private final long year;
    private final int dayOfYear;

    public TredecimalDate(long dayOfEpoch) {
        this.dayOfEpoch = dayOfEpoch;

        if (dayOfEpoch >= 0) {
            this.year = dayOfEpoch / DAYS_IN_YEAR;
        } else {
            this.year = (dayOfEpoch + 1) / DAYS_IN_YEAR - 1;
        }

        this.dayOfYear = (int) (dayOfEpoch - year * DAYS_IN_YEAR);
    }

    public TredecimalDate(long year, int day) {
        this(year * DAYS_IN_YEAR + checkedArgument(day, YEAR_DAYS));
    }

    public TredecimalDate(long year, TredecimalMonth month, int dayOfMonth) {
        this(year * DAYS_IN_YEAR + month.getDays().getFirst() + checkedArgument(dayOfMonth, MONTH_DAYS) - 1);
    }

    public long getDayOfEpoch() {
        return dayOfEpoch;
    }

    public boolean isZeroDay() {
        return dayOfYear == 0;
    }

    public long getYear() {
        return year;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public TredecimalMonth getMonth() {
        checkState(!isZeroDay(), "Zero day has no month");
        return TredecimalMonth.values()[(dayOfYear - 1) / DAYS_IN_MONTH];
    }

    public int getDayOfMonth() {
        checkState(!isZeroDay(), "Zero day has no month");
        return dayOfYear - ((dayOfYear - 1) / DAYS_IN_MONTH) * DAYS_IN_MONTH;
    }

    public boolean isBefore(TredecimalDate other) {
        return compareTo(other) < 0;
    }

    public boolean isAfter(TredecimalDate other) {
        return compareTo(other) > 0;
    }

    public TredecimalDate plusDays(int days) {
        if (days == 0) {
            return this;
        }

        return new TredecimalDate(dayOfEpoch + days);
    }

    public TredecimalDate plusMonths(int months) {
        checkState(!isZeroDay(), "Zero day has no month");
        if (months == 0) {
            return this;
        }
        if (months < 0) {
            return minusMonths(-months);
        }

        final int plusYears = (getMonth().ordinal() + months) / TredecimalMonth.values().length;
        final int plusMonths = months - plusYears * TredecimalMonth.values().length;

        return new TredecimalDate(year + plusYears, dayOfYear + plusMonths * DAYS_IN_MONTH);
    }

    public TredecimalDate plusYears(int years) {
        if (years == 0) {
            return this;
        }

        return new TredecimalDate(year + years, dayOfYear);
    }

    public TredecimalDate minusDays(int days) {
        return plusDays(-days);
    }

    public TredecimalDate minusMonths(int months) {
        checkState(!isZeroDay(), "Zero day has no month");
        if (months == 0) {
            return this;
        }
        if (months < 0) {
            return plusMonths(-months);
        }

        int fullYears = months / 13;
        final int monthsLeft = months - fullYears * 13;

        final TredecimalMonth m = getMonth();

        if (monthsLeft > m.ordinal()) {
            fullYears++;
        }

        final TredecimalMonth newMonth = (monthsLeft == 0)
                               ? m
                               : TredecimalMonth.values()[TredecimalMonth.values().length - monthsLeft];

        return new TredecimalDate(year - fullYears, newMonth, getDayOfMonth());
    }

    public TredecimalDate minusYears(int years) {
        return plusYears(-years);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TredecimalDate that = (TredecimalDate) o;
        return Objects.equals(dayOfEpoch, that.dayOfEpoch);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dayOfEpoch", dayOfEpoch)
                .add("year", year)
                .add("dayOfYear", dayOfYear)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfEpoch);
    }

    @Override
    public int compareTo(TredecimalDate o) {
        return Long.compare(dayOfEpoch, o.dayOfEpoch);
    }
}
