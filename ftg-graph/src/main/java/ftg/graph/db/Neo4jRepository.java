package ftg.graph.db;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.transaction.Transaction;

import java.util.HashMap;
import java.util.List;

public class Neo4jRepository {

    private final Session session;

    @Inject
    public Neo4jRepository(Session session) {
        this.session = session;
    }

    public Transaction beginTransaction() {
        return session.beginTransaction();
    }

    public World createWorldRoot() {
        final World world = new World();

        try (final Transaction transaction = session.beginTransaction()) {
            session.save(world);
            final World saved = session.load(World.class, world.getGraphId());
            transaction.commit();

            return saved;
        }
    }


    public <T extends DomainObject> T save(T entity, Class<T> entityClass) {
        session.save(entity);
        return session.load(entityClass, entity.getGraphId());
    }

    public <T extends DomainObject> void save(T... entities) {
        try (final Transaction transaction = session.beginTransaction()) {
            for (T entity : entities) {
                session.save(entity);
            }
            transaction.commit();
        }
    }

    public <T extends DomainObject> void remove(T... entities) {
        try (final Transaction transaction = session.beginTransaction()) {
            for (T entity : entities) {
                session.delete(entity);
            }
            transaction.commit();
        }
    }

    public Person queryPerson(String worldId, String modelId) {
        return session.queryForObject(Person.class,
            String.format("MATCH (w:World{id:\"%s\"})-->(:Population)-->(p:Person{modelId:%s}) RETURN p", worldId, modelId),
            new HashMap<>());
    }

    public List<Person> queryLivingPersons(World world) {
        return Lists.newArrayList(
            session.query(Person.class,
                "MATCH (w:World{generated:\"%s\"})-->(:Population)-->(p:Person) WHERE NOT (p)-[:IS]->(:DEAD) RETURN p",
                new HashMap<>()));
    }

    public List<Person> queryUnmarriedFemales(World world) {
        return Lists.newArrayList(
            session.query(Person.class,
                "MATCH (w:World{generated:\"%s\"})-->(:Population)-->(p:Person{sex:\"FEMALE\"}) WHERE NOT (p)-->(:MARRIAGE) AND NOT (p)-[:IS]->(:DEAD) RETURN p",
                new HashMap<>()));
    }

    public List<Person> queryUnmarriedMales(World world) {
        return Lists.newArrayList(
            session.query(Person.class,
                "MATCH (w:World{generated:\"%s\"})-->(:Population)-->(p:Person{sex:\"FEMALE\"}) WHERE NOT (p)-->(:MARRIAGE) AND NOT (p)-[:IS]->(:DEAD) RETURN p",
                new HashMap<>()));
    }

    public List<Person> queryMarriedNonPregnantFemales(World world) {
        return Lists.newArrayList(
            session.query(Person.class,
                "MATCH (w:World{generated:\"%s\"})-->(:Population)-->(p:Person{sex:\"FEMALE\"})-->(:MARRIAGE) WHERE NOT (p)-[:IS]->(:PREGNANT) AND NOT (p)-[:IS]->(:DEAD)RETURN p",
                new HashMap<>()));
    }

    public List<Person> queryMarriedPregnantFemales(World world) {
        return Lists.newArrayList(
            session.query(Person.class,
                "MATCH (w:World{generated:\"%s\"})-->(:Population)-->(p:Person{sex:\"FEMALE\"})-->(:MARRIAGE) WHERE (p)-[:IS]->(:PREGNANT) AND NOT (p)-[:IS]->(:DEAD) RETURN p",
                new HashMap<>()));
    }
}
