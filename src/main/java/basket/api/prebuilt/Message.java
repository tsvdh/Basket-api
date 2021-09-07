package basket.api.prebuilt;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Message {

    public Message(String text, boolean warning) {
        URL url = getClass().getResource("/basket/api/fxml/message.fxml");
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

        stage.showAndWait();
    }
}
