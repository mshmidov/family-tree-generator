package ftg.commons.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class RandomUniqueElementGenerator<T> implements LimitedGenerator<T> {

    private final List<T> elements;
    private final Random random = new Random();

    public RandomUniqueElementGenerator(List<T> elements) {
        this.elements = Collections.synchronizedList(new ArrayList<>(elements));
        Collections.shuffle(elements, random);
    }

    @Override
    public T get() {
        return elements.remove(0);
    }

    @Override
    public int elementsLeft() {
        return elements.size();
    }

    @Override
    public Stream<T> stream() {
        return new ArrayList<>(elements).stream().peek(elements::remove);
    }
}
