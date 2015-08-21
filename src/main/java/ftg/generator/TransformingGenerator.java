package ftg.generator;

import com.google.common.base.Function;

public class TransformingGenerator<I, O> implements Generator<O> {

    private final Function<I, O> transformation;

    private final LimitedGenerator<I> generator;

    public TransformingGenerator(LimitedGenerator<I> generator, Function<I, O> transformation) {
        this.transformation = transformation;
        this.generator = generator;
    }

    @Override
    public O get() {
        return transformation.apply(generator.get());
    }
}
