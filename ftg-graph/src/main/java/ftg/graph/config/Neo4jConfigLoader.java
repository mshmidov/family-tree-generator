package ftg.graph.config;


import ftg.commons.bootstrap.AbstractConfigurationLoader;

public final class Neo4jConfigLoader extends AbstractConfigurationLoader {

    public Neo4jConfigLoader(String path) {
        super(path);
    }

    public Neo4jConfiguration loadConfiguration(String configFile) {
        return loadConfigurationClass(configFile, Neo4jConfiguration.class);
    }
}
