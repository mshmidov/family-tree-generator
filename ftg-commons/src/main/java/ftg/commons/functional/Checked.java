package ftg.commons.functional;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Checked {

    private Checked() {
    }

    public static <T> T argument(T value, Predicate<T> predicate, Object message) {
        return argument(value, predicate, () -> String.valueOf(message));
    }

    public static <T> T argument(T value, Predicate<T> predicate, Supplier<String> message) {
        if (predicate.test(value)) {
            return value;
        }
        throw new IllegalArgumentException(message.get());
    }
}
