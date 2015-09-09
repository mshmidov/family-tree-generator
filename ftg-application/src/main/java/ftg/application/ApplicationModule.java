package ftg.application;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.model.World;
import ftg.model.time.TredecimalDate;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Configuration;


public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class)
                .toProvider(() -> new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(World.class).toProvider(() -> new World(new TredecimalDate(0)));

        bind(Simulation.class).in(Singleton.class);
    }

}
