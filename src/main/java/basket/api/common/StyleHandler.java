package basket.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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
import org.jetbrains.annotations.Nullable;
import basket.api.prebuilt.Message;
import basket.api.util.Util;

import static basket.api.app.BasketApp.getImplementingClass;
import static java.util.Objects.requireNonNull;

public class StyleHandler {

    public enum Location {

        INTERNAL(PathHandler::getInternalCSS),
        EXTERNAL(PathHandler::getExternalCSS);

        Location(Function<String, Path> pathGetter) {
            this.pathGetter = pathGetter;
        }

        private final Function<String, Path> pathGetter;
    }

    public enum PreBuiltStyle {
        DEFAULT, DEFAULT_NO_FOCUS, JMETRO
    }

    private final String fileName;
    private final Location location;

    private final @Nullable JMetro jMetro;

    public StyleHandler() {
        this(true, Style.LIGHT, null, null);
    }

    public StyleHandler(Style jMetroStyle) {
        this(true, jMetroStyle, null, null);
    }

    public StyleHandler(String fileName, Location location) {
        this(false, null, fileName, location);
    }

    public StyleHandler(boolean useJMetro, Style jMetroStyle, String fileName, Location location) {
        this.fileName = fileName;
        this.location = location;

        if (useJMetro) {
            this.jMetro = new JMetro(jMetroStyle);
        } else {
            this.jMetro = null;
        }
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
                Path relativePath = location.pathGetter.apply(fileName);
                try {
                    path = requireNonNull(getImplementingClass().getResource(Util.pathToJavaPath(relativePath))).toExternalForm();
                } catch (NullPointerException e) {
                    try {
                        new Message("Unable to get internal stylesheet at: " + relativePath, true);
                    } catch (IllegalStateException ignored) {}
                    return Optional.empty();
                }
            }
            case EXTERNAL -> path = "file:/" + location.pathGetter.apply(fileName);
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
        Parent root = stage.getScene().getRoot();
        if (jMetro != null) {
            root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            jMetro.setParent(root);
        }

        Optional<String> optionalPath = this.getStyleSheetPath();
        optionalPath.ifPresent(path -> root.getStylesheets().add(path));

        setIcon(stage);
    }

    public void reStyleJMetro(Style jMetroStyle) {
        if (jMetro != null) {
            jMetro.setStyle(jMetroStyle);

            for (Window window : Window.getWindows()) {
                Stage stage;
                try {
                    stage = (Stage) window;
                } catch (ClassCastException e) {
                    continue;
                }
                Parent root = stage.getScene().getRoot();
                jMetro.setParent(root);
            }
        }
    }

    public static void setIcon(Stage stage) {
        Path path = PathHandler.getIconPath();

        try (InputStream in = getImplementingClass().getResourceAsStream(Util.pathToJavaPath(path))) {
            InputStream iconStream = requireNonNull(in);

            stage.getIcons().clear();
            stage.getIcons().add(new Image(iconStream));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            System.err.println("Could not find app icon");
            e.printStackTrace();
        }
    }
}
