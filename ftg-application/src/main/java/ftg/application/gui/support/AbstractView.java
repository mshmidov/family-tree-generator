package ftg.application.gui.support;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public abstract class AbstractView implements View {

    private final Parent root;

    protected AbstractView(FXMLLoader fxmlLoader, String fxmlFile) {
        this.root = createView(fxmlLoader, fxmlFile);
    }

    private Parent createView(FXMLLoader fxmlLoader, String fxmlFile) {
        try (InputStream fxml = ClassLoader.getSystemResourceAsStream(fxmlFile)) {
            fxmlLoader.setController(this);
            return fxmlLoader.load(requireNonNull(fxml, "Cannot find " + fxmlFile));
        } catch (Exception e) {
            throw new JavaFxInitializationError(e);
        }
    }

    @Override
    public final Parent getRoot() {
        return root;
    }

}
