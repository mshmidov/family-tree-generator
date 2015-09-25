package ftg.model.state;

import com.google.common.base.MoreObjects;
import ftg.model.time.TredecimalDate;

public final class Death implements State {

    private final TredecimalDate date;

    public Death(TredecimalDate date) {
        this.date = date;
    }

    public TredecimalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("date", date)
                .toString();
    }
}
