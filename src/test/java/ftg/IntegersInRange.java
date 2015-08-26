package ftg;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(IntegerRangeSupplier.class)
public @interface IntegersInRange {

    int from() default -100;

    int to() default 100;

    int step() default 1;
}

