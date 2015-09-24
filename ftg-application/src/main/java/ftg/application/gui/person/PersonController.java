package ftg.application.gui.person;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractController;
import ftg.model.person.Person;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;

public final class PersonController extends AbstractController<PersonView> {


    @Inject
    public PersonController(FXMLLoader fxmlLoader) {
        super(fxmlLoader, "fx/person.fxml");


    }


    public SimpleObjectProperty<Person> personProperty() {
        return view.personProperty();
    }
}
