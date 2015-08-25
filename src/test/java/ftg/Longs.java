package ftg;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(LongsSupplier.class)
public @interface Longs {

    long[] value() default {Long.MIN_VALUE, 0, Long.MAX_VALUE};
}

