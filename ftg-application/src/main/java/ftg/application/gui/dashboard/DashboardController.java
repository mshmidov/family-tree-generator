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

public final class DashboardController extends AbstractController<DashboardView> {

    private static final Logger LOGGER = LogManager.getLogger(DashboardController.class);

    private ObjectProperty<World> world = new SimpleObjectProperty<>(new World());

    @Inject
    public DashboardController(FXMLLoader fxmlLoader,
                               PeopleGeneratorService peopleGeneratorService,
                               SimulationService simulationService) {

        super(fxmlLoader, "fx/dashboard.fxml");

        view.progressProperty().bind(simulationService.progressProperty());
        view.progressVisibleProperty().bind(simulationService.runningProperty());

        final BooleanBinding servicesAreRunning = Bindings.or(peopleGeneratorService.runningProperty(), simulationService.runningProperty());

        peopleGeneratorService.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.forEach(world.get()::submitEvent);
                view.setLivingPeople(world.get().getLivingPersons());
                view.setDeadPeople(world.get().getDeadPersons());
            }
        });

        simulationService.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.forEach(world.get()::submitEvent);
                view.currentDateProperty().set(simulationService.getSimulation().getCurrentDate());
                view.setLivingPeople(world.get().getLivingPersons());
                view.setDeadPeople(world.get().getDeadPersons());
            }
        });

        view.setOnNewSimulation(() -> {
            if (!servicesAreRunning.get()) {
                world.setValue(new World());
                simulationService.invalidateCaches();
            }
        });

        view.setOnPopulateSimulation(people -> {
            if (!servicesAreRunning.get()) {
                peopleGeneratorService.workOrderProperty().setValue(new PeopleGeneratorService.WorkOrder(people, simulationService.getSimulation().getCurrentDate()));
            }
        });

        view.setOnRunSimulation(years -> {
            if (!servicesAreRunning.get()) {
                simulationService.workOrderProperty().setValue(new SimulationService.WorkOrder(years, world.get()));
            }
        });
    }

}


