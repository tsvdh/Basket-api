package api.common.pre_built.popups;

import api.common.PathHandler;
import api.common.pre_built.StyleHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static api.common.pre_built.ButtonFactory.makeSmallButton;

public class Message {

    public Message(String text, boolean warning) {
        Button button = makeSmallButton("OK");

        Label label = new Label();
        label.setFont(new Font(20));
        label.setText(text);
        if (warning) {
            label.setStyle("-fx-text-fill: red");
        }

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label, button);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(10));
        vBox.setMinWidth(300);

        Scene scene = new Scene(vBox);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Important message");
        stage.initModality(Modality.APPLICATION_MODAL);
        try {
            stage.getIcons().add(new Image(PathHandler.getIconPath()));
        } catch (Exception ignored) {}

        button.setOnAction(event -> stage.close());

        StyleHandler.applyStyle(scene);

        stage.showAndWait();
    }
}
