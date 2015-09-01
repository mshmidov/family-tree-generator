package ftg.bootstrap;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import ftg.commons.exception.InitializationError;
import ftg.commons.util.IntegerRange;
import ftg.model.person.Person;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static ftg.commons.util.MorePreconditions.checkedArgument;

public final class DemographyLoader {

    private final String path;

    public DemographyLoader(String path) {
        this.path = path;
    }

    public Table<IntegerRange, Person.Sex, Double> loadDeathRisk() {

        try (final Stream<String> lines = Files.lines(getPath("demography_death_risk.txt"))) {

            final ImmutableTable.Builder<IntegerRange, Person.Sex, Double> builder = ImmutableTable.builder();

            lines.filter(line -> !line.startsWith("#"))
                    .map(this::parseInputLine)
                    .flatMap(Arrays::stream)
                    .forEach(builder::put);

            return builder.build();

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

    private Table.Cell[] parseInputLine(String line) {
        final String[] parts = checkedArgument(line.replaceAll("\\s", "").split("\\|"), a -> a.length == 3);
        final String[] bounds = checkedArgument(parts[0].split("-|\\+"), a -> a.length == 1 || a.length == 2);
        final IntegerRange range = (bounds.length == 1)
                                   ? IntegerRange.inclusive(Integer.valueOf(bounds[0]), Integer.valueOf(bounds[0]))
                                   : IntegerRange.inclusive(Integer.valueOf(bounds[0]), Integer.valueOf(bounds[1]));

        return new Table.Cell[]{
                Tables.immutableCell(range, Person.Sex.MALE, Double.valueOf(parts[1])),
                Tables.immutableCell(range, Person.Sex.FEMALE, Double.valueOf(parts[2]))
        };
    }
}
