package ftg.graph.db;

import com.google.inject.Inject;
import ftg.graph.model.DomainObjectFactory;
import ftg.graph.model.event.Event;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.Transaction;

public final class SimulatedWorld {

    private final DomainObjectFactory domainObjectFactory;
    private final Session session;
    private final Queries queries;
    private final Operations operations;

    @Inject
    public SimulatedWorld(DomainObjectFactory domainObjectFactory, Session session) {
        this.domainObjectFactory = domainObjectFactory;
        this.session = session;

        final World world = createWorldRoot();
        this.queries = new Queries(session, world.getGraphId(), world.getId());
        this.operations = new Operations(session, world.getId(), queries);
    }

    public DomainObjectFactory getFactory() {
        return domainObjectFactory;
    }

    public Queries getQueries() {
        return queries;
    }

    public Operations getOperations() {
        return operations;
    }

    public void submitEvent(Event event) {
        try (final Transaction transaction = session.beginTransaction()) {
            // TODO: store event in db
            event.apply(this);
            transaction.commit();
        }
    }

    private World createWorldRoot() {
        final World world = domainObjectFactory.newWorld();

        try (final Transaction transaction = session.beginTransaction()) {
            session.save(world);
            final World saved = session.load(World.class, world.getGraphId());
            transaction.commit();

            return saved;
        }
    }

}
