package api.common.pre_built;

import api.common.PathHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class StyleHandler {

    public enum Location {

        INTERNAL(PathHandler::getInternalCSS),
        EXTERNAL(PathHandler::getExternalCSS);

        Location(Function<String, String> pathGetter) {
            this.pathGetter = pathGetter;
        }

        private final Function<String, String> pathGetter;
    }

    public static void applyStyle(Scene scene, String fileName, Location location) {
        String path;

        switch (location) {
            case INTERNAL -> {
                String relativePath = location.pathGetter.apply(fileName);
                try {
                    path = StyleHandler.class.getResource(relativePath).toExternalForm();
                } catch (NullPointerException e) {
                    throw new RuntimeException(e); // TODO: add visual warning
                }
            }
            case EXTERNAL -> path = location.pathGetter.apply(fileName);
            default -> throw new RuntimeException("This should never be thrown");
        }

        Parent root = scene.getRoot();
        root.setId("background");
        root.getStylesheets().add(path);
    }
}
