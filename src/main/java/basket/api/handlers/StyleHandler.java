package basket.api.handlers;

import basket.api.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.Nullable;

import static basket.api.app.BasketApp.getImplementingClass;
import static java.util.Objects.requireNonNull;

public class StyleHandler {

    public enum ApiStyle {
        DEFAULT,
        DEFAULT_NO_FOCUS,
        JMETRO_TWEAKED;

        private String toFilePath() {
            return "/basket/api/style/%s.css".formatted(this.name().toLowerCase());
        }
    }

    private @Nullable String apiFilePath;

    private final List<String> filePaths;

    private final @Nullable JMetro jMetro;

    public StyleHandler(@Nullable Style jMetroStyle, @Nullable ApiStyle apiStyle, @Nullable List<String> filePaths) {
        if (jMetroStyle != null) {
            this.jMetro = new JMetro(jMetroStyle);
        } else {
            this.jMetro = null;
        }

        if (apiStyle != null) {
            this.apiFilePath = apiStyle.toFilePath();
        }
        if (filePaths != null) {
            this.filePaths = new LinkedList<>(filePaths);
        } else {
            this.filePaths = new LinkedList<>();
        }
    }

    public void setApiStyle(@Nullable ApiStyle apiStyle) {
        if (apiStyle == null) {
            this.apiFilePath = null;
        } else {
            this.apiFilePath = apiStyle.toFilePath();
        }
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    private List<String> getStyleSheetPaths() {
        List<String> paths = new LinkedList<>();

        if (apiFilePath != null) {
            URL url = this.getClass().getResource(Util.pathToJavaString(apiFilePath));
            if (url != null) {
                paths.add(url.toExternalForm());
            } else {
                System.err.println("Unable to get api stylesheet at: " + apiFilePath);
            }
        }

        for (String filePath : filePaths) {
            URL url = getImplementingClass().getResource(Util.pathToJavaString(filePath));
            if (url != null) {
                paths.add(url.toExternalForm());
            } else {
                System.err.println("Unable to get internal stylesheet at: " + filePath);
            }
        }

        return paths;
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

        root.getStylesheets().addAll(getStyleSheetPaths());

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

    public void reApplyStyleSheets() {
        for (Window window : Window.getWindows()) {
            Stage stage;
            try {
                stage = (Stage) window;
            } catch (ClassCastException e) {
                continue;
            }
            Parent root = stage.getScene().getRoot();

            root.getStylesheets().removeAll();
            root.getStylesheets().addAll(getStyleSheetPaths());
        }
    }

    public static void setIcon(Stage stage) {
        Path path = PathHandler.getIconPath();

        try (InputStream in = getImplementingClass().getResourceAsStream(Util.pathToJavaString(path))) {
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
