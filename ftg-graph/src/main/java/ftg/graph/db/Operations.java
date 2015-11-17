package ftg.graph.db;

import ftg.graph.model.DomainObject;
import ftg.graph.model.person.Family;
import ftg.graph.model.person.Person;
import ftg.graph.model.person.Surname;
import ftg.graph.model.person.Woman;
import ftg.graph.model.world.Country;
import ftg.graph.model.world.World;
import org.neo4j.ogm.session.Session;

import java.util.Optional;
import java.util.function.Supplier;

public final class Operations {

    private final Supplier<Session> sessionSupplier;
    private final Cache cache;
    private final String worldId;
    private final Queries queries;

    public Operations(Supplier<Session> sessionSupplier, Cache cache, String worldId, Queries queries) {
        this.sessionSupplier = sessionSupplier;
        this.cache = cache;
        this.worldId = worldId;
        this.queries = queries;
    }

    public void createCountry(Country country) {
        final World world = queries.getWorld();
        world.getGeography().getCountries().add(country);
        sessionSupplier.get().save(world);
    }

    public Family getOrCreateFamily(Surname surname) {
        final Family family = queries.getFamily(surname);
        return Optional.ofNullable(family).orElseGet(() -> {
            final World world = queries.getWorld();
            final Family newFamily = new Family(worldId, surname);
            world.getPopulation().addFamily(newFamily);
            sessionSupplier.get().save(world);
            return newFamily;
        });
    }

    public Person createPerson(Person person) {
        sessionSupplier.get().save(person);
        final World world = queries.getWorld();
        world.getPopulation().getAllPeople().addPerson(person);
        sessionSupplier.get().save(world);

        cache.cachePerson(person);

        return person;
    }

    public <T extends DomainObject> T save(T domainObject, Class<T> domainObjectClass) {
        sessionSupplier.get().save(domainObject);
        final T saved = sessionSupplier.get().load(domainObjectClass, domainObject.getGraphId());

        if (saved instanceof Person) {
            cache.cachePerson((Person) saved);
        }

        return saved;
    }

    public void save(DomainObject... domainObjects) {
        for (DomainObject domainObject : domainObjects) {
            sessionSupplier.get().save(domainObject);

            if (domainObject instanceof Person) {
                cache.cachePerson((Person) domainObject);
            }
        }
    }

    public void removePregnancy(Woman woman) {
        sessionSupplier.get().delete(woman.getPregnancy());
        woman.setPregnancy(null);
        cache.cachePerson(woman);
    }

    public void delete(DomainObject... domainObjects) {
        for (DomainObject domainObject : domainObjects) {
            sessionSupplier.get().delete(domainObject);
        }
    }
}
