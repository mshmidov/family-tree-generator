package ftg.application.neo4j;

import com.google.inject.Inject;
import ftg.application.neo4j.schema.WorldRoot;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.Transaction;

public class Neo4jRepository {

    private final Session session;

    @Inject
    public Neo4jRepository(Session session) {
        this.session = session;
    }

    public WorldRoot createWorldRoot() {
        final WorldRoot worldRoot = new WorldRoot();

        try (final Transaction transaction = session.beginTransaction()) {
            session.save(worldRoot, 0);
            final WorldRoot saved = session.load(WorldRoot.class, worldRoot.getId());
            transaction.commit();

            return saved;
        }
    }
}
