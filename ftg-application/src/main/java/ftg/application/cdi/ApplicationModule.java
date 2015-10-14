package ftg.application.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import ftg.application.bootstrap.ConfigurationLoader;
import ftg.commons.cdi.Identifier;
import ftg.simulation.configuration.Configuration;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class ApplicationModule extends AbstractModule {

    private final AtomicLong identifier = new AtomicLong(0);

    @Override
    protected void configure() {
        bind(Configuration.class)
                .toProvider(() -> new ConfigurationLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(new TypeLiteral<Supplier<String>>() {})
                .annotatedWith(Identifier.class)
                .toInstance(() -> String.valueOf(identifier.incrementAndGet()));

        bind(new TypeLiteral<Supplier<Long>>() {})
                .annotatedWith(Identifier.class)
                .toInstance(identifier::incrementAndGet);
    }
}
