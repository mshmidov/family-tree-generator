package ftg.simulation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.min;

import com.google.common.collect.ImmutableList;
import ftg.commons.range.IntegerRange;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateRange;
import it.unimi.dsi.util.XorShift128PlusRandom;
import javaslang.collection.Seq;

import java.util.List;
import java.util.stream.Stream;

public final class RandomChoice {

    private static final XorShift128PlusRandom RANDOM = new XorShift128PlusRandom();

    private RandomChoice() {
    }

    public static <T> T from(T... options) {
        final int i = RANDOM.nextInt(options.length);
        return options[i];
    }

    public static <T> T from(Seq<T> options) {
        final int i = RANDOM.nextInt(options.length());
        return options.get(i);
    }

    public static <T extends Enum<T>> T from(Class<T> enumClass) {
        return from(enumClass.getEnumConstants());
    }

    public static TredecimalDate from(TredecimalDateRange range) {
        return between(range.getFirst(), range.getLast());
    }

    public static int from(IntegerRange range) {
        return between(range.getFirst(), range.getLast());
    }

    public static <T> Stream<T> streamFrom(List<T> options) {
        final List<T> optionsCopy = ImmutableList.copyOf(options);
        return RANDOM.ints(0, optionsCopy.size()).mapToObj(optionsCopy::get);
    }

    public static int between(int lower, int higher) {
        checkArgument(lower < higher);
        return RANDOM.nextInt((higher - lower) + 1) + lower;
    }

    public static long between(long lower, long higher) {
        checkArgument(lower < higher);
        return RANDOM.nextLong((higher - lower) + 1) + lower;
    }

    public static TredecimalDate between(TredecimalDate lower, TredecimalDate higher) {
        checkArgument(lower.isBefore(higher));
        return new TredecimalDate(between(lower.getDayOfEpoch(), higher.getDayOfEpoch()));
    }

    public static boolean byChance(double chance) {
        checkArgument(chance >= 0F && chance <= 1F);
        return RANDOM.nextDouble() >= 1 - chance;
    }

    public static int fromRangeByGaussian(int listSize) {
        final int index = (int) floor(abs(RANDOM.nextGaussian() * (listSize - 1) / 3));
        return min(index, listSize - 1);
    }
}
