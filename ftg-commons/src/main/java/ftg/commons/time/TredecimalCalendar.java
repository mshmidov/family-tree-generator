package ftg.commons.time;


import ftg.commons.range.IntegerRange;

public final class TredecimalCalendar {

    public static final int DAYS_IN_YEAR = 365;
    public static final int DAYS_IN_MONTH = 28;
    public static final int WEEKS_IN_MONTH = 4;
    public static final int DAYS_IN_WEEK = 7;

    public static final IntegerRange YEAR_DAYS = IntegerRange.inclusive(0, DAYS_IN_YEAR - 1);
    public static final IntegerRange MONTH_DAYS = IntegerRange.inclusive(1, DAYS_IN_MONTH);

    public enum DayOfWeek {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH,
        SIXTH,
        SEVENTH
    }

    private TredecimalCalendar() {
    }
}
