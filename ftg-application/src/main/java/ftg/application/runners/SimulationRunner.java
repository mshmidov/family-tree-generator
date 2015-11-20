package ftg.application.runners;

import ftg.model.world.World;
import ftg.simulation.Simulation;

public interface SimulationRunner {

    void run(Simulation simulation, World world, int days);

}
