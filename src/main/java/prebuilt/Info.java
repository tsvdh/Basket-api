package prebuilt;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Info {

    public Info(List<String> infoList) {
        URL url = getClass().getResource("/api_fxml/info.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        Stage stage;
        try {
            stage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InfoController controller = loader.getController();

        for (String infoLine : infoList) {
            if (infoLine.equals("-")) {
                controller.infoVBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
            }
            else {
                Label label = new Label(infoLine);
                label.setFont(new Font(14));
                label.setWrapText(true);

                controller.infoVBox.getChildren().add(label);
            }
        }

        stage.show();
    }
}
