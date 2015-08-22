package ftg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class IntegerRange implements Predicate<Integer> {

    private final int lower;

    private final int upper;

    public static IntegerRange inclusive(int lower, int upper) {
        return new IntegerRange(lower, upper);
    }

    private IntegerRange(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    public boolean includes(int i) {
        return (lower <= i) && (i <= upper);
    }

    @Override
    public boolean test(Integer integer) {
        return includes(integer);
    }

    public IntStream values() {
        return IntStream.rangeClosed(lower, upper);
    }
}
