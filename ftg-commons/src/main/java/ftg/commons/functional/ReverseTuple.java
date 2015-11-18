package ftg.commons.functional;

import javaslang.Tuple;
import javaslang.Tuple2;

public final class ReverseTuple {

    private ReverseTuple() {
    }

    public static <L, R> Tuple2<R, L> of2(Tuple2<L,R> tuple) {
        return Tuple.of(tuple._2(), tuple._1());
    }
}
