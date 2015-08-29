package ftg.generator;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class RandomChoice<T> extends RandomElementGenerator<T> {

    public static <T> RandomChoiceBuilder<T> of(T option) {
        return new RandomChoiceBuilder<>(option);
    }

    public static <T extends Enum<T>> RandomChoice<T> ofEnum(Class<T> options) {
        return new RandomChoice<>(ImmutableList.copyOf(options.getEnumConstants()));
    }

    private RandomChoice(List<T> options) {
        super(options);
    }

    public static final class RandomChoiceBuilder<T> {
        private final List<T> options = new ArrayList<>();

        RandomChoiceBuilder(T option) {
            options.add(checkNotNull(option));
        }

        public RandomChoiceBuilder<T> or(T option) {
            options.add(checkNotNull(option));
            return this;
        }

        public RandomChoice<T> build() {
            return new RandomChoice<>(options);
        }
    }
}
