package ftg.util;

import java.util.function.Predicate;

public final class MorePreconditions {

    private MorePreconditions() {
    }

    public static <T> T checkedArgument(T argument, Predicate<T> condition) {
        if (!condition.test(argument)) {
            throw new IllegalArgumentException(String.format("%s is wrong value", argument));
        }
        return argument;
    }

    public static <T> T checkedArgument(T argument, Predicate<T> condition, String message) {
        if (!condition.test(argument)) {
            throw new IllegalArgumentException(String.format(message, argument));
        }
        return argument;
    }

    public static <T extends Comparable<T>> Predicate<T> between(T a, T b) {
        return x -> ((a.compareTo(x) < 0) && (x.compareTo(b) < 0));
    }
}
