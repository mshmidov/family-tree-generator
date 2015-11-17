package ftg.commons.functional;

import java.util.function.BinaryOperator;

public final class Operators {

    private Operators() {}

    public static BinaryOperator<Boolean> and() {
        return (a, b) -> a && b;
    }
}
