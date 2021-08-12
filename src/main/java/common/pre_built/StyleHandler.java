package common.pre_built;

import app.NotifyException;
import common.PathHandler;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

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

    public enum PreBuiltStyle {
        DEFAULT, DEFAULT_NO_FOCUS, JMETRO
    }

    private final String fileName;
    private final Location location;
    private final boolean useJMetro;

    public StyleHandler() {
        this(true, null, null);
    }

    public StyleHandler(String fileName, Location location) {
        this(false, fileName, location);
    }

    public StyleHandler(boolean useJMetro, String fileName, Location location) {
        this.useJMetro = useJMetro;
        this.fileName = fileName;
        this.location = location;
    }

    public static StyleHandler with(PreBuiltStyle preBuiltStyle) {
        switch (preBuiltStyle) {
            case DEFAULT, DEFAULT_NO_FOCUS -> {return new StyleHandler(preBuiltStyle.name(), Location.EXTERNAL);}
            case JMETRO -> {return new StyleHandler();}
            default -> throw new RuntimeException("This should never be thrown");
        }
    }

    private Optional<String> getStyleSheetPath() {
        if (location == null) {
            return Optional.empty();
        }

        String path;
        switch (location) {
            case INTERNAL -> {
                String relativePath = location.pathGetter.apply(fileName);
                try {
                    path = requireNonNull(getImplementingClass().getResource(relativePath)).toExternalForm();
                } catch (NullPointerException e) {
                    throw new NotifyException("Unable to get internal stylesheet at: " + relativePath);
                }
            }
            case EXTERNAL -> {
                path = "file:/" + location.pathGetter.apply(fileName);
            }
            default -> throw new RuntimeException("This should never be thrown");
        }
        return Optional.of(path);
    }

    public void applyStyleToApplication() {
        Window.getWindows().addListener((ListChangeListener<? super Window>) event -> {
            event.next();
            for (Window window : event.getAddedSubList()) {

                Stage stage;
                try {
                    stage = (Stage) window;
                } catch (ClassCastException e) {
                    continue;
                }

                styleStage(stage);
            }
        });
    }

    public void styleStage(Stage stage) {
        Optional<String> optionalPath = this.getStyleSheetPath();

        Parent root = stage.getScene().getRoot();
        if (useJMetro) {
            root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            new JMetro(root, Style.DARK);
        }
        optionalPath.ifPresent(path -> root.getStylesheets().add(path));

        setIcon(stage);
    }

    public static void setIcon(Stage stage) {
        try {
            String path = PathHandler.getIconPath();
            InputStream iconStream = requireNonNull(getImplementingClass().getResourceAsStream(path));
            stage.getIcons().clear();
            stage.getIcons().add(new Image(iconStream));
        } catch (Exception ignored) {}
    }
}
