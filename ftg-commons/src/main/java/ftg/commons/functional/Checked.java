package ftg.commons.functional;


import java.util.function.Function;

public final class Checked {

    private Checked() {
    }

    public static <T> T argument(T value, Function<? super T, Boolean> predicate, Object message) {
        if (predicate.apply(value)) {
            return value;
        }
        throw new IllegalArgumentException(String.format(String.valueOf(message), value));
    }


}
