import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class UtilMultiController extends UtilController {
    private GameSocket gameSocket;

    public void setGameSocket(GameSocket gameSocket) {
        this.gameSocket = gameSocket;
    }

    @Override
    protected void creerBoutonValider() {
        Button ancienButtonValider = (Button) borderPaneId.getScene().lookup("#buttonValiderQuestion");
        if (ancienButtonValider != null) {
            anchorPaneId.getChildren().remove(ancienButtonValider);
        }
        if (buttonAttribut1.getText() != "___") {
            Button buttonValiderQuestion = new Button("Envoyer question");
            buttonValiderQuestion.setId("buttonValiderQuestion");
            buttonValiderQuestion.setOnAction(envoyerQuestionEvent);

            AnchorPane.setBottomAnchor(buttonValiderQuestion, 15.);
            AnchorPane.setRightAnchor(buttonValiderQuestion, 20.);
            anchorPaneId.getChildren().add(buttonValiderQuestion);
        }
    }

    public EventHandler<ActionEvent> envoyerQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent mouseEvent) {
            try {
                ArrayList<String> listeQuestion = creerListeQuestion();
                for (String question : listeQuestion) {
                    gameSocket.envoyerMessage(question);
                }
                gameSocket.envoyerMessage("fin");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
