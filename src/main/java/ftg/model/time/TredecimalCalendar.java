package ftg.model.time;

import ftg.util.IntegerRange;

public final class TredecimalCalendar {

    public static final int DAYS_IN_YEAR = 365;
    public static final int DAYS_IN_MONTH = 28;
    public static final int WEEKS_IN_MONTH = 4;
    public static final int DAYS_IN_WEEK = 7;

    public static final IntegerRange YEAR_DAYS = IntegerRange.inclusive(0, DAYS_IN_YEAR - 1);
    public static final IntegerRange MONTH_DAYS = IntegerRange.inclusive(1, DAYS_IN_MONTH);

    public enum Month {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        ELEVEN,
        TWELVE,
        THIRTEEN;

        private final IntegerRange days = IntegerRange.inclusive(this.ordinal() * DAYS_IN_MONTH + 1, (this.ordinal() + 1) * DAYS_IN_MONTH);

        public IntegerRange getDays() {
            return days;
        }
    }

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
