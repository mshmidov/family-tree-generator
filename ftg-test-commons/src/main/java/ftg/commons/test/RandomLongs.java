package ftg.commons.test;

import org.junit.experimental.theories.ParametersSuppliedBy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ParametersSuppliedBy(RandomLongsSupplier.class)
public @interface RandomLongs {

    long value() default 3;

}

