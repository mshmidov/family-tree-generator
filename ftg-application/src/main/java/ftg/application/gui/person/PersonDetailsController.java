package ftg.application.gui.person;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractController;
import ftg.model.person.Person;
import ftg.model.time.TredecimalDate;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;

public final class PersonDetailsController extends AbstractController<PersonDetailsView> {

    @Inject
    public PersonDetailsController(FXMLLoader fxmlLoader) {
        super(fxmlLoader, "fx/person-details.fxml");
    }

    public SimpleObjectProperty<Person> personProperty() {
        return view.personProperty();
    }

    public SimpleObjectProperty<TredecimalDate> currentDateProperty() {
        return view.currentDateProperty();
    }
}
