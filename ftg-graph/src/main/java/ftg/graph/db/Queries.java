package ftg.graph.db;

import com.google.common.collect.Lists;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;

import java.util.HashMap;
import java.util.List;

public final class Queries {

    private final Session session;
    private final long worldGraphId;
    private final String worldId;

    public Queries(Session session, long worldGraphId, String worldId) {
        this.session = session;
        this.worldGraphId = worldGraphId;
        this.worldId = worldId;
    }

    public World getWorld() {
        return session.load(World.class, worldGraphId, 1);
    }

    public Country getCountry(String name) {
        return session.queryForObject(Country.class,
            String.format("MATCH (w:World{id:'%s'})-[:CONTAINS]->(c:Country{name:'%s'}) RETURN c", worldId, name),
            new HashMap<>());
    }

    public Person getPerson(String id) {
        return session.queryForObject(Person.class,
            String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Person{id:'%s'}) RETURN p", worldId, id),
            new HashMap<>());
    }


    public Man getMan(String id) {
        return session.queryForObject(Man.class,
            String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Man{id:'%s'}) RETURN p", worldId, id),
            new HashMap<>());

    }

    public Woman getWoman(String id) {
        return session.queryForObject(Woman.class,
            String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Woman{id:'%s'}) RETURN p", worldId, id),
            new HashMap<>());
    }

    public List<Man> getUnmarriedMen() {
        return Lists.newArrayList(
            session.query(Man.class,
                String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Man{alive:true}) WHERE NOT (p)-[:MARRIED]->(:Woman) RETURN p", worldId),
                new HashMap<>()));
    }

    public List<Woman> getUnmarriedWomen() {
        return Lists.newArrayList(
            session.query(Woman.class,
                String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Woman{alive:true}) WHERE NOT (p)-[:MARRIED]->(:Man) RETURN p", worldId),
                new HashMap<>()));
    }

    public List<Woman> getMarriedNonPregnantWomen() {
        return Lists.newArrayList(
            session.query(Woman.class,
                String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Woman{alive:true})-[:MARRIED]->(:Man) WHERE NOT (p)-[:PREGNANT]->() RETURN p",
                    worldId),
                new HashMap<>()));
    }

    public List<Woman> getPregnantWomen() {
        return Lists.newArrayList(
            session.query(Woman.class,
                String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Woman{alive:true})-[:PREGNANT]->() RETURN p", worldId),
                new HashMap<>()));
    }

    public List<Person> getLivingPersons() {
        return Lists.newArrayList(
            session.query(Person.class,
                String.format("MATCH (w:World{id:'%s'})-->(:Population)-->(p:Person{alive:true}) RETURN p", worldId),
                new HashMap<>()));
    }
}
