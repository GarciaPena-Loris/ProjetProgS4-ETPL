import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

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
    private Label questionText1;
    @FXML
    private MenuButton buttonAttribut1;

    private static Game partieEnCour;
    private static List<String> listeAttributs;
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

            creerDernierMenuBouton(buttonAttribut1);
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

    private void deplacerValueButton(MenuButton boutonSuivant, int ligne) {
        MenuButton menuButtonValeur = new MenuButton(boutonSuivant.getText());
        menuButtonValeur.setId("buttonValeur" + ligne);
        AnchorPane.setTopAnchor(menuButtonValeur,
        ((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut" + ligne)).getLayoutY());
        AnchorPane.setLeftAnchor(menuButtonValeur, boutonSuivant.getLayoutX());
        menuButtonValeur.setDisable(true);
        AnchorPaneId.getChildren().add(menuButtonValeur);

        if (boutonSuivant.getText().equals("___") || listeAttributsChoisi.size() == 2) {
            List<String> listeValeurs = partieEnCour.getListeValeurs(((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut" + ligne)).getText());
            for (int i = 0; i < listeValeurs.size(); i++) {
                MenuItem valeurs = new MenuItem(listeValeurs.get(i));
                valeurs.setId(menuButtonValeur.getId() + i);
                valeurs.setOnAction(valeurSelectedEvent);
                menuButtonValeur.getItems().add(valeurs);
            }
        }
    }

    private void creerDernierMenuBouton(MenuButton menuButtonAttribut) {
        int compteur = 1;
        menuButtonAttribut.getItems().clear();
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
            MenuButton valueButton = (MenuButton) mainScene.lookup("#buttonValeur" + currentItem.getId()
                    .substring(currentItem.getId().length() - 2, currentItem.getId().length() - 1));
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
                    menuButtonValeurs.setId("buttonValeur" + currentItem.getId()
                            .substring(currentItem.getId().length() - 2, currentItem.getId().length() - 1));

                    // placement du bouton
                    AnchorPane.setTopAnchor(menuButtonValeurs, currentMenuButton.getLayoutY());
                    AnchorPane.setLeftAnchor(menuButtonValeurs, currentMenuButton.getLayoutX() + 35
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
            Button deleteButton = new Button("Supprimer question");
            deleteButton.setId("deleteButton" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(deleteButton, currentMenuButtonAttribut.getLayoutY());
            AnchorPane.setRightAnchor(deleteButton, 20.0);
            deleteButton.setOnAction(deleteQuestionEvent);
            AnchorPaneId.getChildren().add(deleteButton);

            // ajout l'attribut a la liste
            listeAttributsChoisi.add(currentMenuButtonAttribut.getText());

            MenuButton buttonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");
            buttonAjout.setVisible(false);
            buttonAjout.setId("disabled");

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

            creerDernierMenuBouton(menuButtonAttribut);
        }
        // gerer quand nbattribut > 10
    };

    EventHandler<ActionEvent> deleteQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button currentButton = (Button) actionEvent.getSource();
            int currentFlour = Integer.parseInt(currentButton.getId().substring(12,
                    currentButton.getId().length()));
            Scene scene = borderPaneId.getScene();
            String attributActuel = ((MenuButton) scene.lookup("#buttonAttribut" + currentFlour)).getText();

            for (int i = currentFlour; i <= listeAttributsChoisi.size(); i++) {
                // le texte
                ((Label) scene.lookup("#questionText" + i)).setText(
                        ((Label) scene.lookup("#questionText" + (i + 1))).getText());

                // l'attribut
                ((MenuButton) scene.lookup("#buttonAttribut" + i)).setText(
                        ((MenuButton) scene.lookup("#buttonAttribut" + (i + 1))).getText());

                // valeur
                MenuButton currentValueButton = (MenuButton) scene.lookup("#buttonValeur" + i);
                MenuButton nextValueButton = (MenuButton) scene.lookup("#buttonValeur" + (i + 1));

                if (currentValueButton == null && nextValueButton != null) {
                    // creer le bouton avec la valeur
                    deplacerValueButton(nextValueButton, i);
                } else if (currentValueButton != null && nextValueButton != null) {
                    // changer la valeur
                    AnchorPaneId.getChildren().remove(currentValueButton);
                    deplacerValueButton(nextValueButton, i);
                } else if (currentValueButton != null && nextValueButton == null) {
                    // supprimer le bouton
                    AnchorPaneId.getChildren().remove(currentValueButton);
                }
            }

            // supprimer le dernier
            AnchorPaneId.getChildren()
                    .remove(scene.lookup("#questionText" + (listeAttributsChoisi.size() + 1)));
            AnchorPaneId.getChildren()
                    .remove(scene.lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1)));
            MenuButton lastValueButton = (MenuButton) scene
                    .lookup("#buttonValeur" + (listeAttributsChoisi.size() + 1));
            if (lastValueButton != null)
                AnchorPaneId.getChildren().remove(lastValueButton);
            AnchorPaneId.getChildren().remove(scene.lookup("#deleteButton" + (listeAttributsChoisi.size())));

            // supprime l'element de la liste
            listeAttributsChoisi.remove(attributActuel);
            creerDernierMenuBouton(
                    (MenuButton) scene.lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1)));

            // supprime le bouton d'ajout s'il y est
            if (scene.lookup("#buttonAjoutQuestion") != null) {
                AnchorPaneId.getChildren().remove(scene.lookup("#buttonAjoutQuestion"));
            }

            // reactive le dernier et ajouter le bouton ajoutquestion si besoin
            MenuButton lastUsableAttributButton = (MenuButton) scene
                    .lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1));
            MenuButton lastUsableValueButton = (MenuButton) scene
                    .lookup("#buttonValeur" + (listeAttributsChoisi.size() + 1));

            lastUsableAttributButton.setDisable(false);
            if (lastUsableValueButton != null)
                lastUsableValueButton.setDisable(false);
            if (!lastUsableAttributButton.getText().equals("___")
                    && (lastUsableValueButton == null || !lastUsableValueButton.getText().equals("___")))
                creerBoutonAjoutQuestion();

            if (currentFlour == 1) {
                String premierTexte = ((Label) scene.lookup("#questionText1")).getText();
                premierTexte = "L" + premierTexte.substring(4, premierTexte.length());
                ((Label) scene.lookup("#questionText1")).setText(premierTexte);
            }
        }

    };
}
