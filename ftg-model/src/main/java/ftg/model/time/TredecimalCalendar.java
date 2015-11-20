package ftg.model.time;


import ftg.commons.range.IntegerRange;

public final class TredecimalCalendar {

    public enum DayOfWeek {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH,
        SIXTH,
        SEVENTH
    }

    public static final int DAYS_IN_YEAR = 365;
    public static final int DAYS_IN_MONTH = 28;
    public static final int WEEKS_IN_MONTH = 4;
    public static final int DAYS_IN_WEEK = 7;
    public static final IntegerRange YEAR_DAYS = IntegerRange.inclusive(0, DAYS_IN_YEAR - 1);
    public static final IntegerRange MONTH_DAYS = IntegerRange.inclusive(1, DAYS_IN_MONTH);

    public static TredecimalDate min(TredecimalDate a, TredecimalDate b) {
        return (a.isBefore(b)) ? a : b;
    }

    public static TredecimalDate max(TredecimalDate a, TredecimalDate b) {
        return (a.isAfter(b)) ? a : b;
    }

    private TredecimalCalendar() {
    }
}
