package basket.api.handlers;

import basket.api.util.FatalError;
import basket.api.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static basket.api.app.BasketApp.getImplementingClass;

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
            InputStream stream = getImplementingClass().getResourceAsStream(Util.pathToJavaString(path));
            this.object = objectMapper.readValue(stream, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new FatalError(e);
        }

        this.convertedObject = null;
    }

    public JSONHandler(Path path, T object) throws IOException {
        this.path = path;

        Files.deleteIfExists(path);
        FileHandler.makeFile(path);

        try {
            objectMapper.writeValue(path.toFile(), object);
        } catch (JsonProcessingException e) {
            throw new FatalError(e);
        }

        this.object = object;
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

    public static <T> JSONHandler<T> create(Path path, T object) throws IOException {
        return new JSONHandler<>(path, object);
    }
}
