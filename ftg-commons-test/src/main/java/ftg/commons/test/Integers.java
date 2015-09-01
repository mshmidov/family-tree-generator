package ftg.commons.test;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(IntegersSupplier.class)
public @interface Integers {

    int[] value() default {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
}

