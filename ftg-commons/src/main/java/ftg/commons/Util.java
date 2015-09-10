package ftg.commons;

import java.util.Optional;
import java.util.stream.Stream;

public final class Util {

    private Util() {
    }

    public static  <T> Stream<T> streamFromOptional(Optional<T> o) {
        return o.isPresent() ? Stream.of(o.get()) : Stream.empty();
    }
}
