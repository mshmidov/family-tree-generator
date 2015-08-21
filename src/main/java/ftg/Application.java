package ftg;

import com.google.common.collect.ImmutableMap;
import ftg.exception.InitializationError;
import ftg.model.Country;
import ftg.model.World;
import ftg.model.culture.SimpleCulture;
import ftg.model.culture.surname.RussianSurname;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Application {

    public static void main(String[] args) {

        final World world = new World(ImmutableMap.of(Country.OMORJE,
                new SimpleCulture(
                        loadLines("./omorje/name_m.txt"),
                        loadLines("./omorje/name_f.txt"),
                        loadLines("./omorje/surname_noble.txt"),
                        RussianSurname::new)
        ));

        System.out.println(world.getCulture(Country.OMORJE).surnames().get());

    }


    private static List<String> loadLines(String resource) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(resource).toURI()), Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            throw new InitializationError(e);
        }
    }

}
