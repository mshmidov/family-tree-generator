package ftg.application.runners;

import ftg.model.world.World;
import ftg.simulation.Simulation;
import javaslang.collection.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SimpleRunner implements SimulationRunner {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void run(Simulation simulation, World world, int days) {
        Stream.from(0).take(days)
            .forEach(day -> simulation.nextDay(world));
    }
}
