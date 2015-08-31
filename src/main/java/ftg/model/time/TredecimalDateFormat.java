package ftg.model.time;

public enum TredecimalDateFormat {
    ISO {
        @Override
        public String format(TredecimalDate date) {
            return (date.isZeroDay())
                   ? String.format("0.%s", date.getYear())
                   : String.format("%s.%s.%s", date.getDayOfMonth(), date.getMonth().ordinal() + 1, date.getYear());
        }
    };

    public abstract String format(TredecimalDate date);
}
