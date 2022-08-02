package basket.api.handlers;

import basket.api.util.FatalError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.file.Path;

public class JSONHandler<T> {

    public static final ObjectMapper objectMapper =
            JsonMapper.builder()
                    .findAndAddModules()
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .build();

    private final Path path;
    private T object;
    private Object convertedObject;

    public JSONHandler(Path path) throws IOException {
        this.path = path;

        try {
            // TODO: check if necessary
            // InputStream in = getImplementingClass().getResourceAsStream(Util.pathToJavaPath(path))
            this.object = objectMapper.readValue(path.toFile(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new FatalError(e);
        }

        this.convertedObject = null;
    }

    public T getObject() {
        if (object == null) {
            throw new IllegalStateException("Object has been converted");
        }
        return object;
    }

    public <NewT> NewT getConvertedObject(Class<NewT> newTClass) {
        if (convertedObject == null) {
            throw new IllegalStateException("Object has not been converted");
        }

        return newTClass.cast(convertedObject);
    }

    public <NewT> void convertObjectTo(Class<NewT> newTClass) {
        NewT convertedObject = objectMapper.convertValue(this.object, newTClass);

        this.object = null;
        this.convertedObject = convertedObject;
    }

    public void save() throws IOException {
        Object toWrite = object != null ? object : convertedObject;
        try {
            objectMapper.writeValue(path.toFile(), toWrite);
        } catch (JsonProcessingException e) {
            // re-throw as this is not expected and shouldn't be handled
            throw new RuntimeException(e);
        }
    }
}
