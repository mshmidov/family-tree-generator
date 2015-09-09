package ftg.application;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.simulation.Simulation;
import ftg.simulation.configuration.Configuration;


public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventBus.class).toProvider(EventBus::new).in(Singleton.class);

        bind(Configuration.class)
                .toProvider(() -> new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(Simulation.class).in(Singleton.class);

    }

    @Provides
    public World getWorld(EventBus eventBus) {
        return new World(eventBus, new TredecimalDate(0));
    }

}
