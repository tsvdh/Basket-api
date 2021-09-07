package basket.api.common;

import basket.api.app.Property;
import java.util.Properties;

public abstract class PropertiesHandler {

    Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public Object getProperty(Property property) {
        String value = properties.getProperty(property.toString());
        return property.getParser().apply(value);
    }

    public PropertiesHandler setProperty(Property property, Object value) {
        properties.setProperty(property.toString(), String.valueOf(value));
        return this;
    }
}
