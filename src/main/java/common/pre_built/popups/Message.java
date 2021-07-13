package common.pre_built.popups;

import common.PathHandler;
import common.pre_built.StyleHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

public class Message {

    public Message(String text, boolean warning, @Nullable StyleHandler styleHandler) {
        URL url = getClass().getResource("/fxml/message.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = new Stage();

        MessageController controller = loader.getController();
        controller.init(stage, text, warning);

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Message");
        stage.initModality(Modality.APPLICATION_MODAL);

        try {
            stage.getIcons().add(new Image(PathHandler.getIconPath()));
        } catch (Exception ignored) {}

        if (styleHandler != null) {
            styleHandler.applyStyle(scene);
        }

        stage.showAndWait();
    }
}
