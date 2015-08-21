package ftg.model;

import com.google.common.collect.ImmutableMap;
import ftg.model.culture.Culture;
import ftg.model.person.Person;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class World {

    private final Map<Country, Culture> cultures;

    private final Set<Person> livingPersons = new LinkedHashSet<>();

    private final Set<Person> deadPersons = new LinkedHashSet<>();

    public World(Map<Country, Culture> cultures) {
        this.cultures = ImmutableMap.copyOf(cultures);
    }

    public Set<Country> getCountries() {
        return cultures.keySet();
    }

    public Culture getCulture(Country key) {
        return cultures.get(key);
    }
}
