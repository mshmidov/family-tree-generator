package ftg;

import com.google.common.collect.ImmutableMap;
import ftg.commons.exception.InitializationError;
import ftg.model.Country;
import ftg.model.culture.Culture;
import ftg.model.culture.SimpleCulture;
import ftg.model.culture.surname.RussianSurname;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public final class ResourceLoader {

    public Map<Country, Culture> loadCultures() {
        return ImmutableMap.of(Country.OMORJE,
                new SimpleCulture(
                        loadLines("./omorje/name_m.txt"),
                        loadLines("./omorje/name_f.txt"),
                        loadLines("./omorje/surname_noble.txt"),
                        loadLines("./omorje/surname_common.txt"),
                        RussianSurname::new)
        );
    }

    private List<String> loadLines(String resource) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(resource).toURI()), Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            throw new InitializationError(e);
        }
    }
}
