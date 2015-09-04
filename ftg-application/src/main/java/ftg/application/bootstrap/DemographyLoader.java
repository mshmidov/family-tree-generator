package ftg.application.bootstrap;

import com.google.common.collect.ImmutableList;
import ftg.application.bootstrap.configfile.DemographyConfig;
import ftg.application.configuration.Demography;
import ftg.application.configuration.naming.DemographyTable;
import ftg.commons.exception.InitializationError;
import ftg.commons.range.IntegerRange;
import ftg.model.person.Person;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static ftg.commons.MorePreconditions.checkedArgument;

public final class DemographyLoader {

    private final String path;

    public DemographyLoader(String path) {
        this.path = path;
    }

    public Demography loadDemography(DemographyConfig cfg) {
        return new Demography(loadDeathRisk(cfg.getDeathRisk()));
    }

    public DemographyTable loadDeathRisk(String file) {

        try (final Stream<String> lines = Files.lines(getPath(file))) {

            final DemographyTable result = new DemographyTable();

            lines.filter(line -> !line.startsWith("#"))
                    .map(this::parseInputLine)
                    .flatMap(List::stream)
                    .forEach(triple -> result.put(triple.getLeft(), triple.getMiddle(), triple.getRight()));

            return result;

        } catch (IOException | IllegalArgumentException e) {
            throw new InitializationError(e);
        }
    }

    private Path getPath(String file) {
        try {
            return Paths.get(ClassLoader.getSystemResource(this.path + file).toURI());
        } catch (URISyntaxException e) {
            throw new InitializationError(e);
        }
    }

    private List<Triple<IntegerRange, Person.Sex, Double>> parseInputLine(String line) {
        final String[] parts = checkedArgument(line.replaceAll("\\s", "").split("\\|"), a -> a.length == 3);
        final String[] bounds = checkedArgument(parts[0].split("-|\\+"), a -> a.length == 1 || a.length == 2);
        final IntegerRange range = (bounds.length == 1)
                ? IntegerRange.inclusive(Integer.valueOf(bounds[0]), Integer.valueOf(bounds[0]))
                : IntegerRange.inclusive(Integer.valueOf(bounds[0]), Integer.valueOf(bounds[1]));

        return ImmutableList.of(ImmutableTriple.of(range, Person.Sex.MALE, Double.valueOf(parts[1])),
                ImmutableTriple.of(range, Person.Sex.FEMALE, Double.valueOf(parts[2])));
    }
}
