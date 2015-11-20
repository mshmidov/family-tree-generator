package ftg.application.bootstrap;

import ftg.model.world.country.Country;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Set;

public final class Configuration {

    private final Set<Country> countries;

    private final Map<Country, Seq<String>> nobleSurnames;

    public Configuration(Set<Country> countries, Map<Country, Seq<String>> nobleSurnames) {
        this.countries = countries;
        this.nobleSurnames = nobleSurnames;
    }

    public Set<Country> countries() {
        return countries;
    }

    public Seq<String> nobleSurnames(Country country) {
        return nobleSurnames.get(country).get();
    }
}
