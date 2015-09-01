package ftg.commons.generator;

public interface LimitedGenerator<T> extends Generator<T> {

    int elementsLeft();

}
