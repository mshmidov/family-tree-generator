package ftg.commons.functional;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Pair<L, R> implements Map.Entry<L, R> {

    private final L left;

    private final R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public L getKey() {
        return left;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }

    public <U, V> Pair<U, V> map(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends V> rightMapper) {
        return Pair.of(leftMapper.apply(left), rightMapper.apply(right));
    }

    public <T> T reduce(BiFunction<L, R, T> f) {
        return f.apply(left, right);
    }

    @Override
    public int hashCode() {
        // see Map.Entry API specification
        return (getKey() == null ? 0 : getKey().hashCode()) ^
            (getValue() == null ? 0 : getValue().hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(getKey(), other.getKey())
                && Objects.equals(getValue(), other.getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Pair(%s, %s)", left, right);
    }
}
