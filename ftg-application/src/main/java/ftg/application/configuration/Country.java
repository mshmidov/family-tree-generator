package ftg.application.configuration;

import ftg.application.configuration.demography.Demography;
import ftg.application.configuration.naming.NamingSystem;

public final class Country {

    private final String name;

    private final NamingSystem namingSystem;

    private final Demography demography;

    public Country(String name, NamingSystem namingSystem, Demography demography) {
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
