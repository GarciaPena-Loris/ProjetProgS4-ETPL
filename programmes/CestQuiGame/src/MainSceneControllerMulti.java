import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    private BorderPane borderPaneId, persoSelectionnePane, borderScrollId;

    @FXML
    private MenuButton buttonAttribut1;

    @FXML
    private Label consigneText, ipClientText, ipText1, questionText1, questionEstLabel, reponseLabel,
            questionAdveraireLabel, reponseBinaireLabel, nombrePersonnagesRestantLabel, historiqueCriteresLabel;

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
        setDifficulte("multi");
        lireJson();

    }

    @FXML
    protected void initialize() {
        setBorderPaneId(borderPaneId);
        setAnchorPaneId(anchorPaneId);
        setButtonAttribut1(buttonAttribut1);
        setQuestionText1(questionText1);
        setScrollPaneId(borderScrollId);

        if (estServeur) {
            // System.out.println("Lancement controller multi serveur");
            ipText1.setText(ipClient);
        } else {
            // System.out.println("Lancement controller multi client");
            setCheminVersImages("CestQuiGame/bin/gameTamp");
            ipText1.setVisible(false);
            ipClientText.setText("Connect?? au serveur");
        }

        nombrePersonnagesRestantLabel.setText(
                "Il reste " + partieEnCour.getNombrePersonnages() + " personnages encore en vie chez l'adversaire.");

        // creer la grille de jeu et les remplit les boutons
        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);
        grillePerso.getChildren().forEach((image) -> {
            image.setOnMouseClicked(choixPersonnageDebutPartie);
        });
        creerDernierMenuBouton(buttonAttribut1);

        consigneText.setText("Choisissez votre personnage :");

        // desactiver les boutons de question pour le moment
        anchorPaneId.setOpacity(0);

        choixPersonnage = new Thread(() -> {
            try {
                String personnage = gameSocket.ecouterMessage();
                personnageAdversaire = personnage;
                estPersonnageRecu = true;
                // System.out.println("Personnage recu : " + personnageAdversaire);

                while (true) {
                    if (estPersonnageSelectionne) {
                        Platform.runLater(() -> {
                            demarerPartie();
                        });
                        // System.out.println("break");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                afficherFinPartie(
                        "Adversaire deconnect?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
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
                    listeQuestion.add(question);
                } catch (IOException e) {
                    e.printStackTrace();
                    afficherFinPartie(
                            "Adversaire deconnect?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
                    break;
                }
            } while (!question.equals("end"));

            for (String qt : listeQuestion) {
                if (!qt.equals("end"))
                    reponse += qt + "\n";
            }
            Platform.runLater(() -> {
                questionAdveraireLabel.setText(reponse);
                questionAnchorePaneId.setVisible(true);
                consigneText.setText("Veuillez repondre ?? la question de l'adversaire :");
                reponse = "";
            });
        });
        attenteQuestion.start();
    }

    private String getNomPersonnageAdversaire() {
        return personnageAdversaire.substring(0, personnageAdversaire.length() - 4);
    }

    public void afficherFinPartie(String texte) {
        Platform.runLater(() -> {
            borderPaneId.getChildren().clear();
            AnchorPane pageFinale = new AnchorPane();
            
            // texte
            Label texteFin = new Label(texte);
            AnchorPane.setTopAnchor(texteFin, 150.);
            AnchorPane.setLeftAnchor(texteFin, 360.);
            AnchorPane.setRightAnchor(texteFin, 350.);
            if (personnageAdversaire == null) {
                texteFin.setText("Adversaire deconnect?? :(");
                AnchorPane.setLeftAnchor(texteFin, 400.);
                AnchorPane.setTopAnchor(texteFin, 200.);
            }
            texteFin.setFont(new Font("Arial", 17));
            texteFin.setWrapText(true);
            
            // bouton de fin de jeux
            Button quitterButton = new Button("Quitter le jeu");
            quitterButton.setOnAction(quitterEvent);
            AnchorPane.setTopAnchor(quitterButton, 420.);
            AnchorPane.setLeftAnchor(quitterButton, 500.);
            AnchorPane.setRightAnchor(quitterButton, 500.);
            
            // image Perso
            File dossierImage = new File(cheminVersImages);
            String urlImage = dossierImage.getAbsolutePath() + "/" + personnageAdversaire;
            Image imagePerso = new Image("file:///" + urlImage);
            ImageView imageViewPerso = new ImageView(imagePerso);
            imageViewPerso.setFitHeight(125);
            imageViewPerso.setFitWidth(90);
            AnchorPane.setTopAnchor(imageViewPerso, 240.);
            AnchorPane.setLeftAnchor(imageViewPerso, 500.);
            AnchorPane.setRightAnchor(imageViewPerso, 500.);
            
            pageFinale.getChildren().addAll(texteFin, imageViewPerso, quitterButton);
            borderPaneId.setCenter(pageFinale);
            pageMultiController.emptyDirectory(new File("CestQuiGame/bin/gameTamp"));
        });
    };

    @FXML
    void envoyerReponseQuestion(ActionEvent event) {
        String reponse = ((Button) event.getSource()).getText();
        try {
            gameSocket.envoyerMessage(reponse);
            consigneText.setText("L'adversaire proc??de aux ??liminations...");
            questionAnchorePaneId.setVisible(false);
            questionEstLabel.setText("La question de l'adversaire est :");
            Thread ecouterLaReponse = new Thread(() -> {
                // ecouter la reponse
                try {
                    String[] statutElimination = gameSocket.ecouterMessage().split("-");
                    if (statutElimination[0].equals("pasGagne")) {
                        Platform.runLater(() -> {
                            consigneText.setText("L'adversaire a termin??, ?? ton tour de poser une question:");
                            creerDernierMenuBouton(buttonAttribut1);
                            buttonAttribut1.setDisable(false);
                            anchorPaneId.setOpacity(1);
                            anchorPaneId.setDisable(false);
                            nombrePersonnagesRestantLabel.setText("Il reste "
                                    + (partieEnCour.getNombrePersonnages() - Integer.parseInt(statutElimination[1]))
                                    + " personnages encore en vis chez l'adversaire.");
                        });
                    } else if (statutElimination[0].equals("gagne")) {
                        // l'adversaire ?? gagn??
                        afficherFinPartie(
                                "Votre adversaire a trouv?? votre personnage (il ?? ??t?? meilleur :3). Son personnage ??tait "
                                        + getNomPersonnageAdversaire() + " : ");
                        // ajouter bouton quitter
                    } else if (statutElimination[0].equals("perdu")) {
                        // l'adversaire ?? perdu
                        afficherFinPartie(
                                "Votre adversaire a perdu... il a ??limin?? votre personnage (quel boulet)... Vous avez donc gagn?? !! Son personnage ??tait "
                                        + getNomPersonnageAdversaire() + " : ");
                    } else {
                        afficherFinPartie(
                                "Adversaire deconnect?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    afficherFinPartie("Adversaire deconnect?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
                }
            });
            ecouterLaReponse.start();
        } catch (IOException e) {
            afficherFinPartie("Adversaire deconnect?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
        }
    }

    @FXML
    void validerEliminationsEvent(ActionEvent event) throws IOException {
        // System.out.println("Verifier les ??liminations");
        ArrayList<String> nomsPerso = new ArrayList<>();
        for (String perso : listeIdPersoSelectionne) {
            nomsPerso.add(perso.split("_")[0]);
        }
        // ajoute a la liste des personnages mort les personnages selectionn??es
        listeTotalPersoElimine.addAll(nomsPerso);
        listeIdPersoSelectionne.clear();

        boolean personnageAtrouverElimine = listeTotalPersoElimine.contains(personnageAdversaire);
        if (!personnageAtrouverElimine) {
            if (listeTotalPersoElimine.size() == partieEnCour.getNombrePersonnages() - 1) {
                // gagn??
                afficherFinPartie("Bravo ! Vous avez gagn?? ! Le personnage ??tait bien "
                        + getNomPersonnageAdversaire() + " :  ");
                // le dire ?? l'adversaire
                gameSocket.envoyerMessage("gagne");
            } else {
                // pas gagn??
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
                // le dire ?? l'adversaire
                gameSocket.envoyerMessage("pasGagne-" + listeTotalPersoElimine.size());
            }
        } else {
            // perdu
            afficherFinPartie("Vous avez perdu car vous avez ??limin?? "
                    + getNomPersonnageAdversaire() + ", dommage... :(");

            // le dire ?? l'adversaire
            gameSocket.envoyerMessage("perdu");
        }
    }

    @FXML
    void validerPersonngeEvent(ActionEvent event) throws IOException {
        // System.out.println("Envois du personnage a l'adversaire");
        estPersonnageSelectionne = true;

        consigneText.setText("En attente de l'adversaire...");

        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);
        grillePerso.getChildren().forEach((image) -> {
            image.setOnMouseClicked(afficheCibleEventV2);
        });

        validerPersonnageButton.setVisible(false);

        // on envoie le personnage selectionn??e
        gameSocket.envoyerMessage(personnageSelectionne);

        attendPersonnageAdversaire = new Thread(() -> {
            while (true) {
                if (estPersonnageRecu) {
                    Platform.runLater(() -> {
                        demarerPartie();
                    });
                    // System.out.println("break");
                    break;
                }
            }
        });
        attendPersonnageAdversaire.start();
    }

    protected void demarerPartie() {
        anchorPaneId.setOpacity(1);
        if (estServeur)
            consigneText.setText("Poser la question ?? l'adversaire");
        else {
            consigneText.setText("Ton adversaire choisi une question...");
            buttonAttribut1.setDisable(true);
            anchorPaneId.setOpacity(0);
            attendreQuestion();

        }
    }

    // #region event handler

    // #region choix du personnage
    EventHandler<MouseEvent> choixPersonnageDebutPartie = new EventHandler<MouseEvent>() {
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
    EventHandler<MouseEvent> afficheCibleEventV2 = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            afficheCibleEvent.handle(mouseEvent);
        }
    };
    // #endregion

    // #region envoyer question event
    public EventHandler<ActionEvent> envoyerQuestionEvent = new EventHandler<ActionEvent>() {
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
                                            "Veuillez ??liminer les personnages qui ne correspondent pas ?? ce(s) crit??re(s): ");
                                    reponseBinaireLabel.setText("OUI");
                                } else {
                                    consigneText.setText(
                                            "Veuillez ??liminer les personnages qui correspondent ?? ce(s) crit??re(s): ");
                                    reponseBinaireLabel.setText("NON");
                                }
                                String historiqueQuestions = "";
                                for (String qt : listeQuestion) {
                                    historiqueQuestions += qt + "\n";
                                }
                                historiqueCriteresLabel.setText(historiqueQuestions);
                            });
                        } else {
                            // gestion erreur
                            // System.err.println("Mauvais message re??u : " + reponse);
                            afficherFinPartie(
                                    "Adversaire deconnct?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        afficherFinPartie(
                                "Adversaire deconnct?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
                    }
                });
                attenteReponse.start();
            } catch (IOException e) {
                e.printStackTrace();
                afficherFinPartie(
                        "Adversaire deconnct?? :( Son personange ??tait " + getNomPersonnageAdversaire() + " :  ");
            }
        }
    };
    // #endregion

    // #region fermer fenettre
    public EventHandler<ActionEvent> quitterEvent = new EventHandler<ActionEvent>() {
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
