package common;

import app.Property;
import java.io.IOException;
import java.util.Properties;

import static app.BasketApp.getImplementingClass;


public class InternalPropertiesHandler extends PropertiesHandler {

    // root of the path must be the resources folder
    public InternalPropertiesHandler(String path) throws IOException {
        this.properties = new Properties();
        properties.load(getImplementingClass().getResourceAsStream(path));
    }

    public static InternalPropertiesHandler newHandler(String fileName) throws IOException {
        String path = PathHandler.getInternalPropertiesPath(fileName);
        return new InternalPropertiesHandler(path);
    }

    @Override
    public void setProperty(Property property, Object value) {
        throw new RuntimeException("Can not set properties for read only files");
    }
}
