package common;

import app.NotifyException;
import app.Property;

import java.util.Properties;

import static app.App.getImplementingClass;


public class InternalPropertiesHandler extends PropertiesHandler {

    // root of the path must be the resources folder
    public InternalPropertiesHandler(String path) {
        this.properties = new Properties();
        try {
            properties.load(getImplementingClass().getResourceAsStream(path));
        } catch (Exception e) {
            throw new NotifyException("Unable to load internal properties at: " + path);
        }
    }

    public static InternalPropertiesHandler newHandler(String fileName) {
        String path = PathHandler.getInternalPropertiesPath(fileName);
        return new InternalPropertiesHandler(path);
    }

    @Override
    public void setProperty(Property property, Object value) {
        throw new RuntimeException("Can not set properties for read only files");
    }
}
