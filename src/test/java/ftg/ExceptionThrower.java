package ftg;

@FunctionalInterface
public interface ExceptionThrower {
    void throwException() throws Throwable;
}