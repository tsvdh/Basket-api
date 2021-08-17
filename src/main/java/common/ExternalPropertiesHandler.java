package common;

import app.Property;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class ExternalPropertiesHandler extends PropertiesHandler {

    private final File file;

    //absolute path required
    public ExternalPropertiesHandler(String path, @Nullable PropertiesHandler fallback) throws IOException {
        this.properties = new Properties();
        this.file = new File(path);

        FileHandler.makeFile(file);

        // try to construct properties
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (IOException e) {
            if (fallback == null) {
                throw e;
            } else {
                properties = fallback.properties;
                return;
            }
        }

        // add any missing values
        if (fallback != null) {
            Properties fallbackProperties = fallback.properties;

            Set<String> fallbackKeys = fallbackProperties.stringPropertyNames();
            Set<String> propertiesKeys = properties.stringPropertyNames();

            Set<String> missingKeys = new HashSet<>(fallbackKeys);
            missingKeys.removeAll(propertiesKeys);

            for (String missing : missingKeys) {
                properties.setProperty(missing, fallbackProperties.getProperty(missing));
            }
        }
    }

    public static ExternalPropertiesHandler newHandler(String fileName,
                                                       @Nullable PropertiesHandler fallbackProperties) throws IOException {
        String path = PathHandler.getExternalPropertiesPath(fileName);
        return new ExternalPropertiesHandler(path, fallbackProperties);
    }

    @Override
    public ExternalPropertiesHandler setProperty(Property property, Object value) {
        super.setProperty(property, value);
        return this;
    }

    public void save() throws IOException {
        try (FileWriter writer = new FileWriter(this.file)) {
            properties.store(writer, null);
        }
    }
}
