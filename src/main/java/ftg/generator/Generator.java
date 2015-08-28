package ftg.generator;

import com.google.common.base.Supplier;

import java.util.stream.Stream;

public interface Generator<T> extends Supplier<T> {

    Stream<T> stream();
}
