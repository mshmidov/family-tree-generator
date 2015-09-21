package ftg.application.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.commons.cdi.PersonCounter;
import ftg.simulation.configuration.Configuration;

import java.util.concurrent.atomic.AtomicLong;


public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class)
                .toProvider(() -> new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(AtomicLong.class).annotatedWith(PersonCounter.class).toInstance(new AtomicLong(0));
    }
}
