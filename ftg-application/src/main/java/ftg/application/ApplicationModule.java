package ftg.application;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.model.World;
import ftg.model.time.TredecimalDate;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Configuration;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public Simulation getSimulation() {
        final Configuration configuration = new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml");
        final World world = new World(new TredecimalDate(0));
        return new Simulation(configuration, world);
    }
}
