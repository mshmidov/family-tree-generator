package ftg.application.cdi;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.model.time.TredecimalDate;
import ftg.model.world.World;
import ftg.simulation.configuration.Configuration;


public class ApplicationModule extends AbstractModule {

    private final EventBus eventBus = new EventBus(new LoggingSubscriberExceptionHandler("default"));

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new CustomProvisionListener(eventBus::register));

        bind(EventBus.class).toInstance(eventBus);

        bind(Configuration.class)
                .toProvider(() -> new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

    }

    @Provides
    public World getWorld() {
        return new World(new TredecimalDate(0));
    }
}
