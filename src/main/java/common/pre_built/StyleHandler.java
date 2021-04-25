package common.pre_built;

import common.PathHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.function.Function;

public class StyleHandler {

    public enum Location {

        INTERNAL(PathHandler::getInternalCSS),
        EXTERNAL(PathHandler::getExternalCSS);

        Location(Function<String, String> pathGetter) {
            this.pathGetter = pathGetter;
        }

        private final Function<String, String> pathGetter;
    }

    private final String fileName;
    private final Location location;

    public StyleHandler(String fileName, Location location) {
        this.fileName = fileName;
        this.location = location;
    }

    public void applyStyle(Scene scene) {
        applyStyle(scene, fileName, location);
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
