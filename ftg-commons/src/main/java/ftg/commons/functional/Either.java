package ftg.commons.functional;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class Either<L, R> {

    private final Optional<L> left;

    private final Optional<R> right;

    private Either(Optional<L> left, Optional<R> right) {
        checkArgument(left.isPresent() ^ right.isPresent(), "One and only one argument should be present");

        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> ofLeft(L left) {
        return new Either<>(Optional.of(left), Optional.empty());
    }

    public static <L, R> Either<L, R> ofRight(R right) {
        return new Either<>(Optional.empty(), Optional.of(right));
    }

    public static <L, R> Either<L, R> of(Optional<L> left, Optional<R> right) {
        return new Either<>(left, right);
    }

    public boolean isLeft() {
        return left.isPresent();
    }

    public boolean isRight() {
        return right.isPresent();
    }

    public Optional<L> left() {
        return left;
    }

    public Optional<R> right() {
        return right;
    }

    public <U, V> Either<U, V> mapEither(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends V> rightMapper) {
        return Either.of(left.map(leftMapper), right.map(rightMapper));
    }

    public <T> T map(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        return isLeft() ? leftMapper.apply(left.get()) : rightMapper.apply(right.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Either<?, ?> either = (Either<?, ?>) o;
        return Objects.equals(left, either.left) &&
            Objects.equals(right, either.right);
    }

    @Override
    public String toString() {
        return isLeft()
               ? String.format("Either.left[%s]", left.get())
               : String.format("Either.right[%s]", right.get());
    }
}
