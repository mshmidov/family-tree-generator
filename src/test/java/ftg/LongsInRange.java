package ftg;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(LongRangeSupplier.class)
public @interface LongsInRange {

    long from() default -100;

    long to() default 100;

    long step() default 1;

}

