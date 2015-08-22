package ftg.util;

import com.google.common.base.Preconditions;

import java.util.function.Predicate;

public final class MorePreconditions {

    private MorePreconditions() {
    }

    public static <T> T checkArgument(T argument, Predicate<T> condition) {
        if (!condition.test(argument)) {
            throw new IllegalArgumentException();
        }
        return argument;
    }

    public static <T> T checkArgument(T argument, Predicate<T> condition, String message) {
        if (!condition.test(argument)) {
            throw new IllegalArgumentException(message);
        }
        return argument;
    }

    public static <T extends Comparable<T>> Predicate<T> between(T a, T b) {
        return x -> ((a.compareTo(x) < 0) && (x.compareTo(b) < 0));
    }
}
