package common.pre_built.popups;

import common.PathHandler;
import common.pre_built.StyleHandler;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

import static common.pre_built.ButtonFactory.makeSmallButton;


public class Confirmation {

    private final static String QUESTION = "Do you want to perform the action?";

    public static boolean getConfirmation(StyleHandler styleHandler) {
        return getConfirmation(QUESTION, styleHandler);
    }

    public static boolean getConfirmation(String question, StyleHandler styleHandler) {
        return getConfirmation(question, true, styleHandler);
    }

    public static boolean getConfirmation(boolean show, StyleHandler styleHandler) {
        return getConfirmation(QUESTION, show, styleHandler);
    }

    public static boolean getConfirmation(String question, boolean show, StyleHandler styleHandler) {
        if (!show) {
            return true;
        }

        Button yesButton = makeSmallButton("Yes");

        Button noButton = makeSmallButton("No");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(yesButton,
                                noButton);
        hBox.setSpacing(50);
        hBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setText(question);
        label.setFont(new Font(20));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label,
                                hBox);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Confirmation");
        try {
            stage.getIcons().add(new Image(PathHandler.getIconPath()));
        } catch (Exception ignored) {}

        AtomicBoolean confirmation = new AtomicBoolean();

        yesButton.setOnAction(event -> {
            stage.close();
            confirmation.set(true);
        });

        noButton.setOnAction(event -> {
            stage.close();
            confirmation.set(false);
        });

        styleHandler.applyStyle(scene);

        stage.setOnCloseRequest(Event :: consume);
        stage.showAndWait();

        return confirmation.get();
    }
}
