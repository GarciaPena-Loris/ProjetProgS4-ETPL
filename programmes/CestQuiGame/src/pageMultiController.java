import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class pageMultiController {

    @FXML
    private TextField IPTextField;

    @FXML
    private AnchorPane anchorPaneId;

    @FXML
    private AnchorPane earlyPane;

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

    @FXML
    private Text ipText1, ipText2;

    private String jsonPath;

    private GameServer gameServer;
    public static GameClient gameClient;

    @FXML
    protected void initialize() {
        pseudoTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
                    String newPropertyValue) {
                if (jsonPath != null && !pseudoTextField.getText().isEmpty()) {
                    startButton.setDisable(false);
                } else {
                    startButton.setDisable(true);
                }
            }
        });

    }

    @FXML
    void choixJsonMulti(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            jsonPath = selectedFile.getAbsolutePath();
            String jsonNom = selectedFile.getName();
            choixJsonButton.setText(jsonNom);
            if (!pseudoTextField.getText().isEmpty()) {
                startButton.setDisable(false);
            } else {
                startButton.setDisable(true);
            }
        }
    }

    @FXML
    void hostPartie(ActionEvent event) {
        invitePinButton.setSelected(false);
        choixJsonButton.setDisable(false);
        IPTextField.setDisable(true);
        startButton.setText("Recherche d'adversaire");
        startButton.setOnAction(rechercheAdversaireEvent);
    }

    @FXML
    void rejoindrePartie(ActionEvent event) {
        hostPinButton.setSelected(false);
        choixJsonButton.setDisable(true);
        IPTextField.setDisable(false);
        startButton.setText("Rechercher une partie");
        startButton.setOnAction(recherchePartieEvent);
    }

    EventHandler<ActionEvent> rechercheAdversaireEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            ipText1.setVisible(true);
            ipText2.setVisible(true);
            earlyPane.setVisible(false);

            try {
                // creation du serveur
                Thread threadServeur = new Thread(() -> {
                    gameServer = new GameServer();
                    try {
                        gameServer.ecouterMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                threadServeur.start();

                InetAddress inetadr = InetAddress.getLocalHost();
                ipText2.setText(ipText2.getText() + " " + (String) inetadr.getHostAddress());
                startButton.setDisable(true);
                startButton.setText("Lancer partie");
                startButton.setOnAction(startGameHost);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    };

    EventHandler<ActionEvent> recherchePartieEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            ipText1.setVisible(true);
            ipText1.setText("En attente de connexion au serveur...");
            earlyPane.setVisible(false);

            gameClient = new GameClient(IPTextField.getText());

            Thread ecouteClient = new Thread(() -> {
                try {
                    gameClient.ecouterMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            ecouteClient.start();

            // creer le bouton de fermeture de connexion
            Button boutonExit = new Button("Fermer la connexion");
            boutonExit.setOnAction(fermerClient);
            AnchorPane.setLeftAnchor(boutonExit, 176.);
            AnchorPane.setBottomAnchor(boutonExit, 50.);
            anchorPaneId.getChildren().add(boutonExit);

            startButton.setDisable(true);
            startButton.setOnAction(startGameHost);
        }
    };

    EventHandler<ActionEvent> fermerClient = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                gameClient.envoyerMessage("close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    EventHandler<ActionEvent> startGameHost = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
        }
    };

}