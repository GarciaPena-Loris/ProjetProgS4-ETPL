import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javafx.event.EventHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainSceneController {

    // @FXML
    // void btnOkClicked(ActionEvent event) {
    // Stage mainWindow = (Stage) tfTitle.getScene().getWindow();
    // String title = tfTitle.getText();
    // mainWindow.setTitle(title);
    // }

    @FXML
    private BorderPane borderPaneId;
    @FXML
    private AnchorPane AnchorPaneId;
    @FXML
    private MenuButton buttonAttribut1;
    @FXML
    private MenuItem etAjoutQuestion;
    @FXML
    private MenuItem ouAjoutQuestion;

    private static Game partieEnCour;
    private static List<String> listeAttributs;
    private static ArrayList<String> listeConjonction = new ArrayList<>();
    private static ArrayList<String> listeAttributsChoisi = new ArrayList<>();

    // initialize et met la grille dans la partie central
    // créée le button avec les choix d'attributs
    @FXML
    protected void initialize() {
        // Recuperer les données du JSON ici
        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader("Test.json"));

            String cheminVersImages = (String) js.get("images");
            int lignes = Integer.parseInt((String) js.get("ligne"));
            int colonnes = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");

            partieEnCour = new Game(Difficulte.normal, personnages, lignes,
                    colonnes);
            partieEnCour.afficheEtatPartie();
            partieEnCour.verifierElimination(cheminVersImages, lignes, colonnes);

            listeAttributs = partieEnCour.getListeAttributs();

            HashMap<String, String> questions = new HashMap<>();

            // questions.put("cheveux", "blond");
            // questions.put("lunettes", "oui");
            // questions.put("chauve", "non");
            String[] listConnec = { "et", "et", "ou" };
            boolean reponseQuestion = partieEnCour.verifierReponse(questions,
                    listConnec);
            System.out.println(reponseQuestion);

            partieEnCour.NbrePersonnagesACocher(questions);

            // javafx
            GridPane grilleperso = new GridPane();
            grilleperso.setHgap(5);
            grilleperso.setVgap(5);
            int compteur = 1;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++) {
                    // Label label=new Label("YO");
                    String url = "C:/Users/loloy/Desktop/cours/L2_Info/Sem2/ProjetProgS4-ETPL/images/personnages/imageonline-co-split-image-"
                            + compteur + ".png";
                    String urlImage = url;
                    Image imagePerso = new Image(urlImage);
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    imageViewPerso.setFitHeight(70);
                    imageViewPerso.setFitWidth(50);
                    // imageViewPerso.setId()
                    grilleperso.add(imageViewPerso, i, j);
                    compteur++;
                }
                grilleperso.setMaxSize(300, 500);
            }
            borderPaneId.setCenter(grilleperso);

            for (int i = 0; i < listeAttributs.size(); i++) {
                MenuItem attribut = new MenuItem(listeAttributs.get(i));
                buttonAttribut1.getItems().add(attribut);
                attribut.setId(buttonAttribut1.getId() + i);
                attribut.setOnAction(attributSelectedEvent);
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void creerBoutonAjoutQuestion() {
        if (listeAttributsChoisi.size() + 1 < listeAttributs.size()) {
            // creer le bouton pour ajouter une question
            MenuButton buttonAjoutQuestion = new MenuButton("Ajouter question");
            buttonAjoutQuestion.setId("buttonAjoutQuestion");

            // ajout les items
            MenuItem etItem = new MenuItem("et");
            MenuItem ouItem = new MenuItem("ou");
            etItem.setId("" + (listeAttributsChoisi.size() + 1));
            ouItem.setId("" + (listeAttributsChoisi.size() + 1));
            etItem.setOnAction(AjoutQuestionEvent);
            ouItem.setOnAction(AjoutQuestionEvent);
            buttonAjoutQuestion.getItems().add(etItem);
            buttonAjoutQuestion.getItems().add(ouItem);
            // placement du bouton
            AnchorPane.setTopAnchor(buttonAjoutQuestion, (listeAttributsChoisi.size() + 1) * 40.); // a changer
            AnchorPane.setLeftAnchor(buttonAjoutQuestion, 5.);

            AnchorPaneId.getChildren().add(buttonAjoutQuestion);
        }
    }

    // Met en texte le choix selectionné et fait apparaitre les valeusr de
    // l'attribut selectionner si valeurs y a
    EventHandler<ActionEvent> attributSelectedEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            // changement texte attribut
            MenuItem currentItem = (MenuItem) actionEvent.getSource();
            String selectedAttribut = currentItem.getText();

            Scene mainScene = borderPaneId.getScene();
            MenuButton currentMenuButton = ((MenuButton) mainScene
                    .lookup("#" + currentItem.getId().substring(0, currentItem.getId().length() - 1)));
            currentMenuButton.setText(selectedAttribut);

            // supprime le bouton ajout question s'il y est
            MenuButton buttonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");
            if (buttonAjout != null)
                AnchorPaneId.getChildren().remove(buttonAjout);

            // affichage deuxieme bouton
            MenuButton valueButton = (MenuButton) mainScene
                    .lookup("#buttonValeur" + currentItem.getId().substring(currentItem.getId().length() - 2,
                            currentItem.getId().length() - 1));
            boolean estBinaire = partieEnCour.estQuestionBinaire(selectedAttribut);

            if (!estBinaire) {
                if (valueButton != null)
                    AnchorPaneId.getChildren().remove(valueButton);

                // creation du bouton des valeurs
                List<String> listeValeurs = partieEnCour.getListeValeurs(selectedAttribut);

                if (listeValeurs.isEmpty()) {
                    AnchorPaneId.getChildren().remove(valueButton);
                } else {
                    MenuButton menuButtonValeurs = new MenuButton("___");
                    menuButtonValeurs
                            .setId("buttonValeur" + currentItem.getId().substring(currentItem.getId().length() - 2,
                                    currentItem.getId().length() - 1));

                    // placement du bouton
                    AnchorPane.setTopAnchor(menuButtonValeurs, currentMenuButton.getLayoutY());
                    AnchorPane.setLeftAnchor(menuButtonValeurs,
                            currentMenuButton.getLayoutX() + 35
                                    + (new Text(selectedAttribut)).getBoundsInLocal().getWidth());
                    AnchorPaneId.getChildren().add(menuButtonValeurs);

                    // attribution des valeurs en fonction de l'attribut selectionné
                    for (int i = 0; i < listeValeurs.size(); i++) {
                        MenuItem valeurs = new MenuItem(listeValeurs.get(i));
                        valeurs.setId(menuButtonValeurs.getId() + i);
                        valeurs.setOnAction(valeurSelectedEvent);
                        menuButtonValeurs.getItems().add(valeurs);
                    }
                }
            } else {
                if (valueButton != null)
                    AnchorPaneId.getChildren().remove(valueButton);
                creerBoutonAjoutQuestion();
            }
        }
    };

    // affichage valeur bouton et affichage bouton ajout question
    EventHandler<ActionEvent> valeurSelectedEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            MenuItem currentItem = (MenuItem) actionEvent.getSource();
            String selectedValue = currentItem.getText();
            ((MenuButton) borderPaneId.getScene()
                    .lookup("#" + currentItem.getId().substring(0, currentItem.getId().length() - 1)))
                    .setText(selectedValue);

            creerBoutonAjoutQuestion();
        }
    };

    @FXML
    EventHandler<ActionEvent> AjoutQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            MenuItem currentItem = (MenuItem) actionEvent.getSource();
            MenuButton currentMenuButtonAttribut = (MenuButton) borderPaneId.getScene()
                    .lookup("#buttonAttribut" + currentItem.getId());
            MenuButton currentMenuButtonValue = (MenuButton) borderPaneId.getScene()
                    .lookup("#buttonValeur" + currentItem.getId());
            // desactiver le bouton
            currentMenuButtonAttribut.setDisable(true);
            if (currentMenuButtonValue != null) {
                currentMenuButtonValue.setDisable(true);
            }

            // ajoute bouton supprimer question (supprime attribut de la liste)
            Button buttonDelete = new Button("Supprimer question");
            buttonDelete.setId("buttonDelete" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(buttonDelete, currentMenuButtonAttribut.getLayoutY());
            AnchorPane.setRightAnchor(buttonDelete, 20.0);
            buttonDelete.setOnAction(deleteQuestionEvent);
            AnchorPaneId.getChildren().add(buttonDelete);

            // ajout l'attribut a la liste
            listeAttributsChoisi.add(currentMenuButtonAttribut.getText());

            MenuButton buttonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");

            Label texteAjoutQuestion = new Label(currentItem.getText() + " le personnage est-il/a-t-il :");
            texteAjoutQuestion.setId("questionText" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(texteAjoutQuestion, (listeAttributsChoisi.size()) * 40.);
            AnchorPane.setLeftAnchor(texteAjoutQuestion, 5.0);
            AnchorPaneId.getChildren().add(texteAjoutQuestion);

            MenuButton menuButtonAttribut = new MenuButton("___");
            menuButtonAttribut.setId("buttonAttribut" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(menuButtonAttribut, (listeAttributsChoisi.size()) * 40. - 5);
            AnchorPane.setLeftAnchor(menuButtonAttribut, 168.0);
            AnchorPaneId.getChildren().add(menuButtonAttribut);

            int compteur = 1;
            for (String attribut : listeAttributs) {
                if (!listeAttributsChoisi.contains(attribut)) {
                    MenuItem newAttribut = new MenuItem(attribut);
                    menuButtonAttribut.getItems().add(newAttribut);
                    newAttribut.setId(menuButtonAttribut.getId() + compteur);
                    newAttribut.setOnAction(attributSelectedEvent);
                }
                compteur++;
            }
            if (listeAttributsChoisi.size() == listeAttributs.size() - 1) {
                MenuItem attribut = new MenuItem("___");
                menuButtonAttribut.getItems().add(attribut);
                attribut.setId(menuButtonAttribut.getId() + compteur);
                attribut.setOnAction(attributSelectedEvent);
            }
            buttonAjout.setVisible(false);
            buttonAjout.setId("disabled");
        }
        // gerer quand nbattribut > 10
    };

    EventHandler<ActionEvent> deleteQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button currentButton = (Button) actionEvent.getSource();
            int currentFlour = Integer.parseInt(currentButton.getId().substring(12,
                    currentButton.getId().length()));
            System.out.println(currentFlour);
            if (currentFlour == 1) {
                System.out.println("first");
            } else if (currentFlour == listeAttributs.size()) {
                System.out.println("second");
            } else if (currentFlour == listeAttributsChoisi.size()) {
                System.out.println("third");
            } else {
                System.out.println("else");
                System.out.println("#questionText" + (currentFlour + 1));
                ((Label) borderPaneId.getScene().lookup("#questionText" + currentFlour)).setText(
                        ((Label) borderPaneId.getScene().lookup("#questionText" + (currentFlour + 1))).getText());
            }
        }
    };
}
