package ftg.graph.model.world;

import com.google.inject.Inject;
import ftg.commons.cdi.Identifier;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public final class WorldFactory {

    private final Supplier<String> id;

    @Inject
    public WorldFactory(@Identifier Supplier<String> id) {
        this.id = id;
    }

    public World newWorld() {
        final Population population = new Population();

        return new World(id.get(), LocalDateTime.now().toString(), population);
    }

    public Country newCountry(String name) {
        return new Country(id.get(), name);
    }
}
