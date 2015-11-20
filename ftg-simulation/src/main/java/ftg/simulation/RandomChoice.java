package ftg.simulation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.min;

import com.google.common.collect.ImmutableList;
import javaslang.collection.Seq;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public final class RandomChoice {

    private RandomChoice() {
    }

    public static <T> T from(T... options) {
        final int i = ThreadLocalRandom.current().nextInt(options.length);
        return options[i];
    }

    public static <T> T from(Seq<T> options) {
        final int i = ThreadLocalRandom.current().nextInt(options.length());
        return options.get(i);
    }

    public static <T extends Enum<T>> T from(Class<T> enumClass) {
        return from(enumClass.getEnumConstants());
    }

    public static <T extends Enum<T>> Stream<T> streamFrom(Class<T> enumClass) {
        final T[] options = enumClass.getEnumConstants();
        return ThreadLocalRandom.current().ints(0, options.length).mapToObj(i -> options[i]);
    }

    public static <T> Stream<T> streamFrom(T... options) {
        final T[] optionsCopy = options.clone();
        return ThreadLocalRandom.current().ints(0, optionsCopy.length).mapToObj(i -> optionsCopy[i]);
    }

    public static <T> Stream<T> streamFrom(List<T> options) {
        final List<T> optionsCopy = ImmutableList.copyOf(options);
        return ThreadLocalRandom.current().ints(0, optionsCopy.size()).mapToObj(optionsCopy::get);
    }

    public static int between(int lower, int higher) {
        checkArgument(lower < higher);
        return ThreadLocalRandom.current().nextInt(higher - lower + 1) + lower;
    }

    public static boolean byChance(double chance) {
        checkArgument(chance >= 0F && chance <= 1F);
        return ThreadLocalRandom.current().nextDouble() >= 1 - chance;
    }

    public static int fromRangeByGaussian(int listSize) {
        final int index = (int) floor(abs(ThreadLocalRandom.current().nextGaussian() * (listSize - 1) / 3));
        return min(index, listSize - 1);
    }
}
