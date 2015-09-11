package ftg.application.gui.dashboard;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import ftg.model.world.PersonIntroductionEvent;
import ftg.simulation.RandomModel;
import ftg.simulation.Simulation;
import ftg.simulation.SimulationStepEvent;
import ftg.simulation.configuration.Configuration;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.NamingSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import javax.inject.Provider;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;

public class DashboardController {

    private final EventBus eventBus;
    private final Configuration configuration;
    private final Provider<Simulation> simulationProvider;

    private final RandomModel randomModel = new RandomModel();

    private final IntegerProperty livingPersonsCount = new SimpleIntegerProperty();
    private final ObjectProperty<ObservableList<Person>> livingPersons = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<Person>> deadPersons = new SimpleObjectProperty<>();

    private Simulation simulation;

    @Inject
    public DashboardController(EventBus eventBus, Configuration configuration, Provider<Simulation> simulationProvider) {
        this.eventBus = eventBus;
        this.configuration = configuration;
        this.simulationProvider = () -> {
            final Simulation simulation = simulationProvider.get();

            livingPersonsCount.bind(Bindings.size(simulation.getWorld().getLivingPersons()));
            livingPersons.setValue(simulation.getWorld().getLivingPersons());
            deadPersons.setValue(simulation.getWorld().getDeadPersons());

            return simulation;
        };
    }

    public IntegerProperty livingPersonsCountProperty() {
        return livingPersonsCount;
    }

    public ObjectProperty<ObservableList<Person>> livingPersonsProperty() {
        return livingPersons;
    }


    public ObjectProperty<ObservableList<Person>> deadPersonsProperty() {
        return deadPersons;
    }

    public void newSimulation() {
        simulation = simulationProvider.get();
    }

    public void populateSimulation(int people) {
        simulationMustExist();
        final IntegerRange age = IntegerRange.inclusive(17, 50);

        for (Country country : configuration.getCountries()) {
            final NamingSystem namingSystem = country.getNamingSystem();

            final List<PersonIntroductionEvent> events = namingSystem.getUniqueSurnames().stream()
                    .limit(people)
                    .map(surname -> randomModel.newPerson(country, surname, age, simulation.getCurrentDate()))
                    .map(PersonIntroductionEvent::new).collect(Collectors.toList());

            events.forEach(eventBus::post);
        }
    }

    public void runSimulation(int years) {
        simulationMustExist();

        LongStream.range(0, years * DAYS_IN_YEAR)
                .forEach(i -> eventBus.post(new SimulationStepEvent()));
    }

    private Simulation simulationMustExist() {
        if (simulation == null) {
            simulation = simulationProvider.get();
        }

        return simulation;
    }
}


