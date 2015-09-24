package ftg.model.state;

import ftg.model.time.TredecimalDate;

public final class Death implements State {

    private final TredecimalDate date;

    public Death(TredecimalDate date) {
        this.date = date;
    }

    public TredecimalDate getDate() {
        return date;
    }
}
