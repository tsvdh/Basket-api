package common.pre_built;

import app.NotifyException;
import common.PathHandler;
import java.io.InputStream;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import static app.BasketApp.getImplementingClass;
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

    private final String fileName;
    private final Location location;

    public StyleHandler(String fileName, Location location) {
        this.fileName = fileName;
        this.location = location;
    }

    public String getStyleSheetPath() {
        switch (location) {
            case INTERNAL -> {
                String relativePath = location.pathGetter.apply(fileName);
                try {
                    return requireNonNull(getImplementingClass().getResource(relativePath)).toExternalForm();
                } catch (NullPointerException e) {
                    throw new NotifyException("Unable to get internal stylesheet at: " + relativePath);
                }
            }
            case EXTERNAL -> {
                return "file:/" + location.pathGetter.apply(fileName);
            }
            default -> throw new RuntimeException("This should never be thrown");
        }
    }

    public void applyStyleToApplication() {
        String path = this.getStyleSheetPath();
        Window.getWindows().addListener((ListChangeListener<? super Window>) event -> {
            event.next();
            for (Window window : event.getAddedSubList()) {

                Stage stage;
                try {
                    stage = (Stage) window;
                } catch (ClassCastException e) {
                    continue;
                }

                stage.getScene().getRoot().getStylesheets().add(path);
                setIcon(stage);
            }
        });
    }

    public void applyStyleToScene(Scene scene) {
        String path = this.getStyleSheetPath();
        scene.getRoot().getStylesheets().add(path);
    }

    public static void setIcon(Stage stage) {
        try {
            String path = PathHandler.getIconPath();
            InputStream iconStream = requireNonNull(getImplementingClass().getResourceAsStream(path));
            stage.getIcons().add(new Image(iconStream));
        } catch (Exception ignored) {}
    }
}
