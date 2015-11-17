package ftg.graph.model.world;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;
import ftg.commons.cdi.Namespace;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public final class WorldFactory {

    private final Supplier<String> id;
    private final String namespace;

    @Inject
    public WorldFactory(@Identifier Supplier<String> id, @Namespace String namespace) {
        this.id = id;
        this.namespace = namespace;
    }

    public World newWorld() {

        final AllPeople allPeople = new AllPeople(id.get(), namespace);
        final Population population = new Population(id.get(), namespace, allPeople);

        final Geography geography = new Geography(id.get(), namespace);

        return new World(namespace, LocalDateTime.now().toString(), population, geography);
    }

    public Country newCountry(String name) {
        return new Country(id.get(), namespace, name);
    }


}
