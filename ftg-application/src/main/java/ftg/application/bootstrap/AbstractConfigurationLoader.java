package ftg.application.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import ftg.commons.exception.InitializationError;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractConfigurationLoader {

    protected final String path;
    protected final ObjectMapper objectMapper;

    public AbstractConfigurationLoader(String path) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModule(new MrBeanModule());
        this.path = path;
    }

    protected <T> T loadConfigurationClass(String configFile, Class<T> configurationClass) {

        try (InputStream input = ClassLoader.getSystemResource(path + configFile).openStream()) {
            return objectMapper.readValue(input, configurationClass);

        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }
}
