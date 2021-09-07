package basket.api.prebuilt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessageController {

    private Stage stage;

    public void init(Stage stage, String text, boolean warning) {
        this.stage = stage;
        if (warning) {
            message.setStyle("-fx-text-fill: red");
        }
        message.setText(text);
    }

    @FXML
    public Label message;

    @FXML
    public void close(ActionEvent event) {
        stage.close();
    }
}
