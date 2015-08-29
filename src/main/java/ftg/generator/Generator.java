package ftg.generator;


import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Generator<T> extends Supplier<T> {

    Stream<T> stream();
}
