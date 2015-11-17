package ftg.graph.db;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ftg.graph.model.event.Event;
import ftg.graph.model.person.PersonFactory;
import ftg.graph.model.world.World;
import ftg.graph.model.world.WorldFactory;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.Transaction;

import java.util.Collection;

public final class SimulatedWorld {

    private final WorldFactory worldFactory;
    private final PersonFactory personFactory;

    private final Provider<Session> sessionProvider;
    private final Cache cache;
    private final Queries queries;
    private final Operations operations;

    private Session session;

    @Inject
    public SimulatedWorld(WorldFactory worldFactory, PersonFactory personFactory, Provider<Session> sessionProvider) {
        this.worldFactory = worldFactory;
        this.personFactory = personFactory;
        this.sessionProvider = sessionProvider;

        this.session = sessionProvider.get();
        final World world = createWorldRoot();
        this.cache = new Cache();
        this.queries = new Queries(() -> session, cache, world.getGraphId(), world.getId());
        this.operations = new Operations(() -> session, cache, world.getId(), queries);
    }

    public Queries getQueries() {
        return queries;
    }

    public Operations getOperations() {
        return operations;
    }

    public void newDay() {
        session = sessionProvider.get();
    }

    public <T> T submitEvent(Event<T> event) {
        try (final Transaction transaction = session.beginTransaction()) {
            final T result = event.apply(this, personFactory);
            transaction.commit();
            return result;
        }
    }

    public void submitEvents(Collection<? extends Event> events) {
        try (final Transaction transaction = session.beginTransaction()) {
            // TODO: store event in db
            events.forEach(event -> event.apply(this, personFactory));
            transaction.commit();
        }
    }

    private World createWorldRoot() {
        final World world = worldFactory.newWorld();

        try (final Transaction transaction = session.beginTransaction()) {
            session.save(world);
            final World saved = session.load(World.class, world.getGraphId());
            transaction.commit();

            return saved;
        }
    }

}
