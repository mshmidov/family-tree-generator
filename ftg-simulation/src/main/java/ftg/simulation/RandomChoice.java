package ftg.simulation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.min;

import com.google.common.collect.ImmutableList;
import ftg.commons.range.IntegerRange;
import ftg.model.time.TredecimalDate;
import ftg.model.time.TredecimalDateRange;
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

    public static TredecimalDate from(TredecimalDateRange range) {
        return between(range.getFirst(), range.getLast());
    }

    public static int from(IntegerRange range) {
        return between(range.getFirst(), range.getLast());
    }

    public static <T> Stream<T> streamFrom(List<T> options) {
        final List<T> optionsCopy = ImmutableList.copyOf(options);
        return ThreadLocalRandom.current().ints(0, optionsCopy.size()).mapToObj(optionsCopy::get);
    }

    public static int between(int lower, int higher) {
        checkArgument(lower < higher);
        return ThreadLocalRandom.current().nextInt(lower, higher + 1);
    }

    public static long between(long lower, long higher) {
        checkArgument(lower < higher);
        return ThreadLocalRandom.current().nextLong(lower, higher + 1);
    }

    public static TredecimalDate between(TredecimalDate lower, TredecimalDate higher) {
        checkArgument(lower.isBefore(higher));
        return new TredecimalDate(between(lower.getDayOfEpoch(), higher.getDayOfEpoch()));
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
