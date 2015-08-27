package ftg.util;

import com.google.common.base.MoreObjects;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;

public final class IntegerRange implements Predicate<Integer>, Iterable<Integer> {

    private final int lower;

    private final int upper;

    public static IntegerRange inclusive(int lower, int upper) {
        return new IntegerRange(lower, upper);
    }

    private IntegerRange(int lower, int upper) {
        checkArgument(lower < upper);
        this.lower = lower;
        this.upper = upper;
    }

    public int getFirst() {
        return lower;
    }

    public int getLast() {
        return upper;
    }

    public boolean includes(int i) {
        return (lower <= i) && (i <= upper);
    }

    @Override
    public boolean test(Integer integer) {
        return includes(integer);
    }

    public IntStream stream() {
        return IntStream.rangeClosed(lower, upper);
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return new IntegerRangeIterator(lower, upper);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerRange integers = (IntegerRange) o;
        return Objects.equals(lower, integers.lower) &&
                Objects.equals(upper, integers.upper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lower", lower)
                .add("upper", upper)
                .toString();
    }

    private static class IntegerRangeIterator implements PrimitiveIterator.OfInt {

        private final int upper;

        private int current;

        public IntegerRangeIterator(int lower, int upper) {
            this.upper = upper;
            this.current = lower;
        }

        @Override
        public boolean hasNext() {
            return current <= upper;
        }

        @Override
        public int nextInt() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return current++;
        }
    }
}
