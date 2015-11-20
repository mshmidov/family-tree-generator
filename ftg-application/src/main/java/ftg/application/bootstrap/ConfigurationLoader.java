package ftg.application.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import ftg.application.bootstrap.configfile.ConfigurationYAML;
import ftg.application.bootstrap.configfile.CountryYAML;
import ftg.application.bootstrap.configfile.NamingSystemYAML;
import ftg.commons.exception.InitializationError;
import ftg.model.world.country.Country;
import ftg.model.world.country.Names;
import ftg.simulation.configuration.naming.CulturalNaming;
import ftg.simulation.configuration.naming.NamesImpl;
import javaslang.Tuple;
import javaslang.collection.Array;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Seq;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class ConfigurationLoader {

    private final String path;

    private final ObjectMapper objectMapper;

    private final DemographyLoader demographyLoader;

    public ConfigurationLoader(String path) {
        this.path = path;
        this.demographyLoader = new DemographyLoader(path);
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModule(new MrBeanModule());
    }


    public Configuration loadConfiguration(String configFile) {

        final ConfigurationYAML configurationYAML = loadConfigFile(configFile);

        final HashMap<Country, Seq<String>> countriesAndSurnames = HashMap.ofAll(
            HashSet.ofAll(configurationYAML.getCountries())
                .map(cfg -> Tuple.of(
                    loadCountry(cfg),
                    cfg.getNamingSystem().getUniqueSurnames().stream().flatMap(this::loadLines).collect(Array.collector()))));

        return new Configuration(countriesAndSurnames.keySet(), countriesAndSurnames);
    }

    private ConfigurationYAML loadConfigFile(String configFile) {

        try (InputStream input = ClassLoader.getSystemResource(path + configFile).openStream()) {
            return objectMapper.readValue(input, ConfigurationYAML.class);

        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    private Country loadCountry(CountryYAML yaml) {
        return new Country(yaml.getName(),
                           createNamingSystem(yaml.getNamingSystem()),
                           demographyLoader.loadDemography(yaml.getDemography()));
    }

    private Names createNamingSystem(NamingSystemYAML yaml) {

        final Array<String> maleNames = yaml.getMaleNames().stream().flatMap(this::loadLines).collect(Array.collector());
        final Array<String> femaleNames = yaml.getFemaleNames().stream().flatMap(this::loadLines).collect(Array.collector());
        final Array<String> commonSurnames = yaml.getCommonSurnames().stream().flatMap(this::loadLines).collect(Array.collector());

        return new NamesImpl(CulturalNaming.valueOf(yaml.getNamingLogic().toUpperCase()).namingLogic,
                             maleNames, femaleNames, commonSurnames);
    }


    private Stream<String> loadLines(String resource) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(path + resource).toURI()), Charset.defaultCharset()).stream();
        } catch (IOException | URISyntaxException e) {
            throw new InitializationError(e);
        }
    }

}
