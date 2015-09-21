package ftg.application.gui.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public final class ReactSupport {

    private static final Logger LOGGER = LogManager.getLogger(ReactSupport.class);

    private ReactSupport() {
    }

    public static <T> Function<Future<T>, T> futureResultOr(final T defaultValue) {
        return future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.catching(e);
                return defaultValue;
            }
        };
    }
}
