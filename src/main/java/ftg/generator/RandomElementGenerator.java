package ftg.generator;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Random;

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
}
