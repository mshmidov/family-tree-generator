package ftg.application.runners;

import ftg.model.world.World;
import ftg.simulation.Simulation;
import javaslang.collection.Stream;

public final class SimpleRunner implements SimulationRunner {

    @Override
    public void run(Simulation simulation, World world, int days) {
        Stream.from(0).take(days)
            .forEach(day -> simulation.nextDay(world));
    }
}
