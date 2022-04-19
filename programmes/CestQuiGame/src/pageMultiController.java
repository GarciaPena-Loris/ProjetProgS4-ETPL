import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private String cheminVersImages;

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

    public void emptyDirectory(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                emptyDirectory(file);
            }
            file.delete();
        }
    }

    private void fermerFenettreEvent() {
        ((Stage) ipText1.getScene().getWindow())
                .setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        try {
                            emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
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

    private void relancerServeur() {
        System.out.println("Probleme reception message");
        System.out.println("Client disconected");
        ipClientText.setText("");
        ipText1.setText("En attente d'adversaire...");
        startButton.setDisable(true);
    }

    @FXML
    void choixJsonMulti(ActionEvent event) throws FileNotFoundException, IOException, ParseException {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            jsonPath = selectedFile.getAbsolutePath();
            String jsonNom = selectedFile.getName();
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader(jsonPath));
            cheminVersImages = (String) js.get("images");
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
                            String ipClient = ((GameServer) gameSocket).connexionClient();
                            ipClientText.setText("-" + ipClient);
                            ipClientText.setVisible(true);
                            ipText1.setText("Client connecté :");
                            // startButton.setDisable(false);
                            gameSocket.envoyerMessage("connected");
                            if (gameSocket.ecouterMessage().equals("downloadable")) {
                                // envoyer le json au client
                                ((GameServer) gameSocket).envoyerFichier(new File(jsonPath));

                                if (gameSocket.ecouterMessage().equals("done")) {
                                    // envois toutes les images
                                    File dossierImage = new File(cheminVersImages);
                                    gameSocket
                                            .envoyerMessage(
                                                    "" + dossierImage.listFiles(UtilController.imageFiltre).length);
                                    if (gameSocket.ecouterMessage().equals("done")) {
                                        for (File image : dossierImage.listFiles(UtilController.imageFiltre)) {
                                            gameSocket.envoyerMessage(image.getName());
                                            if (gameSocket.ecouterMessage().equals("done")) {
                                                ((GameServer) gameSocket).envoyerFichier(image);
                                                if (gameSocket.ecouterMessage().equals("done")) {
                                                } else {
                                                    relancerServeur();
                                                }
                                            } else {
                                                relancerServeur();
                                            }
                                        }
                                        if (gameSocket.ecouterMessage().equals("end")) {
                                            startButton.setDisable(false);
                                            if (!gameSocket.ecouterMessage().equals("start")) {
                                                relancerServeur();
                                            }
                                        } else {
                                            relancerServeur();
                                        }
                                    } else {
                                        relancerServeur();
                                    }
                                } else {
                                    relancerServeur();
                                }
                            } else {
                                relancerServeur();
                            }
                            break;
                        } catch (Exception e) {
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
                    String etat = ((GameClient) gameSocket).connectionServeur(IPTextField.getText());
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
                            ipText1.setText("Connecté au serveur !  En attente du telechargement du JSON...");
                            if (retryButton != null)
                                retryButton.setVisible(false);

                            gameSocket.envoyerMessage("downloadable");
                            System.out.println("En attente du telechargement des fichier...");
                            // attente debut de la partie
                            ((GameClient) gameSocket).enregistrerJson();
                            gameSocket.envoyerMessage("done");

                            ipText1.setText("Connecté au serveur !  En attente du telechargement des Images...");
                            int nombreImage = Integer.parseInt(gameSocket.ecouterMessage());
                            gameSocket.envoyerMessage("done");

                            // telecharge toutes les images
                            for (int i = 0; i < nombreImage; i++) {
                                String nomImage = gameSocket.ecouterMessage();
                                gameSocket.envoyerMessage("done");

                                ((GameClient) gameSocket).enregistrerImage(nomImage);
                                gameSocket.envoyerMessage("done");

                            }
                            gameSocket.envoyerMessage("end");

                            System.out.println("En attente du lancement de la partie");
                            String attenteDebutPartie = gameSocket.ecouterMessage();
                            if (attenteDebutPartie.equals("start")) {
                                File json = new File("CestQuiGame/bin/gameTamp/game.json");
                                jsonPath = json.getAbsolutePath();
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
                    } catch (IOException | ParseException e) {
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

                gameSocket.envoyerMessage("start");

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
                        emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
                        System.exit(0);

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
                    emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
                    System.exit(0);
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