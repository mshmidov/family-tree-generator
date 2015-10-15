package ftg.application.neo4j.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ftg.application.neo4j.Neo4JWorld;
import ftg.application.neo4j.config.Neo4jConfigLoader;
import ftg.application.neo4j.config.Neo4jConfiguration;
import ftg.model.world.World;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4JSupportModule extends AbstractModule {

    @Override
    protected void configure() {

        final Neo4jConfiguration neo4jConfiguration = new Neo4jConfigLoader("./config/").loadConfiguration("neo4j-config.yml");
        bind(Neo4jConfiguration.class).toInstance(neo4jConfiguration);

        bind(SessionFactory.class).toInstance(new SessionFactory("ftg.model", "ftg.application.neo4j.schema"));

        bind(World.class).to(Neo4JWorld.class).in(Singleton.class);
    }

    @Provides
    public Session neo4jSession(SessionFactory sessionFactory, Neo4jConfiguration neo4jConfiguration) {
        return sessionFactory.openSession(neo4jConfiguration.getUrl(), neo4jConfiguration.getUsername(), neo4jConfiguration.getPassword());
    }

}
