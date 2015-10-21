package ftg.commons.time;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class TredecimalDateRange implements Iterable<TredecimalDate> {

    private final TredecimalDate first;

    private final TredecimalDate last;

    public static TredecimalDateRange inclusiveDateRange(TredecimalDate first, TredecimalDate last) {
        return new TredecimalDateRange(first, last);
    }

    TredecimalDateRange(TredecimalDate first, TredecimalDate last) {
        checkArgument(!first.isAfter(last));
        this.first = first;
        this.last = last;
    }

    public TredecimalDate getFirst() {
        return first;
    }

    public TredecimalDate getLast() {
        return last;
    }

    public boolean includes(TredecimalDate date) {
        return !date.isBefore(first) && !date.isAfter(last);
    }

    @Override
    public Iterator<TredecimalDate> iterator() {
        return new TredecimalDateRangeIterator(first, last);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TredecimalDateRange that = (TredecimalDateRange) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(last, that.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("first", first)
                .add("last", last)
                .toString();
    }

    private static class TredecimalDateRangeIterator implements Iterator<TredecimalDate> {

        private final TredecimalDate last;

        private TredecimalDate current;

        public TredecimalDateRangeIterator(TredecimalDate first, TredecimalDate last) {
            this.last = last;
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return !current.isAfter(last);
        }

        @Override
        public TredecimalDate next() {
            if (hasNext()) {
                final TredecimalDate old = current;
                current = current.plusDays(1);
                return old;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
