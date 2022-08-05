package basket.api.prebuilt;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Confirmation {

    private final static String QUESTION = "Do you want to perform the action?";

    private final ConfirmationController controller;

    public Confirmation() {
        this(QUESTION);
    }

    public Confirmation(String question) {
        URL url = Confirmation.class.getResource("/basket/api/fxml/confirmation.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        Stage stage;
        try {
            stage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller = loader.getController();
        controller.init(stage, question);

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }

    public boolean getResult() {
        return controller.getConfirmation();
    }
}
