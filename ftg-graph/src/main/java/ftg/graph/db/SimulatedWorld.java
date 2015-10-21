package ftg.graph.db;

import com.google.inject.Inject;
import ftg.graph.model.event.Event;
import ftg.graph.model.person.PersonFactory;
import ftg.graph.model.world.World;
import ftg.graph.model.world.WorldFactory;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.Transaction;

public final class SimulatedWorld {

    private final WorldFactory worldFactory;
    private final PersonFactory personFactory;

    private final Session session;

    private final Queries queries;
    private final Operations operations;

    @Inject
    public SimulatedWorld(WorldFactory worldFactory, PersonFactory personFactory, Session session) {
        this.worldFactory = worldFactory;
        this.personFactory = personFactory;
        this.session = session;

        final World world = createWorldRoot();
        this.queries = new Queries(session, world.getGraphId(), world.getId());
        this.operations = new Operations(session, world.getId(), queries);
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
            event.apply(this, personFactory);
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
