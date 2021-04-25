package api.common;

import api.app.Property;
import java.util.Properties;

abstract class PropertiesHandler {

    Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public Object getProperty(Property property) {
        String value = properties.getProperty(property.toString());
        return property.getParser().apply(value);
    }

    public void setProperty(Property property, Object value) {
        properties.setProperty(property.toString(), String.valueOf(value));
    }
}
