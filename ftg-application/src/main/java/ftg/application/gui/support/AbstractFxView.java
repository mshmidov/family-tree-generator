package ftg.application.gui.support;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.InputStream;
import java.util.Objects;

public abstract class AbstractFxView<T> {

    @Inject
    private FXMLLoader fxmlLoader;

    private final String fxmlFile;

    protected AbstractFxView(String fxmlFile) {
        this.fxmlFile = fxmlFile;
    }

    public final LoadedView<T> load() {

        try (InputStream fxml = ClassLoader.getSystemResourceAsStream(fxmlFile)) {
            final Parent load = fxmlLoader.load(Objects.requireNonNull(fxml, "Cannot find " + fxmlFile));
            return new LoadedView<>(load, fxmlLoader.getController());
        } catch (Exception e) {
            throw new JavaFxInitializationError(e);
        }
    }
}
