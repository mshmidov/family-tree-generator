package ftg.commons.functional;

import java.util.function.Function;

public final class BooleanLogic {

    private BooleanLogic() {
    }

    public static <T> Function<? super T, Boolean> not(Function<? super T, Boolean> f) {
        return arg -> !f.apply(arg);
    }
}
