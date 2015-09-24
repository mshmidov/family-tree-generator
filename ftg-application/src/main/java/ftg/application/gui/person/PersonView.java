package ftg.application.gui.person;

import ftg.model.person.Person;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

public final class PersonView {

    private final SimpleObjectProperty<Person> person = new SimpleObjectProperty<>();

    @FXML
    private VBox root;

    @FXML
    private Label name;

    @FXML
    public void initialize() {

        final MonadicBinding<String> personName = EasyBind.select(person).selectObject(p -> new SimpleStringProperty(p.getName())).orElse("");
        name.textProperty().bind(personName);


    }

    public SimpleObjectProperty<Person> personProperty() {
        return person;
    }
}
