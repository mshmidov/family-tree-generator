package ftg.application.gui.support;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class AbstractController<T> {

    protected final T view;
    protected final Parent viewRoot;

    protected AbstractController(FXMLLoader fxmlLoader, String fxmlFile) {
        final LoadedView<T> loadedView = createView(fxmlLoader, fxmlFile);
        this.view = loadedView.getController();
        this.viewRoot = loadedView.getRoot();
    }

    public final Parent getViewRoot() {
        return viewRoot;
    }

    private LoadedView<T> createView(FXMLLoader fxmlLoader, String fxmlFile) {
        try (InputStream fxml = ClassLoader.getSystemResourceAsStream(fxmlFile)) {
            final Parent parent = fxmlLoader.load(requireNonNull(fxml, "Cannot find " + fxmlFile));
            return new LoadedView<>(parent, fxmlLoader.getController());
        } catch (Exception e) {
            throw new JavaFxInitializationError(e);
        }
    }

}
