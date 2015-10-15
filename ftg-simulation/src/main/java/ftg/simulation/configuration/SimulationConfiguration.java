package ftg.simulation.configuration;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SimulationConfiguration {

    private final List<Country> countries;

    public SimulationConfiguration(List<Country> countries) {
        this.countries = ImmutableList.copyOf(countries);
    }

    public List<Country> getCountries() {
        return countries;
    }
}
