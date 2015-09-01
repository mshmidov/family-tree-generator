package ftg.commons.test;

import junit.framework.AssertionFailedError;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

public class ThrowableAssertion {

    public static ThrowableAssertion assertThrown(ExceptionThrower exceptionThrower) {
        try {
            exceptionThrower.throwException();
        } catch (Throwable caught) {
            return new ThrowableAssertion(caught);
        }
        throw new AssertionFailedError("No exception was thrown");
    }

    private final Throwable caught;

    public ThrowableAssertion(Throwable caught) {
        this.caught = caught;
    }

    public ThrowableAssertion isInstanceOf(Class<? extends Throwable> exceptionClass) {
        Assert.assertThat(caught, CoreMatchers.instanceOf(exceptionClass));
        return this;
    }

    public ThrowableAssertion hasMessage(String expectedMessage) {
        Assert.assertThat(caught.getMessage(), CoreMatchers.equalTo(expectedMessage));
        return this;
    }

    public ThrowableAssertion hasNoCause() {
        Assert.assertThat(caught.getCause(), CoreMatchers.nullValue());
        return this;
    }

    public ThrowableAssertion hasCauseInstanceOf(Class<? extends Throwable> exceptionClass) {
        Assert.assertThat(caught.getCause(), CoreMatchers.notNullValue());
        Assert.assertThat(caught.getCause(), CoreMatchers.instanceOf(exceptionClass));
        return this;
    }
}