import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MainSceneControllerMulti extends UtilMultiController {
    private boolean estServeur;
    private static GameSocket gameSocket;
    private String jsonPath;
    private String ipClient;

    private String personnageSelectionne;
    private String personnageAdversaire;
    private boolean estPersonnageSelectionne;
    private boolean estPersonnageRecu;

    private Thread attendPersonnageAdversaire;
    private Thread choixPersonnage;

    @FXML
    private AnchorPane anchorPaneId;

    @FXML
    private BorderPane borderPaneId, persoSelectionnePane, imagesBorderPaneId;

    @FXML
    private MenuButton buttonAttribut1;

    @FXML
    private Label consigneText, ipClientText, ipText1, questionText1;

    @FXML
    private Text persoText;

    @FXML
    private Button validerPersonnageButton;

    @FXML
    private Label questionEstLabel;

    @FXML
    private Label reponseLabel;

    @FXML
    private Label questionAdveraireLabel;

    @FXML
    private AnchorPane questionAnchorePaneId;

    // demarer la partie

    // choisi personnage
    // envois le personnages choisi
    // attend de recuperer le personnages de l'adversaire
    /*
     * en boucle cote serveur :
     * -pose une question
     * -attend la reponse adversaire
     * -elimine les personnages
     * -verifie les eliminations
     * -si c'est bon :
     * -confirme elimiation a l'adversaire
     * -attend question adversaire
     * -donne sa réponse
     * -attends confirmation adversaire
     * -sinon :
     * -envois gagner ou perdu à l'adversaire
     * -fin de partie
     */

    public MainSceneControllerMulti(Boolean estServeur, GameSocket gameSocket, String jsonPath, String ipClient) {
        this.estServeur = estServeur;
        this.gameSocket = gameSocket;
        this.jsonPath = jsonPath;
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
        anchorPaneId.setVisible(false);

        choixPersonnage = new Thread(() -> {
            try {
                String personnage = gameSocket.ecouterMessage();
                personnageAdversaire = personnage;
                estPersonnageRecu = true;
                System.out.println("Personnage recu : " + personnageAdversaire);

                while (true) {
                    if (estPersonnageSelectionne) {
                        demarerPartie();
                        System.out.println("break");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        choixPersonnage.start();
    }

    @FXML
    void envoyerReponseQuestion(ActionEvent event) {
        System.out.println(((Button) event.getSource()).getText());
    }

    protected void demarerPartie() {
        choixPersonnage.interrupt();
        attendPersonnageAdversaire.interrupt();

        Platform.runLater(() -> {
            anchorPaneId.setVisible(true);
            if (estServeur)
                consigneText.setText("Poser la question à l'adversaire");
            else {
                consigneText.setText("Ton adversaire choisi une question...");
                buttonAttribut1.setDisable(true);
                anchorPaneId.setOpacity(0.5);

                Thread attenteQuestion = new Thread(() -> {
                    String question = "";
                    ArrayList<String> listeQuestion = new ArrayList<>();
                    do {
                        try {
                            question = gameSocket.ecouterMessage();
                            listeQuestion.add(question);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } while (!question.equals("fin"));

                    String reponse;
                    if (listeQuestion.size() > 2) {
                        reponse = "Les critères de la question sont : ";
                    } else {
                        reponse = "Le critère de la question est : ";
                    }
                    for (int i = 0; i < listeQuestion.size() - 1; i++) {
                        for (String qt : listeQuestion) {
                            reponse += qt + "\n";
                        }
                    }
                    questionAdveraireLabel.setText(reponse);
                    questionAnchorePaneId.setVisible(true);
                    consigneText.setText("Veuillez repondre à la question de l'adversaire :");
                });
                attenteQuestion.start();

            }
        });
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

    // #region envoie de la question

    // #endregion

    // #endregion

    @FXML
    void validerPersonngeEvent(ActionEvent event) throws IOException {
        System.out.println("Envois du personnage a l'adversaire");
        estPersonnageSelectionne = true;

        consigneText.setText("En attente de l'adversaire...");

        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);

        validerPersonnageButton.setVisible(false);

        // on envoie le personnage selectionnée
        gameSocket.envoyerMessage(personnageSelectionne);

        attendPersonnageAdversaire = new Thread(() -> {
            while (true) {
                if (estPersonnageRecu) {
                    demarerPartie();
                    System.out.println("break");
                    break;
                }
            }
        });
        attendPersonnageAdversaire.start();
    }
}
