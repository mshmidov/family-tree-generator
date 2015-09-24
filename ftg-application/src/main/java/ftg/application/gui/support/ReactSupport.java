package ftg.application.gui.support;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public final class ReactSupport {

    private static final Logger LOGGER = LogManager.getLogger(ReactSupport.class);

    private ReactSupport() {
    }

    public static <T> MonadicBinding<String> selectMonadicString(ObservableValue<T> observable, Function<T, String> getter) {
        return EasyBind.select(observable).selectObject(p -> new SimpleStringProperty(getter.apply(p))).orElse("");
    }

    public static <T, V> MonadicBinding<V> selectMonadicObject(ObservableValue<T> observable, Function<T, V> getter, V orElse) {
        return EasyBind.select(observable).selectObject(p -> new SimpleObjectProperty<>(getter.apply(p))).orElse(orElse);
    }

    public static <T, V> MonadicBinding<V> selectMonadicProperty(ObservableValue<T> observable, Function<T, ObservableValue<V>> getter) {
        return EasyBind.select(observable).selectObject(getter::apply).orElse(new SimpleObjectProperty<>());
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
