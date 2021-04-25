package api.common;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ExternalPropertiesHandler extends PropertiesHandler {

    private final File file;

    //absolute path required
    public ExternalPropertiesHandler(String path, @Nullable PropertiesHandler fallback) {
        this.properties = new Properties();
        this.file = new File(path);

        try {
            FileHandler.makeFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: add visual warning
        }

        // try to construct properties
        try {
            properties.load(new FileReader(file));
        } catch (Exception e) {
            if (fallback == null) {
                throw new RuntimeException(e); // TODO: add visual warning
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

    public static ExternalPropertiesHandler newHandler(String fileName, String appName,
                                                       @Nullable PropertiesHandler fallbackProperties) {
        String path = PathHandler.getExternalPropertiesPath(fileName, appName);
        return new ExternalPropertiesHandler(path, fallbackProperties);
    }

    public void saveProperties() {
        try {
            properties.store(new FileWriter(this.file), null);
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: add visual warning
        }
    }
}
