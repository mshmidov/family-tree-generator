package ftg.application.bootstrap.simulation;

import static java.util.stream.Collectors.toList;

import ftg.application.bootstrap.simulation.configfile.NamingSystemConfig;
import ftg.application.bootstrap.simulation.configfile.SimulationConfigFile;
import ftg.commons.bootstrap.AbstractConfigurationLoader;
import ftg.commons.exception.InitializationError;
import ftg.simulation.configuration.SimulatedCountry;
import ftg.simulation.configuration.SimulationConfiguration;
import ftg.simulation.configuration.naming.CulturalNaming;
import ftg.simulation.configuration.naming.NamingLogic;
import ftg.simulation.configuration.naming.NamingSystem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class SimulationConfigLoader extends AbstractConfigurationLoader {

    private final DemographyLoader demographyLoader;

    public SimulationConfigLoader(String path) {
        super(path);
        this.demographyLoader = new DemographyLoader(path);
    }


    public SimulationConfiguration loadConfiguration(String configFile) {

        final SimulationConfigFile configurationFile = loadConfigurationClass(configFile, SimulationConfigFile.class);

        final List<SimulatedCountry> countries = new ArrayList<>();

        configurationFile.getCountries().stream()
                .map(cfg -> new SimulatedCountry(
                        cfg.getName(),
                        createNamingSystem(cfg.getNamingSystem()),
                        demographyLoader.loadDemography(cfg.getDemography())))
                .forEach(countries::add);

        return new SimulationConfiguration(countries);
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
