package ftg.model.time;

import ftg.util.IntegerRange;

public enum TredecimalMonth {
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

    private final IntegerRange days = IntegerRange.inclusive(
            this.ordinal() * TredecimalCalendar.DAYS_IN_MONTH + 1,
            (this.ordinal() + 1) * TredecimalCalendar.DAYS_IN_MONTH);

    public static TredecimalMonth first() {
        return values()[0];
    }

    public static TredecimalMonth last() {
        return values()[values().length - 1];
    }

    public IntegerRange getDays() {
        return days;
    }

    public TredecimalMonth previous() {
        return this == first()
               ? last()
               : values()[this.ordinal() - 1];
    }

    public TredecimalMonth next() {
        return this == last()
               ? first()
               : values()[this.ordinal() + 1];
    }

}
