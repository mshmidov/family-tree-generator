package ftg.application.configuration;

import ftg.application.configuration.naming.NamingSystem;

public final class Country {

    private final String name;

    private final NamingSystem namingSystem;

    public Country(String name, NamingSystem namingSystem) {
        this.name = name;
        this.namingSystem = namingSystem;
    }

    public String getName() {
        return name;
    }

    public NamingSystem getNamingSystem() {
        return namingSystem;
    }
}
