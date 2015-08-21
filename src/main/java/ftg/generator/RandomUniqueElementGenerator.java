package ftg.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUniqueElementGenerator<T> implements LimitedGenerator<T> {

    private final List<T> elements;
    private final Random random = new Random();

    public RandomUniqueElementGenerator(List<T> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public T get() {
        final int i = random.nextInt(elements.size());
        return elements.remove(i);
    }

    @Override
    public int elementsLeft() {
        return elements.size();
    }
}
