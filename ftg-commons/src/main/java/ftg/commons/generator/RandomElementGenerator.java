package ftg.commons.generator;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class RandomElementGenerator<T> implements Generator<T> {

    private final List<T> elements;
    private final Random random = new Random();

    public RandomElementGenerator(List<T> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    @Override
    public T get() {
        final int i = random.nextInt(elements.size());
        return elements.get(i);
    }

    @Override
    public Stream<T> stream() {
        return random.ints(0, elements.size()).mapToObj(elements::get);
    }
}
