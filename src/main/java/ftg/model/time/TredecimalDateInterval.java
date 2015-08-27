package ftg.model.time;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static ftg.model.time.TredecimalCalendar.YEAR_DAYS;
import static ftg.util.MorePreconditions.checkedArgument;
import static java.lang.Math.*;

public final class TredecimalDateInterval implements Comparable<TredecimalDateInterval> {

    private final long days;

    private final long years;

    private final int daysOfYear;

    public static TredecimalDateInterval between(TredecimalDate a, TredecimalDate b) {
        return new TredecimalDateInterval(abs(a.getDayOfEpoch() - b.getDayOfEpoch()));
    }

    public TredecimalDateInterval(long days) {
        this.days = checkedArgument(days, d -> d >= 0);
        this.years = days / DAYS_IN_YEAR;
        this.daysOfYear = (int) (days - years * DAYS_IN_YEAR);
    }

    public TredecimalDateInterval(long years, int daysOfYear) {
        this.years = checkedArgument(years, d -> d >= 0);
        this.daysOfYear = checkedArgument(daysOfYear, YEAR_DAYS);
        this.days = addExact(multiplyExact(years, DAYS_IN_YEAR), daysOfYear);
    }

    public long getDays() {
        return days;
    }

    public long getYears() {
        return years;
    }

    public int getDaysOfYear() {
        return daysOfYear;
    }

    @Override
    public int compareTo(TredecimalDateInterval o) {
        return Long.compare(days, o.days);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TredecimalDateInterval that = (TredecimalDateInterval) o;
        return Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(days);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("days", days)
                .add("years", years)
                .add("daysOfYear", daysOfYear)
                .toString();
    }
}
