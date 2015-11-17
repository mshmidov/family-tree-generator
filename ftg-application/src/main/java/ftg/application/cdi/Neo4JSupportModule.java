package ftg.application.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ftg.graph.config.Neo4jConfigLoader;
import ftg.graph.config.Neo4jConfiguration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4JSupportModule extends AbstractModule {

    @Override
    protected void configure() {

        final Neo4jConfiguration neo4jConfiguration = new Neo4jConfigLoader("./config/").loadConfiguration("neo4j-config.yml");
        bind(Neo4jConfiguration.class).toInstance(neo4jConfiguration);

        bind(SessionFactory.class).toInstance(new SessionFactory("ftg.graph.model"));
    }

    @Provides
    public Session neo4jSession(SessionFactory sessionFactory, Neo4jConfiguration neo4jConfiguration) {
        return sessionFactory.openSession(neo4jConfiguration.getUrl(), neo4jConfiguration.getUsername(), neo4jConfiguration.getPassword());
    }

}
