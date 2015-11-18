package ftg.commons.functional;

import java.util.function.Predicate;

public final class BooleanLogic {

    private BooleanLogic() {
    }

    public static <T> Predicate<? super T> not(Predicate<? super T> f) {
        return f.negate();
    }
}
