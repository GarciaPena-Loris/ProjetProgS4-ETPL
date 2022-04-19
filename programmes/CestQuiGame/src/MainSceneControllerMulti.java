import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSceneControllerMulti extends UtilController {
    @FXML
    private AnchorPane anchorPaneId, questionAnchorePaneId, partieReponseQuestionPane;

    @FXML
    private BorderPane borderPaneId, persoSelectionnePane, imagesBorderPaneId;

    @FXML
    private MenuButton buttonAttribut1;

    @FXML
    private Label consigneText, ipClientText, ipText1, questionText1, questionEstLabel, reponseLabel,
            questionAdveraireLabel, reponseBinaireLabel;

    @FXML
    private Text persoText;

    @FXML
    private Button validerPersonnageButton, validerEliminationsButton;

    private boolean estServeur;
    private GameSocket gameSocket;
    private String ipClient;

    private String personnageSelectionne;
    private String personnageAdversaire;
    private String reponse = "";

    private boolean estPersonnageSelectionne;
    private boolean estPersonnageRecu;

    private Thread attendPersonnageAdversaire;
    private Thread choixPersonnage;

    public MainSceneControllerMulti(Boolean estServeur, GameSocket gameSocket, String jsonPath, String ipClient)
            throws IOException {
        this.estServeur = estServeur;
        this.gameSocket = gameSocket;
        this.ipClient = ipClient;

        setJson(jsonPath);
        setDifficulte("multi"); // ça sert a rien
        lireJson();

    }
    
    @FXML
    protected void initialize() {
        setBorderPaneId(imagesBorderPaneId);
        setAnchorPaneId(anchorPaneId);
        setButtonAttribut1(buttonAttribut1);
        setQuestionText1(questionText1);
        
        if (estServeur) {
            System.out.println("Lancement controller multi serveur");
            ipText1.setText(ipClient);
        } else {
            System.out.println("Lancement controller multi client");
            setCheminVersImages("CestQuiGame/bin/gameTamp");
            ipText1.setVisible(false);
            ipClientText.setText("Connecté au serveur");
        }

        // creer la grille de jeu et les remplit les boutons
        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);
        grillePerso.getChildren().forEach((image) -> {
            image.setOnMouseClicked(choixPersonnageDebutPartie);
        });
        creerDernierMenuBouton(buttonAttribut1);

        consigneText.setText("Choisisez votre personnage :");

        // desactiver les boutons de question pour le moment
        anchorPaneId.setOpacity(0);

        choixPersonnage = new Thread(() -> {
            try {
                String personnage = gameSocket.ecouterMessage();
                personnageAdversaire = personnage;
                estPersonnageRecu = true;
                System.out.println("Personnage recu : " + personnageAdversaire);

                while (true) {
                    if (estPersonnageSelectionne) {
                        Platform.runLater(() -> {
                            demarerPartie();
                        });
                        System.out.println("break");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                afficherFinPartie(
                        "Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
            }
        });
        choixPersonnage.start();
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

    private void attendreQuestion() {
        Thread attenteQuestion = new Thread(() -> {
            String question = "";
            ArrayList<String> listeQuestion = new ArrayList<>();
            do {
                try {
                    question = gameSocket.ecouterMessage();
                    if (question.equals("close")) {
                        // another thing
                        break;
                    }
                    listeQuestion.add(question);
                } catch (IOException e) {
                    e.printStackTrace();
                    afficherFinPartie(
                            "Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
                }
            } while (!question.equals("end"));

            for (String qt : listeQuestion) {
                if (!qt.equals("end"))
                    reponse += qt + "\n";
            }
            Platform.runLater(() -> {
                questionAdveraireLabel.setText(reponse);
                questionAnchorePaneId.setVisible(true);
                consigneText.setText("Veuillez repondre à la question de l'adversaire :");
                reponse = "";
            });
        });
        attenteQuestion.start();
    }

    public void afficherFinPartie(String texte) {
        AnchorPane pageFinale = new AnchorPane();
        // texte
        Label texteGagner = new Label(texte);
        texteGagner.setFont(new Font("Arial", 17));
        texteGagner.setWrapText(true);
        AnchorPane.setTopAnchor(texteGagner, 180.);
        AnchorPane.setLeftAnchor(texteGagner, 360.);
        AnchorPane.setRightAnchor(texteGagner, 350.);

        // bouton de fin de jeux
        Button quitterButton = new Button();
        quitterButton.setText("Quitter le jeu");
        quitterButton.setOnAction(quitterEvent);
        anchorPaneId.getChildren().add(quitterButton);
        AnchorPane.setTopAnchor(quitterButton, 400.);
        AnchorPane.setLeftAnchor(quitterButton, 500.);
        AnchorPane.setRightAnchor(quitterButton, 500.);

        // image Perso
        File dossierImage = new File(cheminVersImages);
        String urlImage = dossierImage.getAbsolutePath() + "/" + personnageAdversaire + ".png";
        Image imagePerso = new Image("file:///" + urlImage);
        ImageView imageViewPerso = new ImageView(imagePerso);
        imageViewPerso.setFitHeight(125);
        imageViewPerso.setFitWidth(90);
        AnchorPane.setTopAnchor(imageViewPerso, 240.);
        AnchorPane.setLeftAnchor(imageViewPerso, 500.);
        AnchorPane.setRightAnchor(imageViewPerso, 500.);

        pageFinale.getChildren().addAll(texteGagner, imageViewPerso, quitterButton);
        borderPaneId.setCenter(pageFinale);
    };

    @FXML
    void envoyerReponseQuestion(ActionEvent event) throws IOException {
        System.out.println(((Button) event.getSource()).getText());
        String reponse = ((Button) event.getSource()).getText();
        gameSocket.envoyerMessage(reponse);
        consigneText.setText("L'adversaire procède aux éliminations...");
        questionAnchorePaneId.setVisible(false);
        questionEstLabel.setText("La question de l'adversaire est :");
        Thread ecouterLaReponse = new Thread(() -> {
            // ecouter la reponse
            try {
                String statutElimination;
                statutElimination = gameSocket.ecouterMessage();
                Platform.runLater(() -> {
                    if (statutElimination.equals("pasGagne")) {
                        consigneText.setText("L'adversaire a terminé, à ton tour de poser une question:");
                        creerDernierMenuBouton(buttonAttribut1);
                        buttonAttribut1.setDisable(false);
                        anchorPaneId.setOpacity(1);
                        anchorPaneId.setDisable(false);
                        // l'inconnu
                    } else if (statutElimination.equals("gagne")) {
                        // l'adversaire à gagné
                        borderPaneId.getChildren().clear();
                        afficherFinPartie(
                                "Votre adversaire a trouvé votre personnage (il à été meilleur :3). Son personnage était "
                                        + personnageAdversaire + ":");
                        // ajouter bouton quitter
                    } else if (statutElimination.equals("perdu")) {
                        // l'adversaire à perdu
                        borderPaneId.getChildren().clear();
                        afficherFinPartie(
                                "Votre adversaire a perdu... il a éliminé votre personnage (quel boulet)... Vous avez donc gagné !! Son personnage était "
                                        + personnageAdversaire + ":");
                    } else {
                        afficherFinPartie(
                                "Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                afficherFinPartie("Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
            }
        });
        ecouterLaReponse.start();
    }

    @FXML
    void validerEliminationsEvent(ActionEvent event) throws IOException {
        System.out.println("Verifier les éliminations");
        ArrayList<String> nomsPerso = new ArrayList<>();
        for (String perso : listeIdPersoSelectionne) {
            nomsPerso.add(perso.split("_")[0]);
        }
        // ajoute a la liste des personnages mort les personnages selectionnées
        listeTotalPersoElimine.addAll(nomsPerso);
        listeIdPersoSelectionne.clear();

        boolean personnageAtrouverElimine = listeTotalPersoElimine.contains(personnageAdversaire);
        if (!personnageAtrouverElimine) {
            System.out.println("Total elimine : " + listeTotalPersoElimine.size());
            System.out.println("Total : " + partieEnCour.getNombrePersonnages());
            if (listeTotalPersoElimine.size() == partieEnCour.getNombrePersonnages() - 1) {
                // gagné
                borderPaneId.getChildren().clear();
                afficherFinPartie("Bravo ! Vous avez gagné ! Le personnage était bien "
                        + personnageAdversaire + ": ");
                // le dire à l'adversaire
                gameSocket.envoyerMessage("gagne");
            } else {
                // pas gagné
                partieReponseQuestionPane.setVisible(false);
                consigneText.setText("Ton adversaire choisi une question...");
                // recreer la grille
                GridPane grillePerso = new GridPane();
                creerGrille(grillePerso);
                grillePerso.getChildren().forEach((image) -> {
                    image.setOnMouseClicked(afficheCibleEventV2);
                });
                recreerAnchorPaneID();
                creerDernierMenuBouton(buttonAttribut1);

                attendreQuestion();
                // le dire à l'adversaire
                gameSocket.envoyerMessage("pasGagne");
            }
        } else {
            // perdu
            borderPaneId.getChildren().clear();
            afficherFinPartie("Vous avez perdu car vous avez éliminé "
                    + personnageAdversaire + ", dommage... :(");

            // le dire à l'adversaire
            gameSocket.envoyerMessage("perdu");
        }
    }

    @FXML
    void validerPersonngeEvent(ActionEvent event) throws IOException {
        System.out.println("Envois du personnage a l'adversaire");
        estPersonnageSelectionne = true;

        consigneText.setText("En attente de l'adversaire...");

        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);
        grillePerso.getChildren().forEach((image) -> {
            image.setOnMouseClicked(afficheCibleEventV2);
        });

        validerPersonnageButton.setVisible(false);

        // on envoie le personnage selectionnée
        gameSocket.envoyerMessage(personnageSelectionne);

        attendPersonnageAdversaire = new Thread(() -> {
            while (true) {
                if (estPersonnageRecu) {
                    Platform.runLater(() -> {
                        demarerPartie();
                    });
                    System.out.println("break");
                    break;
                }
            }
        });
        attendPersonnageAdversaire.start();
    }

    protected void demarerPartie() {
        choixPersonnage.interrupt();
        attendPersonnageAdversaire.interrupt();

        anchorPaneId.setOpacity(1);
        if (estServeur)
            consigneText.setText("Poser la question à l'adversaire");
        else {
            consigneText.setText("Ton adversaire choisi une question...");
            buttonAttribut1.setDisable(true);
            anchorPaneId.setOpacity(0);
            attendreQuestion();

        }
    }

    // #region event handler

    // #region choix du personnage
    EventHandler<MouseEvent> choixPersonnageDebutPartie = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            persoSelectionnePane.getChildren().clear();
            ImageView imageSelected = new ImageView(((ImageView) mouseEvent.getTarget()).getImage());
            imageSelected.setFitHeight(125);
            imageSelected.setFitWidth(90);
            personnageSelectionne = ((ImageView) mouseEvent.getTarget()).getId().split("_")[0];
            persoSelectionnePane.setCenter(imageSelected);
            persoText.setVisible(true);
            validerPersonnageButton.setVisible(true);
        }
    };
    // #endregion

    // #region afficher cible event 2
    EventHandler<MouseEvent> afficheCibleEventV2 = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            afficheCibleEvent.handle(mouseEvent);
        }
    };
    // #endregion

    // #region envoyer question event
    public EventHandler<ActionEvent> envoyerQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent mouseEvent) {
            try {
                ArrayList<String> listeQuestion = creerListeQuestion();
                for (String question : listeQuestion) {
                    gameSocket.envoyerMessage(question);
                }
                gameSocket.envoyerMessage("end");
                anchorPaneId.setOpacity(0);
                anchorPaneId.setDisable(true);
                consigneText.setText("En attente de la reponse de l'adversaire...");

                Thread attenteReponse = new Thread(() -> {
                    try {
                        String reponse = gameSocket.ecouterMessage();
                        if (reponse.equals("Oui") || reponse.equals("Non")) {
                            attendSelection = true;
                            Platform.runLater(() -> {
                                partieReponseQuestionPane.setVisible(true);
                                if (reponse.equals("Oui")) {
                                    consigneText.setText(
                                            "Veuillez éliminer les personnages qui ne correspondent pas à ce(s) critère(s): ");
                                    reponseBinaireLabel.setText("OUI");
                                } else {
                                    consigneText.setText(
                                            "Veuillez éliminer les personnages qui correspondent à ce(s) critère(s): ");
                                    reponseBinaireLabel.setText("NON");
                                }
                            });
                            // la c la folie
                        } else {
                            // gestion erreur
                            System.err.println("Mauvais message reçu : " + reponse);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        afficherFinPartie(
                                "Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
                    }
                });
                attenteReponse.start();
            } catch (IOException e) {
                e.printStackTrace();
                afficherFinPartie(
                        "Adversaire deconncté :( Son personange était " + personnageAdversaire + ": ");
            }
        }
    };
    // #endregion

    // #region fermer fenettre
    public EventHandler<ActionEvent> quitterEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            ((Stage) borderPaneId.getScene().getWindow()).close();
            pageMultiController.emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
            System.exit(0);
        }
    };
    // #endregion

    // #endregion
}
