import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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

    private Thread threadServeur;

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

    public static void emptyDirectory(File folder) {
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
                            if (gameSocket != null) {
                                gameSocket.stopSocket();
                            }
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("Fermeture socket impossible");
                        }
                    }
                });
    }

    private void relancerServeur() {
        Platform.runLater(() -> {
            System.out.println("Probleme reception message");
            System.out.println("Client disconected");
            if (gameSocket != null) {
                try {
                    gameSocket.stopSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ipClientText.setText("");
            ipText1.setText("En attente d'adversaire...");
            startButton.setText("Envoyer les données");
            startButton.setOnAction(sendData);
            startButton.setDisable(true);
            emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
            lancerServeur();
        });
    }

    private void relancerClient() {
        Platform.runLater(() -> {
            emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
            estServeurConnecte = false;
            ipText1.setVisible(true);
            ipText1.setText("Serveur deconnecté...");
            ipText2.setVisible(false);
            earlyPane.setVisible(true);
            startButton.setVisible(true);
            startButton.setDisable(false);
            retryButton.setVisible(false);
            cancelButton.setVisible(false);
        });
    }

    private void lancerServeur() {
        threadServeur = new Thread(() -> {
            while (true) {
                try {
                    String ipClient = ((GameServer) gameSocket).connexionClient();
                    ipText1.setText("Client connecté :");
                    ipClientText.setText("-" + ipClient);
                    ipClientText.setVisible(true);
                    startButton.setDisable(false);
                    gameSocket.envoyerMessage("connected");
                    break;
                } catch (Exception e) {
                    if (e.getMessage().equals("Socket is closed")) {
                        System.out.println("Socket serveur fermé");
                        relancerServeur();
                        break;
                    }
                }
            }
        });
        threadServeur.start();
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
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));
                String ip = in.readLine();
                ipText2.setText(ipText2.getText() + " " + ip);
                startButton.setDisable(true);
                AnchorPane.setLeftAnchor(startButton, 240.);
                startButton.setText("Envoyer les données");
                startButton.setOnAction(sendData);
                startButton.setDisable(true);

                // creation du serveur
                gameSocket = new GameServer();

                // attente de connexion du client
                lancerServeur();

            } catch (Exception e) {
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
                        relancerClient();
                    }
                } catch (Exception e) {
                    System.err.println("Probleme de creation du client");
                    relancerClient();
                }

                // verifie si le serveur est bien connecté
                if (estServeurConnecte) {
                    try {
                        String messageRecu = gameSocket.ecouterMessage();
                        if (messageRecu.equals("connected")) {
                            if (retryButton != null)
                                retryButton.setVisible(false);

                            ipText1.setText("Connecté au serveur ! En attente de l'envois des fichiers...");
                            if (gameSocket.ecouterMessage().equals("dataInComing")) {
                                gameSocket.envoyerMessage("downloadable");
                                System.out.println("En attente de l'envois des fichiers...");
                                ipText1.setText("Connecté au serveur !  Telechargement du JSON...");
                                // attente debut de la partie
                                ((GameClient) gameSocket).enregistrerJson();
                                gameSocket.envoyerMessage("done");

                                ipText1.setText("Connecté au serveur !  Telechargement des Images...");
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
                                ipText1.setText("Connecté au serveur !  Données reçus avec succée !");

                                String attenteDebutPartie = gameSocket.ecouterMessage();
                                if (attenteDebutPartie.equals("start")) {
                                    jsonPath = new File("CestQuiGame/bin/gameTamp/game.json").getAbsolutePath();
                                    Platform.runLater(() -> {
                                        lancerPartieClient();
                                    });
                                } else {
                                    relancerClient();
                                }
                            } else {
                                relancerClient();
                            }
                        } else {
                            relancerClient();
                        }
                    } catch (IOException | ParseException e) {
                        System.err.println("Probleme connexion serveur");
                        relancerClient();
                    }
                }
            });
            ecouteClient.start();
        }
    };
    // #endregion

    EventHandler<ActionEvent> sendData = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            Thread envoisDonnees = new Thread(() -> {
                try {
                    startButton.setDisable(true);
                    startButton.setText("Lancer la partie");
                    startButton.setOnAction(startGameHost);

                    gameSocket.envoyerMessage("dataInComing");

                    if (gameSocket.ecouterMessage().equals("downloadable")) {
                        ipText1.setText("Envois du JSON à :");

                        // envois le json au client
                        ((GameServer) gameSocket).envoyerFichier(new File(jsonPath));

                        if (gameSocket.ecouterMessage().equals("done")) {
                            ipText1.setText("Envois des images à :");

                            // envois toutes les images au client
                            File dossierImage = new File(cheminVersImages);
                            if (dossierImage.listFiles(UtilController.imageFiltre) == null) {
                                relancerServeur();
                                System.out.println("Chemin vers les images incorrect");
                                Platform.runLater(() -> {
                                    ipClientText.setText("Chemon vers les images incorrect...");
                                });
                            } else {
                                gameSocket
                                        .envoyerMessage(
                                                "" + dossierImage.listFiles(UtilController.imageFiltre).length);
                                if (gameSocket.ecouterMessage().equals("done")) {
                                    for (File image : dossierImage.listFiles(UtilController.imageFiltre)) {
                                        ipText1.setText("Envois de l'image " + image.getName() + " à :");

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
                                        ipText1.setText("Données envoyées avec succés à :");

                                        if (!gameSocket.ecouterMessage().equals("started")) {
                                            relancerServeur();
                                        }
                                    } else {
                                        relancerServeur();
                                    }
                                } else {
                                    relancerServeur();
                                }
                            }
                        } else {
                            relancerServeur();
                        }
                    } else {
                        relancerServeur();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    relancerServeur();
                }
            });
            envoisDonnees.start();
        }
    };

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