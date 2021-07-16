package common.pre_built.popups;

import common.PathHandler;
import common.pre_built.StyleHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;


public class Confirmation {

    private final static String QUESTION = "Do you want to perform the action?";

    public static boolean getConfirmation(@Nullable StyleHandler styleHandler) {
        return getConfirmation(QUESTION, styleHandler);
    }

    public static boolean getConfirmation(String question, @Nullable StyleHandler styleHandler) {
        return getConfirmation(question, true, styleHandler);
    }

    public static boolean getConfirmation(boolean show, @Nullable StyleHandler styleHandler) {
        return getConfirmation(QUESTION, show, styleHandler);
    }

    public static boolean getConfirmation(String question, boolean show, @Nullable StyleHandler styleHandler) {
        if (!show) {
            return true;
        }

        URL url = Confirmation.class.getResource("/api_fxml/confirmation.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        Stage stage;
        try {
            stage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConfirmationController controller = loader.getController();
        controller.init(stage, question);

        stage.initModality(Modality.APPLICATION_MODAL);

        try {
            stage.getIcons().add(new Image(PathHandler.getIconPath()));
        } catch (Exception ignored) {}

        if (styleHandler != null) {
            styleHandler.applyStyle(stage.getScene());
        }

        stage.showAndWait();

        return controller.getConfirmation();
    }
}
