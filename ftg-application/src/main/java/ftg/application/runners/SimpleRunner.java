package ftg.application.runners;

import com.google.inject.Inject;
import ftg.model.event.EventFactory;
import ftg.model.world.World;
import ftg.simulation.Simulation;
import javaslang.collection.Stream;

public final class SimpleRunner implements SimulationRunner {

    private final EventFactory eventFactory;

    @Inject
    public SimpleRunner(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public void run(Simulation simulation, World world, int days) {
        Stream.from(0).take(days)
            .forEach(day -> simulation.nextDay(world));
    }
}
