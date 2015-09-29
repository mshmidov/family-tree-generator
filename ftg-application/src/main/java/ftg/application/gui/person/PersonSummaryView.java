package ftg.application.gui.person;

import ftg.model.person.Person;
import ftg.model.state.Death;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import static ftg.model.time.TredecimalDateFormat.ISO;
import static org.reactfx.util.Tuples.t;

public final class PersonSummaryView extends AbstractPersonView {

    @FXML
    private VBox root;

    @FXML
    private Hyperlink name;

    @FXML
    private Label dates;

    @FXML
    public void initialize() {

        root.visibleProperty().bind(Bindings.isNotNull(personProperty()));
        root.prefWidthProperty().bind(Bindings.max(name.prefWidthProperty(), dates.prefWidthProperty()));

        person.mapToBi(p -> t(p.getName(), p.getSurname()))
                .map((name, surname) -> String.join(" ", name, surname))
                .feedTo(name.textProperty());

        final EventStream<String> birthDateString = person.map(Person::getBirthDate).map(ISO::format);
        final EventStream<String> deathDateString = personDeath.map(death -> death.map(Death::getDate).map(ISO::format).map(s -> " - " + s).orElse(""));

        EventStreams.combine(birthDateString, deathDateString, personAge)
                .map((birth, death, age) -> String.format("%s%s (%s)", birth, death, age))
                .feedTo(dates.textProperty());
    }
}
