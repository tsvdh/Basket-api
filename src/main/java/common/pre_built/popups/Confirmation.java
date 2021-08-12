package common.pre_built.popups;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Confirmation {

    private final static String QUESTION = "Do you want to perform the action?";

    // This class should not be instantiated
    private Confirmation() {}

    public static boolean getConfirmation() {
        return getConfirmation(QUESTION);
    }

    public static boolean getConfirmation(String question) {
        return getConfirmation(question, true);
    }

    public static boolean getConfirmation(boolean show) {
        return getConfirmation(QUESTION, show);
    }

    public static boolean getConfirmation(String question, boolean show) {
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

        stage.showAndWait();

        return controller.getConfirmation();
    }
}
