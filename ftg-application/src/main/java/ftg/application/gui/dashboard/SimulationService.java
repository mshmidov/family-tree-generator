package ftg.application.gui.dashboard;

import com.google.inject.Inject;
import ftg.model.world.Event;
import ftg.model.world.World;
import ftg.simulation.Simulation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static ftg.commons.MorePreconditions.checkedArgument;
import static ftg.model.time.TredecimalCalendar.DAYS_IN_YEAR;
import static java.util.Objects.requireNonNull;

public final class SimulationService extends Service<Collection<Event>> {

    private final ObjectProperty<WorkOrder> workOrder = new SimpleObjectProperty<>();

    private Simulation simulation;


    @Inject
    public SimulationService(Provider<Simulation> simulationFactory) {
        simulation = simulationFactory.get();

        workOrder.addListener((observable, oldValue, newValue) -> {
                    requireNonNull(newValue);
                    checkState(!isRunning());
                    if (simulation == null) {
                        simulation = simulationFactory.get();
                    }
                    reset();
                    start();
                }
        );
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public ObjectProperty<WorkOrder> workOrderProperty() {
        return workOrder;
    }

    public void invalidateCaches() {
        checkState(!isRunning());
        simulation = null;
    }

    @Override
    protected Task<Collection<Event>> createTask() {
        return new SimulationTask(workOrder.get(), simulation);
    }

    private static final class SimulationTask extends Task<Collection<Event>> {

        private final WorkOrder workOrder;

        private final Simulation simulation;

        private SimulationTask(WorkOrder workOrder, Simulation simulation) {
            this.workOrder = workOrder;
            this.simulation = simulation;
        }

        @Override
        protected Collection<Event> call() throws Exception {

            final World internalWorld = new World(workOrder.getWorld());

            final List<Event> producedEvents = new ArrayList<>();

            final int totalDays = workOrder.getYears() * DAYS_IN_YEAR;

            for (int i = 0; i < totalDays; i++) {
                producedEvents.addAll(simulation.nextDay(internalWorld));
                updateProgress(i, totalDays);
            }

            return producedEvents;
        }
    }

    public static final class WorkOrder {

        private final int years;

        private final World world;

        public WorkOrder(int years, World world) {
            this.years = checkedArgument(years, y -> y > 0);
            this.world = requireNonNull(world);
        }

        public int getYears() {
            return years;
        }

        public World getWorld() {
            return world;
        }
    }
}
