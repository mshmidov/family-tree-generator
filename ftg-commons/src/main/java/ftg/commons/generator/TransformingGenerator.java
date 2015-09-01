package ftg.commons.generator;


import java.util.function.Function;
import java.util.stream.Stream;

public class TransformingGenerator<I, O> implements Generator<O> {

    private final Function<I, O> transformation;

    private final Generator<I> generator;

    public TransformingGenerator(Generator<I> generator, Function<I, O> transformation) {
        this.transformation = transformation;
        this.generator = generator;
    }

    @Override
    public O get() {
        return transformation.apply(generator.get());
    }

    @Override
    public Stream<O> stream() {
        return generator.stream().map(transformation::apply);
    }
}
