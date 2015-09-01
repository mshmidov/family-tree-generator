package ftg.commons.test;

@FunctionalInterface
public interface ExceptionThrower {
    void throwException() throws Throwable;
}