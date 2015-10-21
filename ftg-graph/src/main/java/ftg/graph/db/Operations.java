package ftg.graph.db;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Person;
import ftg.graph.model.world.Country;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;

import java.util.HashMap;

public final class Operations {

    private final Session session;
    private final String worldId;
    private final Queries queries;

    public Operations(Session session, String worldId, Queries queries) {
        this.session = session;
        this.worldId = worldId;
        this.queries = queries;
    }

    public void createCountry(Country country) {
        final World world = queries.getWorld();
        world.getCountries().add(country);
        session.save(world, 1);
    }

    public Person createPerson(Person person) {
        session.save(person);
        return session.queryForObject(Person.class,
            String.format("MATCH (World{id:'%s'})-[:POPULATED_BY]->(pop:Population), (p:Person{id:'%s'}) CREATE (pop)-[:CONTAINS]->(p) RETURN p",
                worldId, person.getId()),
            new HashMap<>());
    }

    public <T extends DomainObject> T save(T domainObject, Class<T> domainObjectClass) {
        session.save(domainObject);
        return session.load(domainObjectClass, domainObject.getGraphId());
    }

    public void save(DomainObject... domainObjects) {
        for (DomainObject domainObject : domainObjects) {
            session.save(domainObject);
        }
    }
}
