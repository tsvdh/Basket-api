package common.pre_built.popups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmationController {

    private Stage stage;
    private Boolean confirmation;

    public void init(Stage stage, String question) {
        this.stage = stage;
        this.question.setText(question);
        this.confirmation = false;
    }

    public Boolean getConfirmation() {
        return confirmation;
    }

    @FXML
    public Label question;

    @FXML
    public void confirm(ActionEvent event) {
        confirmation = true;
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event) {
        confirmation = false;
        stage.close();
    }
}