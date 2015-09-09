package ftg.simulation;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.*;

public final class RandomChoice {

    private final Random random = new Random();

    public <T> T from(T... options) {
        final int i = random.nextInt(options.length);
        return options[i];
    }

    public <T> T from(List<T> options) {
        final int i = random.nextInt(options.size());
        return options.get(i);
    }

    public <T extends Enum<T>> T from(Class<T> enumClass) {
        return from(enumClass.getEnumConstants());
    }

    public <T extends Enum<T>> Stream<T> streamFrom(Class<T> enumClass) {
        final T[] options = enumClass.getEnumConstants();
        return random.ints(0, options.length).mapToObj(i -> options[i]);
    }

    public <T> Stream<T> streamFrom(T... options) {
        final T[] optionsCopy = options.clone();
        return random.ints(0, optionsCopy.length).mapToObj(i -> optionsCopy[i]);
    }

    public <T> Stream<T> streamFrom(List<T> options) {
        final List<T> optionsCopy = ImmutableList.copyOf(options);
        return random.ints(0, optionsCopy.size()).mapToObj(optionsCopy::get);
    }

    public int between(int lower, int higher) {
        checkArgument(lower < higher);
        return random.nextInt(higher - lower + 1) + lower;
    }

    public boolean byChance(double chance) {
        checkArgument(chance >= 0F && chance <= 1F);
        return random.nextDouble() >= 1 - chance;
    }

    public int fromRangeByGaussian(int listSize) {
        final int index = (int) floor(abs(random.nextGaussian() * (listSize - 1) / 3));
        return min(index, listSize - 1);
    }
}
