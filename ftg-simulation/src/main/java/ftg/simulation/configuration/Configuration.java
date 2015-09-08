package ftg.simulation.configuration;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Configuration {

    private final List<Country> countries;

    public Configuration(List<Country> countries) {
        this.countries = ImmutableList.copyOf(countries);
    }

    public List<Country> getCountries() {
        return countries;
    }
}
