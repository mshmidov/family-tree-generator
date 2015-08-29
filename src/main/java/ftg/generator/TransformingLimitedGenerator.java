package ftg.generator;


import java.util.function.Function;

public class TransformingLimitedGenerator<I, O> extends TransformingGenerator<I, O> implements LimitedGenerator<O> {

    private final LimitedGenerator<I> generator;

    public TransformingLimitedGenerator(LimitedGenerator<I> generator, Function<I, O> transformation) {
        super(generator, transformation);
        this.generator = generator;
    }

    @Override
    public int elementsLeft() {
        return generator.elementsLeft();
    }
}


