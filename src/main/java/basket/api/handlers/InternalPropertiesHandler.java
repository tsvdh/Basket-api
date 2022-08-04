package basket.api.handlers;

import basket.api.app.Property;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import basket.api.util.Util;

import static basket.api.app.BasketApp.getImplementingClass;

@Deprecated
public class InternalPropertiesHandler extends PropertiesHandler {

    // root of the path must be the resources folder
    public InternalPropertiesHandler(Path path) throws IOException {
        this.properties = new Properties();
        try (InputStream in = getImplementingClass().getResourceAsStream(Util.pathToJavaString(path))) {
            properties.load(in);
        } catch (NullPointerException e) {
            throw new IOException(e);
        }
    }

    public static InternalPropertiesHandler newHandler(String fileName) throws IOException {
        Path path = PathHandler.getInternalDataPath(fileName);
        return new InternalPropertiesHandler(path);
    }

    @Override
    public PropertiesHandler setProperty(Property property, Object value) {
        throw new RuntimeException("Can not set properties for read only files");
    }
}
