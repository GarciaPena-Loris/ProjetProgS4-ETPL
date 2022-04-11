import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class pageMultiController {

    @FXML
    private TextField IPTextField;

    @FXML
    private AnchorPane anchorPaneId;

    @FXML
    private Button choixJsonButton;

    @FXML
    private RadioButton hostPinButton;

    @FXML
    private RadioButton invitePinButton;

    @FXML
    private TextField pseudoTextField;

    @FXML
    private Button startButton;

    private boolean isHost;

    //private static Client client;

    private String jsonName;

    // public static void connectionAuServeur(){
    //     client = new Client();
    // }

    @FXML
    void choixJsonMulti(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            jsonName = selectedFile.getAbsolutePath();
            String jsonNom = selectedFile.getName();
            choixJsonButton.setText(jsonNom);
        }
    }

    @FXML
    void hostPartie(ActionEvent event) {
        invitePinButton.setSelected(false);
        choixJsonButton.setDisable(false);
        IPTextField.setDisable(true);
        isHost = true;
        startButton.setText("Lancer la partie");
        startButton.setOpacity(0.5);
    }

    @FXML
    void rejoindrePartie(ActionEvent event) {
        hostPinButton.setSelected(false);
        choixJsonButton.setDisable(true);
        IPTextField.setDisable(false);
        isHost = false;
        startButton.setText("Rejoindre la partie");
        startButton.setOpacity(0.5);
    }
    
    @FXML
    void startGame(ActionEvent event) {

    }

    // public static void main(String[] args) {
    //     connectionAuServeur();
    // }
}