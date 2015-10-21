package ftg.simulation.configuration;

import ftg.simulation.configuration.demography.Demography;
import ftg.simulation.configuration.naming.NamingSystem;

public final class SimulatedCountry {

    private final String name;

    private final NamingSystem namingSystem;

    private final Demography demography;

    public SimulatedCountry(String name, NamingSystem namingSystem, Demography demography) {
        this.name = name;
        this.namingSystem = namingSystem;
        this.demography = demography;
    }

    public String getName() {
        return name;
    }

    public NamingSystem getNamingSystem() {
        return namingSystem;
    }

    public Demography getDemography() {
        return demography;
    }
}
