package ftg.commons.functional;


import java.util.function.Predicate;

public final class Checked {

    private Checked() {
    }

    public static <T> T argument(T value, Predicate<? super T> predicate, Object message) {
        if (predicate.test(value)) {
            return value;
        }
        throw new IllegalArgumentException(String.format(String.valueOf(message), value));
    }


}
