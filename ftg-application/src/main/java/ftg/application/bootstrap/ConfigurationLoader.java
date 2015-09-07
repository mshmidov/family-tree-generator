package ftg.application.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.collect.ImmutableList;
import ftg.application.bootstrap.configfile.ConfigurationFile;
import ftg.application.bootstrap.configfile.NamingSystemConfig;
import ftg.commons.exception.InitializationError;
import ftg.simulation.configuration.Country;
import ftg.simulation.configuration.naming.CulturalNaming;
import ftg.simulation.configuration.naming.NamingLogic;
import ftg.simulation.configuration.naming.NamingSystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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


    public List<Country> loadConfiguration(String configFile) {

        final ConfigurationFile configurationFile = loadConfigFile(configFile);


        final ImmutableList.Builder<Country> builder = ImmutableList.<Country>builder();

        configurationFile.getCountries().stream()
                .map(cfg -> new Country(
                        cfg.getName(),
                        createNamingSystem(cfg.getNamingSystem()),
                        demographyLoader.loadDemography(cfg.getDemography())))
                .forEach(builder::add);

        return builder.build();
    }

    private ConfigurationFile loadConfigFile(String configFile) {

        try (InputStream input = ClassLoader.getSystemResource(path + configFile).openStream()) {
            return objectMapper.readValue(input, ConfigurationFile.class);

        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    private NamingSystem createNamingSystem(NamingSystemConfig cfg) {
        final NamingLogic namingLogic = CulturalNaming.valueOf(cfg.getNamingLogic().toUpperCase()).getNamingLogic();

        final List<String> maleNames = cfg.getMaleNames().stream().flatMap(this::loadLines).collect(toList());
        final List<String> femaleNames = cfg.getFemaleNames().stream().flatMap(this::loadLines).collect(toList());
        final List<String> commonSurnames = cfg.getCommonSurnames().stream().flatMap(this::loadLines).collect(toList());
        final List<String> uniqueSurnames = cfg.getUniqueSurnames().stream().flatMap(this::loadLines).collect(toList());

        return new NamingSystem(namingLogic, maleNames, femaleNames, commonSurnames, uniqueSurnames);
    }


    private Stream<String> loadLines(String resource) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(path + resource).toURI()), Charset.defaultCharset()).stream();
        } catch (IOException | URISyntaxException e) {
            throw new InitializationError(e);
        }
    }


}
