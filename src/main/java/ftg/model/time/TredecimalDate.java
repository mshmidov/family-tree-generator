package ftg.model.time;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;
import static ftg.model.time.TredecimalCalendar.*;
import static ftg.util.MorePreconditions.checkArgument;

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

        this.dayOfYear = checkArgument((int) (dayOfEpoch - year * DAYS_IN_YEAR), YEAR_DAYS);
    }

    public TredecimalDate(long year, int day) {
        this(year * DAYS_IN_YEAR + checkArgument(day, YEAR_DAYS));
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

    public Month getMonth() {
        checkState(!isZeroDay(), "Zero day has no month");
        return TredecimalCalendar.Month.values()[(dayOfYear - 1) / DAYS_IN_MONTH];
    }

    public int getDayOfMonth() {
        checkState(!isZeroDay(), "Zero day has no month");
        return dayOfYear - ((dayOfYear - 1) / DAYS_IN_MONTH) * DAYS_IN_MONTH;
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

        return new TredecimalDate(dayOfEpoch + months * DAYS_IN_MONTH);
    }

    public TredecimalDate plusYears(int years) {
        if (years == 0) {
            return this;
        }

        return new TredecimalDate(year + years, dayOfYear);
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
