import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class pageMultiController {

    @FXML
    private TextField IPTextField;

    @FXML
    private AnchorPane anchorPaneId, earlyPane;

    @FXML
    private RadioButton hostPinButton, invitePinButton;

    @FXML
    private Button startButton, cancelButton, retryButton, choixJsonButton, validerPersonnageButton;

    @FXML
    private Text ipText1, ipText2, ipClientText, persoText;

    @FXML
    private ScrollPane scrollPane;

    private String jsonPath;

    private GameSocket gameSocket;
    private boolean estServeurConnecte = false;

    @FXML
    protected void initialize() {
        IPTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
                    String newPropertyValue) {
                String[] ipSlpit = IPTextField.getText().split("\\.");
                if (ipSlpit.length == 4) {
                    startButton.setDisable(false);
                } else {
                    startButton.setDisable(true);
                }
            }
        });
    }

    private void fermerFenettreEvent() {
        ((Stage) ipText1.getScene().getWindow())
                .setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        try {
                            System.out.println("Fermetures sockets");
                            gameSocket.stopSocket();
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("Fermeture socket impossible");
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
            startButton.setDisable(false);
        }
    }

    @FXML
    void hostPartie(ActionEvent event) {
        invitePinButton.setSelected(false);
        choixJsonButton.setDisable(false);
        IPTextField.setDisable(true);
        startButton.setText("Recherche d'adversaire");
        startButton.setOnAction(rechercheAdversaireEvent);
        if (jsonPath != null) {
            startButton.setDisable(false);
        } else {
            startButton.setDisable(true);
        }
        fermerFenettreEvent();
    }

    @FXML
    void rejoindrePartie(ActionEvent event) {
        hostPinButton.setSelected(false);
        choixJsonButton.setDisable(true);
        IPTextField.setDisable(false);
        startButton.setText("Rechercher une partie");
        startButton.setOnAction(recherchePartieEvent);
        if (!IPTextField.getText().equals("")) {
            startButton.setDisable(false);
        }
        fermerFenettreEvent();
    }

    @FXML
    void relancerConnexionEvent(ActionEvent actionEvent) {
        System.out.println("Relancement cherche serveur");
        recherchePartieEvent.handle(new ActionEvent());
    }

    @FXML
    void cancelConnexionEvent(ActionEvent actionEvent) {
        System.out.println("Annulation recherche serveur");
        ipText1.setVisible(false);
        ipText2.setVisible(false);
        earlyPane.setVisible(true);
        cancelButton.setVisible(false);
        retryButton.setVisible(false);
        startButton.setVisible(true);
        startButton.setDisable(true);
        String[] ipSlpit = IPTextField.getText().split("\\.");
        if (ipSlpit.length == 4) {
            startButton.setDisable(false);
        }
    }

    // #region cote serveur
    EventHandler<ActionEvent> rechercheAdversaireEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                ipText1.setVisible(true);
                ipText1.setText("En attente d'adversaire...");
                ipText2.setVisible(true);
                earlyPane.setVisible(false);

                // affiche l'ip du serveur
                InetAddress inetadr = InetAddress.getLocalHost();
                ipText2.setText(ipText2.getText() + " " + (String) inetadr.getHostAddress());
                startButton.setDisable(true);
                startButton.setText("Lancer partie");
                AnchorPane.setLeftAnchor(startButton, 250.);
                startButton.setOnAction(startGameHost);

                // creation du serveur
                gameSocket = new GameServer();

                // attente de connexion du client
                Thread threadServeur = new Thread(() -> {
                    while (true) {
                        try {
                            String ipClient = gameSocket.connexionClient();
                            ipClientText.setText("-" + ipClient);
                            ipClientText.setVisible(true);
                            ipText1.setText("Client connecté :");
                            startButton.setDisable(false);
                            gameSocket.envoyerMessage("connected");
                            if (gameSocket.ecouterMessage().equals("close")) {
                                System.out.println("Client disconected");
                                ipClientText.setText("");
                                ipText1.setText("En attente d'adversaire...");
                                startButton.setDisable(true);
                            }
                            break;
                        } catch (IOException e) {
                            if (e.getMessage().equals("Socket is closed")) {
                                System.out.println("Socket serveur fermé");
                                ipText1.setVisible(false);
                                ipText2.setVisible(false);
                                earlyPane.setVisible(true);
                                break;
                            }
                        }
                    }
                });
                threadServeur.start();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                ipText2.setText(ipText2.getText() + " erreur");
            }
        }
    };
    // #endregion

    // #region cote client
    EventHandler<ActionEvent> recherchePartieEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            ipText1.setVisible(true);
            ipText1.setText("En attente de connexion au serveur...");
            earlyPane.setVisible(false);
            startButton.setVisible(false);

            gameSocket = new GameClient();

            Thread ecouteClient = new Thread(() -> {
                // creer les bouton pour relancer la recherche et annuler
                retryButton.setVisible(true);
                retryButton.setDisable(true);
                cancelButton.setVisible(true);
                cancelButton.setDisable(true);

                try {
                    String etat = gameSocket.connectionServeur(IPTextField.getText());
                    System.out.println("Etat : " + etat);
                    if (etat.equals("error")) {
                        ipText1.setText("Serveur non connecté...");
                        // active les boutons si le serveur n'est pas trouvé
                        retryButton.setDisable(false);
                        cancelButton.setDisable(false);
                    } else if (etat.equals("ok")) {
                        estServeurConnecte = true;
                        retryButton.setVisible(false);
                        cancelButton.setVisible(false);
                    } else {
                        System.out.println("Message incorrect : " + etat);
                    }
                } catch (Exception e) {
                    System.err.println("Probleme de creation du client");
                }

                // verifie si le serveur est bien connecté
                if (estServeurConnecte) {
                    try {
                        String messageRecu = gameSocket.ecouterMessage();
                        if (messageRecu.equals("connected")) {
                            ipText1.setText("Connecté au serveur !  En attente du lancement de la partie...");
                            if (retryButton != null)
                                retryButton.setVisible(false);

                            System.out.println("en attente du debut de la partie");
                            // attente debut de la partie
                            String attenteDebutPartie = gameSocket.ecouterMessage();
                            String[] msgSplit = attenteDebutPartie.split("\\*");
                            if (msgSplit.length == 2 && msgSplit[0].equals("start")) {
                                jsonPath = msgSplit[1];
                                Platform.runLater(() -> {
                                    lancerPartieClient();
                                });
                            } else if (attenteDebutPartie.equals("close")) {
                                estServeurConnecte = false;
                                ipText1.setVisible(true);
                                ipText1.setText("Serveur deconnecté...");
                                ipText2.setVisible(false);
                                earlyPane.setVisible(true);
                                startButton.setVisible(true);
                                startButton.setDisable(false);
                                retryButton.setVisible(false);
                                cancelButton.setVisible(false);
                            } else {
                                System.err.println("Message incorrect recu : " + attenteDebutPartie);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            ecouteClient.start();
        }
    };
    // #endregion

    EventHandler<ActionEvent> startGameHost = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                System.out.println("Debut de la partie cote serveur");

                // envoyer le json au client
                gameSocket.envoyerMessage("start*" + jsonPath);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pageJeuMulti.fxml"));
                fxmlLoader.setController(
                        new MainSceneControllerMulti(true, gameSocket, jsonPath, ipClientText.getText()));

                Parent root = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("QuiEstCe? - Multi-joueur - (Serveur)");
                File logo = new File("images/logoQuiEstCe.png");
                stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        System.out.println("Fenetre fermé");
                        // a faire mais dans l'idée il faut le dire au client le pauvre
                    }
                });
                stage.show();

                ((Stage) anchorPaneId.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void lancerPartieClient() {
        try {
            System.out.println("Debut partie client");
            gameSocket.envoyerMessage("started");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pageJeuMulti.fxml"));
            fxmlLoader.setController(new MainSceneControllerMulti(false, gameSocket, jsonPath, null));

            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("QuiEstCe? - Multi-joueur - (Client)");
            File logo = new File("images/logoQuiEstCe.png");
            stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("Fenetre fermé");
                    // a faire mais dans l'idée il faut le dire au serveur le pauvre
                }
            });
            stage.show();

            ((Stage) anchorPaneId.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}