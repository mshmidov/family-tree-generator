package ftg.application.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import ftg.application.bootstrap.simulation.SimulationConfigLoader;
import ftg.commons.cdi.Identifier;
import ftg.simulation.configuration.SimulationConfiguration;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class ApplicationModule extends AbstractModule {

    private final String uniqueIdPart = UUID.randomUUID().toString();

    private final AtomicLong identifier = new AtomicLong(0);

    @Override
    protected void configure() {
        bind(SimulationConfiguration.class)
                .toProvider(() -> new SimulationConfigLoader("./config/").loadConfiguration("simulation-config.yml"))
                .in(Singleton.class);

        bind(new TypeLiteral<Supplier<String>>() {})
            .annotatedWith(Identifier.class)
            .toInstance(() -> uniqueIdPart + String.valueOf(identifier.incrementAndGet()));
    }
}
