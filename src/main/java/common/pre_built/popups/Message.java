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

import static common.pre_built.StyleHandler.setIcon;

public class Message {

    public Message(String text, boolean warning, @Nullable StyleHandler styleHandler) {
        URL url = getClass().getResource("/api_fxml/message.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        Stage stage;
        try {
            stage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageController controller = loader.getController();
        controller.init(stage, text, warning);

        stage.initModality(Modality.APPLICATION_MODAL);

        setIcon(stage);

        if (styleHandler != null) {
            styleHandler.applyStyle(stage.getScene());
        }

        stage.showAndWait();
    }
}
