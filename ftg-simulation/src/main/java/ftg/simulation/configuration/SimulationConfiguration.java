package ftg.simulation.configuration;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SimulationConfiguration {

    private final List<SimulatedCountry> countries;

    public SimulationConfiguration(List<SimulatedCountry> countries) {
        this.countries = ImmutableList.copyOf(countries);
    }

    public List<SimulatedCountry> getCountries() {
        return countries;
    }
}
