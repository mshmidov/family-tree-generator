package ftg.application.gui.support;

public class JavaFxInitializationError extends Error {

    public JavaFxInitializationError() {
        super();
    }

    public JavaFxInitializationError(String message) {
        super(message);
    }

    public JavaFxInitializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaFxInitializationError(Throwable cause) {
        super(cause);
    }
}