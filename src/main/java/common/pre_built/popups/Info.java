package common.pre_built.popups;

import common.PathHandler;
import common.pre_built.StyleHandler;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import static common.pre_built.StyleHandler.setIcon;

public class Info {

    public Info(List<String> infoList, @Nullable StyleHandler styleHandler) {
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

        setIcon(stage);

        if (styleHandler != null) {
            styleHandler.applyStyle(stage.getScene());
        }

        stage.show();
    }
}
