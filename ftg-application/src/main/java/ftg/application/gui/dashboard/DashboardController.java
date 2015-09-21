package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.application.gui.support.AbstractController;
import ftg.model.world.World;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DashboardController extends AbstractController<DashboardView> {

    private static final Logger LOGGER = LogManager.getLogger(DashboardController.class);

    private ObjectProperty<World> world = new SimpleObjectProperty<>(new World());

    @Inject
    public DashboardController(FXMLLoader fxmlLoader,
                               PeopleGeneratorService peopleGeneratorService,
                               SimulationService simulationService) {

        super(fxmlLoader, "fx/dashboard.fxml");

        final BooleanBinding servicesAreRunning = Bindings.or(peopleGeneratorService.runningProperty(), simulationService.runningProperty());

        peopleGeneratorService.valueProperty().addListener((observable, oldValue, newValue) -> {
            newValue.forEach(world.get()::submitEvent);
        });

        simulationService.valueProperty().addListener((observable, oldValue, newValue) -> {
            newValue.forEach(world.get()::submitEvent);
            view.setLivingPeople(world.get().getLivingPersons());
            view.setDeadPeople(world.get().getDeadPersons());
        });

        view.setOnNewSimulation(() -> {
            LOGGER.trace("onNewSimulation");
            if (!servicesAreRunning.get()) {
                world.setValue(new World());
                simulationService.invalidateCaches();
            }
        });

        view.setOnPopulateSimulation(people -> {
            LOGGER.trace("onPopulateSimulation(" + people + ")");
            if (!servicesAreRunning.get()) {
                peopleGeneratorService.workOrderProperty().setValue(new PeopleGeneratorService.WorkOrder(people, simulationService.getSimulation().getCurrentDate()));
            }
        });

        view.setOnRunSimulation(years -> {
            LOGGER.trace("onRunSimulation(" + years + ")");
            if (!servicesAreRunning.get()) {
                simulationService.workOrderProperty().setValue(new SimulationService.WorkOrder(years, world.get()));
            }
        });


    }

}


