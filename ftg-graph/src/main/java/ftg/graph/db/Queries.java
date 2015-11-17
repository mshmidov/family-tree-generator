package ftg.graph.db;

import com.google.common.collect.Lists;
import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Family;
import ftg.graph.model.person.Man;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Surname;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Queries {

    private final Supplier<Session> sessionSupplier;
    private final Cache cache;
    private final String namespace;
    private final long worldGraphId;

    public Queries(Supplier<Session> sessionSupplier, Cache cache, long worldGraphId, String namespace) {
        this.sessionSupplier = sessionSupplier;
        this.cache = cache;
        this.worldGraphId = worldGraphId;
        this.namespace = namespace;
    }

    public <T extends DomainObject> T load(Class<T> objectClass, long graphId, int depth) {
        return sessionSupplier.get().load(objectClass, graphId, depth);
    }

    public World getWorld() {
        return sessionSupplier.get().load(World.class, worldGraphId, 2);
    }

    public Country getCountry(String name) {
        return sessionSupplier.get().queryForObject(Country.class,
            String.format("MATCH (c:Country{namespace:'%s', name:'%s'}) RETURN c", this.namespace, name),
            Collections.emptyMap());
    }

    public Family getFamily(Surname surname) {
        return sessionSupplier.get().queryForObject(Family.class,
            String.format("MATCH (f:Family{namespace:'%s', id:'%s'}) RETURN f", namespace, surname.getMaleForm()),
            Collections.emptyMap());
    }

    public Person getPerson(String id) {
        return cache.getPerson(id);
//        return sessionSupplier.get().queryForObject(Person.class,
//            String.format("MATCH (p:Person{namespace:'%s', id:'%s'}) RETURN p", namespace, id),
//            Collections.emptyMap());
    }

    public Man getMan(String id) {
        return cache.getMan(id);
    }

    public Woman getWoman(String id) {
        return cache.getWoman(id);
    }

    public Stream<Man> getUnmarriedMen() {
        return cache.getLivingPersons().stream()
            .filter(person -> person.getSex() == Person.Sex.MALE)
            .map(person -> (Man) person)
            .filter(man -> man.getWife() == null);

        //        return Lists.newArrayList(
        //            sessionSupplier.get().query(Man.class,
        //                String.format("MATCH (n:Man{namespace:'%s', alive:true}) WHERE NOT (n)-[:MARRIED]->(:Woman) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p",
        //                    namespace),
        //                Collections.emptyMap()));
    }

    public Stream<Woman> getUnmarriedWomen() {
        return cache.getLivingPersons().stream()
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .map(person -> (Woman) person)
            .filter(woman -> woman.getHusband() == null);

        //        return Lists.newArrayList(
        //            sessionSupplier.get().query(Woman.class,
        //                String.format(
        //                    "MATCH (n:Woman{namespace:'%s', alive:true}) WHERE NOT (n)-[:MARRIED]->(:Man) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p ORDER BY n.birthDate",
        //                    namespace),
        //                Collections.emptyMap()));
    }

    public List<Woman> getMarriedNonPregnantWomen(long bornAfter, long bornBefore) {
        return cache.getLivingPersons().stream()
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .map(person -> (Woman) person)
            .filter(woman -> woman.getHusband() != null)
            .filter(woman -> woman.getPregnancy() == null)
            .collect(Collectors.toList());

//        final String query =
//            "MATCH (n:Woman{namespace:'%s', alive:true})-[:MARRIED]->(:Man) "
//                + "WHERE (%s<=n.birthDate AND n.birthDate<=%s) "
//                + "AND NOT (n)-[:PREGNANT]->(:Pregnancy) "
//                + "WITH n MATCH p=(n)-[*0..1]-(m) "
//                + "RETURN p";
//
//        return Lists.newArrayList(
//            sessionSupplier.get().query(Woman.class,
//                String.format(query, namespace, bornAfter, bornBefore),
//                Collections.emptyMap()));
    }

    public Collection<Woman> getPregnantWomen(long conceptionAfter, long conceptionBefore) {
        return cache.getLivingPersons().stream()
            .filter(person -> person.getSex() == Person.Sex.FEMALE)
            .map(person -> (Woman) person)
            .filter(woman -> woman.getPregnancy() != null)
            .collect(Collectors.toList());

//        final String query =
//            "MATCH (n:Woman{namespace:'%s', alive:true})-[:PREGNANT]->(pregnancy:Pregnancy) "
//                + "WHERE %s<=pregnancy.conceptionDate AND pregnancy.conceptionDate<=%s "
//                + "WITH n MATCH p=(n)-[*0..1]-(m) "
//                + "RETURN p";
//        return Lists.newArrayList(
//            sessionSupplier.get().query(Woman.class,
//                String.format(query, namespace, conceptionAfter, conceptionBefore),
//                Collections.emptyMap()));
    }

    public Collection<Person> getLivingPersons() {
        return cache.getLivingPersons();
        //        return Lists.newArrayList(
        //            session.query(Person.class,
        //                String.format("MATCH (n:Person{namespace:'%s', alive:true}) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p", namespace),
        //                Collections.emptyMap()));
    }

    public boolean haveCommonAncestor(Person a, Person b, int limit) {
        final List<Person> ancestors = Lists.newArrayList(sessionSupplier.get().query(Person.class,
            String.format("match p=(a:Person{id:\"%s\"})-[r1:IS_CHILD_OF*..%s]->(ancestor:Person)<-[r2:IS_CHILD_OF*..%s]-(b:Person{id:\"%s\"}) return ancestor",
                a.getId(), limit, limit, b.getId()),
            Collections.emptyMap()));

        return !ancestors.isEmpty();
    }

    public boolean haveDirectAncestry(Person a, Person b) {
        final List<Person> ancestors = Lists.newArrayList(sessionSupplier.get().query(Person.class,
            String.format(
                "match (a:Person{id:\"%s\"}),(b:Person{id:\"%s\"}) with a, b match p1=(a)-[:IS_CHILD_OF*]->(b) optional match p2=(a)<-[:IS_CHILD_OF*]-(b) return p1,p2",
                a.getId(), b.getId()),
            Collections.emptyMap()));

        return !ancestors.isEmpty();

    }
}
