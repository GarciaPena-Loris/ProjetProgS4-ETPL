import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class UtilController {
    protected Game partieEnCour;
    protected String cheminVersImages;
    protected int ligne;
    protected int colonne;

    protected static String difficulte;
    protected static String json;

    protected boolean attendSelection = false; // etait static

    protected BorderPane borderPaneId;
    protected AnchorPane anchorPaneId;
    protected Label questionText1;
    protected MenuButton buttonAttribut1;

    protected ArrayList<String> listeAttributs; // etait static
    protected ArrayList<String> listeAttributsChoisi = new ArrayList<>();
    protected ArrayList<String> listeIdPersoSelectionne = new ArrayList<>();
    protected ArrayList<String> listeTotalPersoElimine = new ArrayList<>();

    private ArrayList<String> listeAttribut = new ArrayList<>();
    private ArrayList<String> listeValeur = new ArrayList<>();
    private ArrayList<String> listeConnecteur = new ArrayList<>();
    private boolean aPlusieurQuestion = false;

    protected static FilenameFilter imageFiltre = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : new String[] { "png", "jpg" }) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    protected void lireJson() {
        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader(json));

            System.out.println(js);
            cheminVersImages = (String) js.get("images");
            System.out.println("chemin ici" + cheminVersImages);
            ligne = Integer.parseInt((String) js.get("ligne"));
            colonne = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");

            if ((String) js.get("difficulte") != null) {
                // partie chargée
                difficulte = ((String) js.get("difficulte"));
                partieEnCour = new Game(difficulte, personnages, ligne, colonne,
                        (JSONObject) js.get("personnagesChoisi"));
            } else {
                // nouvelle partie
                partieEnCour = new Game(difficulte, personnages, ligne,
                        colonne);
            }

            listeAttributs = partieEnCour.getListeAttributs();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void creerGrille(GridPane grillePerso) {
        int x = 0; // collones
        int y = 0; // ligne

        System.out.println("chemin : " +cheminVersImages);
        File dossierImage = new File(cheminVersImages);
        if (dossierImage.isDirectory()) {

            ArrayList<String> listePersonnageMort = partieEnCour.getListePersonnageMort();
            listeTotalPersoElimine = listePersonnageMort;

            grillePerso.setId("grillePerso");
            grillePerso.setMaxSize(100, 50);
            grillePerso.setHgap(colonne);
            grillePerso.setVgap(ligne);

            for (File image : dossierImage.listFiles(imageFiltre)) {
                String nomImage = image.getName();
                String urlImage = image.getAbsolutePath();
                Image imagePerso = new Image("file:///" + urlImage);
                ImageView imageViewPerso = new ImageView(imagePerso);
                imageViewPerso.setFitHeight(125);
                imageViewPerso.setFitWidth(90);
                imageViewPerso
                        .setId(nomImage.substring(0, nomImage.length() - 4) + "_" + x + "_" + y);
                imageViewPerso.setOnMouseClicked(afficheCibleEvent);
                grillePerso.add(imageViewPerso, x, y);

                if (!listePersonnageMort.isEmpty()) {
                    if (listePersonnageMort
                            .contains(image.getName().substring(0, image.getName().length() - 4))) {
                        File f2 = new File("images/mortpng.png");
                        Image imageMort = new Image("file:///" + f2.getAbsolutePath());
                        ImageView imageViewMort = new ImageView(imageMort);
                        imageViewMort.setId("mort_" + x + "_" + y);
                        imageViewMort.setFitHeight(125);
                        imageViewMort.setFitWidth(90);
                        grillePerso.add(imageViewMort, x, y);

                    }
                }
                x++;
                if (x == colonne) {
                    x = 0;
                    y++;
                }
            }
            borderPaneId.setCenter(grillePerso);
        } else {
            borderPaneId.getChildren().clear();
            Label textePerdu = new Label("Chemin vers les images incorrect.");
            borderPaneId.setCenter(textePerdu);
        }
    }

    protected void creerDernierMenuBouton(MenuButton menuButtonAttribut) {
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

    protected void creerBoutonAjoutQuestion() {
        MenuButton ancienButtonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");
        if (ancienButtonAjout != null) {
            anchorPaneId.getChildren().remove(ancienButtonAjout);
        }

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

            anchorPaneId.getChildren().add(buttonAjoutQuestion);
        }
    }

    protected void creerBoutonestimer() {
        Button ancienButtonEstimer = (Button) borderPaneId.getScene().lookup("#boutonEstimer");
        if (ancienButtonEstimer != null) {
            anchorPaneId.getChildren().remove(ancienButtonEstimer);
            if (borderPaneId.getScene().lookup("#estimationTexte") != null) {
                anchorPaneId.getChildren().remove((Label) anchorPaneId.getScene().lookup("#estimationTexte"));
            }
        }
        if (difficulte.equals("Facile")
                && !buttonAttribut1.getText().equals("___")) {
            Button estimerButton = new Button("Estimer");
            estimerButton.setId("boutonEstimer");
            estimerButton.setOnAction(estimerEliminationEvent);
            AnchorPane.setBottomAnchor(estimerButton, 15.);
            AnchorPane.setRightAnchor(estimerButton, 80.);
            anchorPaneId.getChildren().add(estimerButton);
        }
    }

    protected void deplacerValueButton(MenuButton boutonSuivant, int ligne) {
        MenuButton menuButtonValeur = new MenuButton(boutonSuivant.getText());
        menuButtonValeur.setId("buttonValeur" + ligne);
        AnchorPane.setTopAnchor(menuButtonValeur,
                ((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut" + ligne)).getLayoutY());
        AnchorPane.setLeftAnchor(menuButtonValeur, boutonSuivant.getLayoutX());
        menuButtonValeur.setDisable(true);
        anchorPaneId.getChildren().add(menuButtonValeur);

        if (boutonSuivant.getText().equals("___") || listeAttributsChoisi.size() == 2) {
            ArrayList<String> listeValeurs = partieEnCour.getListeValeurs(
                    ((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut" + ligne)).getText());
            for (int i = 0; i < listeValeurs.size(); i++) {
                MenuItem valeurs = new MenuItem(listeValeurs.get(i));
                valeurs.setId(menuButtonValeur.getId() + i);
                valeurs.setOnAction(valeurSelectedEvent);
                menuButtonValeur.getItems().add(valeurs);
            }
        }
    }

    protected void creerBoutonValider() {
        Button ancienButtonValider = (Button) borderPaneId.getScene().lookup("#buttonValiderQuestion");
        if (ancienButtonValider != null) {
            anchorPaneId.getChildren().remove(ancienButtonValider);
        }
        if (buttonAttribut1.getText() != "___") {
            Button buttonValiderQuestion = new Button("Valider");
            buttonValiderQuestion.setId("buttonValiderQuestion");
            buttonValiderQuestion.setOnAction(validerQuestionEvent);

            AnchorPane.setBottomAnchor(buttonValiderQuestion, 15.);
            AnchorPane.setRightAnchor(buttonValiderQuestion, 20.);
            anchorPaneId.getChildren().add(buttonValiderQuestion);
        }
    }

    protected ArrayList<String> creerListeQuestion() {
        ArrayList<String> listeQuestion = new ArrayList<>();

        // recuperer les valeurs et enleve
        Scene scene = borderPaneId.getScene();

        // cree les talbeaus attribut valeur et conecteur
        for (int i = 1; i <= listeAttributsChoisi.size() + 1; i++) {
            // recuperer attributs
            MenuButton buttonAttribut = (MenuButton) scene.lookup("#buttonAttribut" + i);
            if (buttonAttribut != null && !buttonAttribut.getText().equals("___")) {
                MenuButton buttonValeur = (MenuButton) scene.lookup("#buttonValeur" + i);
                listeAttribut.add(buttonAttribut.getText());

                // recuperer valeurs
                if (buttonValeur == null) {
                    listeValeur.add("oui");
                    listeQuestion.add(buttonAttribut.getText());
                } else if (!buttonValeur.getText().equals("___")) {
                    listeValeur.add(buttonValeur.getText());
                    listeQuestion.add(buttonAttribut.getText() + " " + buttonValeur.getText());

                } else {
                    listeAttribut.remove(buttonAttribut.getText());
                }

                // recuperer connecteur
                if (i > 1) {
                    aPlusieurQuestion = true;
                    Label connecteur = (Label) scene.lookup("#questionText" + i);
                    if (connecteur != null) {
                        String textConnecteur = connecteur.getText().substring(0, 2);
                        listeConnecteur.add(textConnecteur);
                        String valI = textConnecteur + " " + listeQuestion.get(i - 1);
                        listeQuestion.remove(i - 1);
                        listeQuestion.add(valI);
                    }
                }
            }
        }
        return listeQuestion;
    }

    // #region event handler

    // #region selectionner les personnages a eliminer
    EventHandler<MouseEvent> afficheCibleEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent actionEvent) {
            if (attendSelection) {
                ImageView cibleActuel = (ImageView) actionEvent.getSource();
                String[] coordonnee = cibleActuel.getId().split("_");
                if (!cibleActuel.getId().split("_")[0].equals("cible")) {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");

                    File f = new File("images/ciblepng.png");
                    Image imageCible = new Image("file:///" + f.getAbsolutePath());
                    ImageView imageViewCible = new ImageView(imageCible);
                    imageViewCible.setFitHeight(125);
                    imageViewCible.setFitWidth(90);
                    imageViewCible.setId("cible_" + coordonnee[1] + "_" + coordonnee[2] + "_" + coordonnee[0]);
                    imageViewCible.setOnMouseClicked(afficheCibleEvent);
                    grilleperso.add(imageViewCible, Integer.parseInt(coordonnee[1]), Integer.parseInt(coordonnee[2]));

                    // mettre dans la liste le perso éliminé
                    listeIdPersoSelectionne.add(cibleActuel.getId());

                } else {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    File f = new File("images/personnages/" + cibleActuel.getId().split("_")[3] + ".png");
                    Image imagePerso = new Image("file:///" + f.getAbsolutePath());
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    imageViewPerso.setFitHeight(125);
                    imageViewPerso.setFitWidth(90);
                    imageViewPerso.setId(cibleActuel.getId().split("_")[3] + "_" + Integer.parseInt(coordonnee[1]) + "_"
                            + Integer.parseInt(coordonnee[2]));
                    imageViewPerso.setOnMouseClicked(afficheCibleEvent);
                    grilleperso.add(imageViewPerso, Integer.parseInt(coordonnee[1]), Integer.parseInt(coordonnee[2]));

                    grilleperso.getChildren().remove(cibleActuel);
                    listeIdPersoSelectionne.remove(cibleActuel.getId().split("_")[3] + "_"
                            + Integer.parseInt(coordonnee[1]) + "_" + Integer.parseInt(coordonnee[2]));
                }
            }
        }
    };
    // #endregion

    // #region ajouter une question
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
            anchorPaneId.getChildren().add(deleteButton);

            // ajout l'attribut a la liste
            listeAttributsChoisi.add(currentMenuButtonAttribut.getText());

            MenuButton buttonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");
            buttonAjout.setVisible(false);
            buttonAjout.setId("disabled");

            Label texteAjoutQuestion = new Label(currentItem.getText() + " le personnage est-il ou a-t-il :");
            texteAjoutQuestion.setId("questionText" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(texteAjoutQuestion, (listeAttributsChoisi.size()) * 40.);
            AnchorPane.setLeftAnchor(texteAjoutQuestion, 5.0);
            anchorPaneId.getChildren().add(texteAjoutQuestion);

            MenuButton menuButtonAttribut = new MenuButton("___");
            menuButtonAttribut.setId("buttonAttribut" + (listeAttributsChoisi.size() + 1));
            AnchorPane.setTopAnchor(menuButtonAttribut, (listeAttributsChoisi.size()) * 40. - 5);
            AnchorPane.setLeftAnchor(menuButtonAttribut, 181.0);
            anchorPaneId.getChildren().add(menuButtonAttribut);

            creerDernierMenuBouton(menuButtonAttribut);
            creerBoutonestimer();
        }
        // gerer quand nbattribut > 10
    };
    // #endregion

    // #region affichage valeur bouton et affichage bouton ajout question
    EventHandler<ActionEvent> valeurSelectedEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            MenuItem currentItem = (MenuItem) actionEvent.getSource();
            String selectedValue = currentItem.getText();
            ((MenuButton) borderPaneId.getScene()
                    .lookup("#" + currentItem.getId().substring(0, 13)))
                    .setText(selectedValue);

            if (listeIdPersoSelectionne.size() > 0) {
                GridPane grillePerso = new GridPane();
                creerGrille(grillePerso);
            }
            creerBoutonAjoutQuestion();
            creerBoutonValider();
            creerBoutonestimer();
        }
    };
    // #endregion

    // #region Met en texte le choix selectionné et fait apparaitre les valeusr de
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
                anchorPaneId.getChildren().remove(buttonAjout);

            // affichage deuxieme bouton
            MenuButton valueButton = (MenuButton) mainScene.lookup("#buttonValeur" + currentItem.getId()
                    .substring(currentItem.getId().length() - 2, currentItem.getId().length() - 1));
            boolean estBinaire = partieEnCour.estQuestionBinaire(selectedAttribut);

            if (!estBinaire) {
                if (valueButton != null)
                    anchorPaneId.getChildren().remove(valueButton);

                // creation du bouton des valeurs
                ArrayList<String> listeValeurs = partieEnCour.getListeValeurs(selectedAttribut);

                if (listeValeurs.isEmpty()) {
                    anchorPaneId.getChildren().remove(valueButton);
                } else {
                    MenuButton menuButtonValeurs = new MenuButton("___");
                    menuButtonValeurs.setId("buttonValeur" + currentItem.getId()
                            .substring(currentItem.getId().length() - 2, currentItem.getId().length() - 1));

                    // placement du bouton
                    AnchorPane.setTopAnchor(menuButtonValeurs, currentMenuButton.getLayoutY());
                    AnchorPane.setLeftAnchor(menuButtonValeurs, currentMenuButton.getLayoutX() + 35
                            + (new Text(selectedAttribut)).getBoundsInLocal().getWidth());
                    anchorPaneId.getChildren().add(menuButtonValeurs);

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
                    anchorPaneId.getChildren().remove(valueButton);
                if (listeIdPersoSelectionne.size() > 0) {
                    GridPane grillePerso = new GridPane();
                    creerGrille(grillePerso);
                }
                creerBoutonAjoutQuestion();
                creerBoutonValider();
                creerBoutonestimer();
            }
        }
    };
    // #endregion

    // #region Affiche le nbr de personnage à eliminer
    EventHandler<ActionEvent> estimerEliminationEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (anchorPaneId.getScene().lookup("#estimationTexte") == null) {
                // recuperer les valeurs et enleve
                Scene scene = borderPaneId.getScene();
                ArrayList<String> listeAttribut = new ArrayList<>();
                ArrayList<String> listeValeur = new ArrayList<>();
                ArrayList<String> listeConnecteur = new ArrayList<>();

                // vide la liste des attributs selecionné
                listeIdPersoSelectionne.clear();

                // cree les talbeaus attribut valeur et conecteur
                for (int i = 1; i <= listeAttributsChoisi.size() + 1; i++) {
                    // recuperer attributs
                    MenuButton buttonAttribut = (MenuButton) scene.lookup("#buttonAttribut" + i);
                    if (buttonAttribut != null && !buttonAttribut.getText().equals("___")) {
                        MenuButton buttonValeur = (MenuButton) scene.lookup("#buttonValeur" + i);
                        listeAttribut.add(buttonAttribut.getText());

                        // recuperer valeurs
                        if (buttonValeur == null) {
                            listeValeur.add("oui");
                        } else if (!buttonValeur.getText().equals("___")) {
                            listeValeur.add(buttonValeur.getText());

                        } else {
                            listeAttribut.remove(buttonAttribut.getText());
                        }

                        // recuperer connecteur
                        if (i > 1) {
                            Label connecteur = (Label) scene.lookup("#questionText" + i);
                            if (connecteur != null) {
                                String textConnecteur = connecteur.getText().substring(0, 2);
                                listeConnecteur.add(textConnecteur);
                            }
                        }
                    }
                }

                if (listeAttribut.size() > 0) {
                    ArrayList<String> listePersoAEliminer = partieEnCour.personnagesAEliminer(
                            listeTotalPersoElimine, listeAttribut,
                            listeValeur, listeConnecteur);
                    Label estimationLabel = new Label("Elimination de "
                            + listePersoAEliminer.size()
                            + " sur " + ((partieEnCour.getNombrePersonnages() - listeTotalPersoElimine.size())));
                    estimationLabel.setId("estimationTexte");
                    AnchorPane.setRightAnchor(estimationLabel, 150.);
                    AnchorPane.setBottomAnchor(estimationLabel, 20.);
                    anchorPaneId.getChildren().add(estimationLabel);

                    // placement cible sur les personnages estimés
                    GridPane grillePerso = new GridPane();
                    creerGrille(grillePerso);

                    Object[] listeImage = (Object[]) grillePerso.getChildren().toArray();

                    for (Object image : listeImage) {
                        String[] idSplit = ((String) ((ImageView) image).getId()).split("_");
                        if (listePersoAEliminer.contains(idSplit[0]) && !listeTotalPersoElimine.contains(idSplit[0])) {

                            File f = new File("images/ciblepng.png");
                            Image imageCible = new Image("file:///" + f.getAbsolutePath());
                            ImageView imageViewCible = new ImageView(imageCible);
                            imageViewCible.setFitHeight(125);
                            imageViewCible.setFitWidth(90);
                            imageViewCible.setId("cible_" + idSplit[1] + "_" + idSplit[2] + "_" + idSplit[0]);
                            grillePerso.add(imageViewCible, Integer.parseInt(idSplit[1]),
                                    Integer.parseInt(idSplit[2]));

                            // mettre dans la liste le perso éliminé
                            listeIdPersoSelectionne.add(((ImageView) image).getId());
                        }
                    }
                }
            }

        }
    };
    // #endregion

    // #region supprime une question
    EventHandler<ActionEvent> deleteQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            Button currentButton = (Button) actionEvent.getSource();
            int currentFlour = Integer.parseInt(currentButton.getId().substring(12, currentButton.getId().length()));
            Scene scene = borderPaneId.getScene();
            String attributActuel = ((MenuButton) scene.lookup("#buttonAttribut" + currentFlour)).getText();

            for (int i = currentFlour; i <= listeAttributsChoisi.size(); i++) {
                // le texte
                ((Label) scene.lookup("#questionText" + i))
                        .setText(((Label) scene.lookup("#questionText" + (i + 1))).getText());

                // l'attribut
                ((MenuButton) scene.lookup("#buttonAttribut" + i))
                        .setText(((MenuButton) scene.lookup("#buttonAttribut" + (i + 1))).getText());

                // valeur
                MenuButton currentValueButton = (MenuButton) scene.lookup("#buttonValeur" + i);
                MenuButton nextValueButton = (MenuButton) scene.lookup("#buttonValeur" + (i + 1));

                if (currentValueButton == null && nextValueButton != null) {
                    // creer le bouton avec la valeur
                    deplacerValueButton(nextValueButton, i);
                } else if (currentValueButton != null && nextValueButton != null) {
                    // changer la valeur
                    anchorPaneId.getChildren().remove(currentValueButton);
                    deplacerValueButton(nextValueButton, i);
                } else if (currentValueButton != null && nextValueButton == null) {
                    // supprimer le bouton
                    anchorPaneId.getChildren().remove(currentValueButton);
                }
            }

            // supprimer le dernier
            anchorPaneId.getChildren().remove(scene.lookup("#questionText" + (listeAttributsChoisi.size() + 1)));
            anchorPaneId.getChildren().remove(scene.lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1)));
            MenuButton lastValueButton = (MenuButton) scene.lookup("#buttonValeur" + (listeAttributsChoisi.size() + 1));
            if (lastValueButton != null)
                anchorPaneId.getChildren().remove(lastValueButton);
            anchorPaneId.getChildren().remove(scene.lookup("#deleteButton" + (listeAttributsChoisi.size())));

            // supprime l'element de la liste
            listeAttributsChoisi.remove(attributActuel);
            creerDernierMenuBouton((MenuButton) scene.lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1)));

            // reactive le dernier et ajouter le bouton ajoutquestion si besoin
            MenuButton lastUsableAttributButton = (MenuButton) scene
                    .lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1));
            MenuButton lastUsableValueButton = (MenuButton) scene
                    .lookup("#buttonValeur" + (listeAttributsChoisi.size() + 1));

            lastUsableAttributButton.setDisable(false);
            if (lastUsableValueButton != null)
                lastUsableValueButton.setDisable(false);
            if (!lastUsableAttributButton.getText().equals("___")
                    && (lastUsableValueButton == null || !lastUsableValueButton.getText().equals("___"))) {
                creerBoutonAjoutQuestion();
            }

            if (currentFlour == 1) {
                String premierTexte = questionText1.getText();
                premierTexte = "L" + premierTexte.substring(4, premierTexte.length());
                questionText1.setText(premierTexte);
            }

            if (listeIdPersoSelectionne.size() > 0) {
                GridPane grillePerso = new GridPane();
                creerGrille(grillePerso);
            }
            // deplacer le bouton valider
            creerBoutonValider();
            creerBoutonestimer();
        }
    };
    // #endregion

    // #region verifier la reponse de la question
    EventHandler<ActionEvent> validerQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            attendSelection = true;

            ArrayList<String> listeQuestion = creerListeQuestion();

            anchorPaneId.getChildren().clear();

            // verifier reponse
            boolean reponseQuestion = partieEnCour.verifierReponse(listeAttribut, listeValeur, listeConnecteur);

            // afiche la reponse
            Label reponseText = new Label();
            if (reponseQuestion) {
                if (aPlusieurQuestion) {
                    String reponse = "La réponse est : VRAI.\nVos critères étaient : ";
                    for (String question : listeQuestion) {
                        reponse += question + "\n";
                    }
                    reponseText.setText(reponse);
                } else {
                    String reponse = "La réponse est : VRAI.\nVotre critère était : ";
                    for (String question : listeQuestion) {
                        reponse += question + "\n";
                    }
                    reponseText.setText(reponse);
                }
            } else {
                if (aPlusieurQuestion) {
                    String reponse = "La réponse est : FAUX.\nVos critères étaient : ";
                    for (String question : listeQuestion) {
                        reponse += question + "\n";
                    }
                    reponseText.setText(reponse);
                } else {
                    String reponse = "La réponse est : FAUX.\nVotre critère était : ";
                    for (String question : listeQuestion) {
                        reponse += question + "\n";
                    }
                    reponseText.setText(reponse);
                }
            }
            AnchorPane.setTopAnchor(reponseText, 5.);
            AnchorPane.setLeftAnchor(reponseText, 5.);
            anchorPaneId.getChildren().add(reponseText);

            // affiche texte pour selection personnage + ajout bouton validation selection
            Label validationText = new Label("Veuillez cliquer sur les personnages à éliminer.");
            if (difficulte.equals("Facile") && listeIdPersoSelectionne.size() > 0) {
                attendSelection = false;
                if (reponseQuestion) {
                    if (aPlusieurQuestion) {
                        validationText = new Label(
                                "Cliquez sur 'Valider' pour élimner le(s) personne(s) qui ne correspondent pas à ces critères.");
                    } else {
                        validationText = new Label(
                                "Cliquez sur 'Valider' pour élimner le(s) personne(s) qui ne correspondent pas à ce critère.");
                    }
                } else {
                    if (aPlusieurQuestion) {
                        validationText = new Label(
                                "Cliquez sur 'Valider' pour élimner le(s) personne(s) qui correspondent à ces critères.");
                    } else {
                        validationText = new Label(
                                "Cliquez sur 'Valider' pour élimner le(s) personne(s) qui correspondent à ce critère.");
                    }
                }

                // inverse les cileble si la réponse est fausse
                if (reponseQuestion) {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    grilleperso.setId("oldGrillePerso");
                    Object[] listeImagesAncienneGrille = (Object[]) grilleperso.getChildren().toArray();
                    borderPaneId.getChildren().remove(grilleperso);

                    // replace tous les personnages et place ce qui sont mort
                    GridPane newGrilleperso = new GridPane();
                    creerGrille(newGrilleperso);

                    // place les cibles sur les personnges qui n'en avait pas et les ajoutes dans la
                    // liste
                    for (Object ancienneImage : listeImagesAncienneGrille) {
                        String id = ((ImageView) ancienneImage).getId();
                        String[] idSplit = ((String) ((ImageView) ancienneImage).getId()).split("_");

                        if (!listeIdPersoSelectionne.contains(id) && !idSplit[0].equals("cible")
                                && !idSplit[0].equals("mort") && !listeTotalPersoElimine.contains(idSplit[0])) {
                            File f = new File("images/ciblepng.png");
                            Image imageCible = new Image("file:///" + f.getAbsolutePath());
                            ImageView imageViewCible = new ImageView(imageCible);
                            imageViewCible.setFitHeight(125);
                            imageViewCible.setFitWidth(90);
                            imageViewCible.setId("cible_" + idSplit[1] + "_" + idSplit[2] + "_" + idSplit[0]);
                            newGrilleperso.add(imageViewCible, Integer.parseInt(idSplit[1]),
                                    Integer.parseInt(idSplit[2]));

                            // mettre dans la liste le perso éliminé
                            listeIdPersoSelectionne.add(id);
                        } else {
                            listeIdPersoSelectionne.remove(id);
                        }
                    }

                    borderPaneId.setCenter(newGrilleperso);
                }
            }
            AnchorPane.setTopAnchor(validationText, (listeQuestion.size() * 40.) + 20.);
            AnchorPane.setLeftAnchor(validationText, 5.);
            anchorPaneId.getChildren().add(validationText);

            Button validerValidation = new Button("Valider");
            AnchorPane.setTopAnchor(validerValidation, 5.);
            AnchorPane.setRightAnchor(validerValidation, 20.);
            validerValidation.setOnAction(verifierEliminationEvent);
            anchorPaneId.getChildren().add(validerValidation);

        }
    };
    // #endregion

    // #region verifie les personnages éliminé
    EventHandler<ActionEvent> verifierEliminationEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            // tue tous les personnages selectionné
            ArrayList<String> nomsPerso = new ArrayList<>();
            for (String perso : listeIdPersoSelectionne) {
                nomsPerso.add(perso.split("_")[0]);
            }
            boolean personnageAtrouverElimine = partieEnCour.verifierElimination(nomsPerso);

            if (!personnageAtrouverElimine) {
                listeTotalPersoElimine.addAll(nomsPerso);
                // si tous les perso sont éliminé sauf le bon
                if (listeTotalPersoElimine.size() == partieEnCour.getNombrePersonnages() - 1) {
                    // vide l'écran, affiche le personnage gagant et supprime la save.
                    borderPaneId.getChildren().clear();
                    File fileSave = new File("CestQuiGame/bin/save.json");
                    fileSave.delete();

                    // texte
                    AnchorPane pageFinale = new AnchorPane();
                    String personnageChoisi = partieEnCour.getPersonnageChoisi();
                    Label texteGagner = new Label("Bravo ! Vous avez gagné ! Le personnage était bien "
                            + personnageChoisi + " :)");
                    AnchorPane.setTopAnchor(texteGagner, 210.);
                    AnchorPane.setLeftAnchor(texteGagner, 360.);
                    AnchorPane.setRightAnchor(texteGagner, 350.);

                    // image Perso
                    File dossierImage = new File(cheminVersImages);
                    String urlImage = dossierImage.getAbsolutePath() + "/" + personnageChoisi + ".png";
                    Image imagePerso = new Image("file:///" + urlImage);
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    imageViewPerso.setFitHeight(125);
                    imageViewPerso.setFitWidth(90);
                    AnchorPane.setTopAnchor(imageViewPerso, 240.);
                    AnchorPane.setLeftAnchor(imageViewPerso, 500.);
                    AnchorPane.setRightAnchor(imageViewPerso, 500.);

                    // bouton quitter
                    Button buttonQuitter = new Button("Quitter");
                    buttonQuitter.setId("buttonRejouer");
                    buttonQuitter.setOnAction(quitterEvent);
                    AnchorPane.setTopAnchor(buttonQuitter, 400.);
                    AnchorPane.setLeftAnchor(buttonQuitter, 500.);
                    AnchorPane.setRightAnchor(buttonQuitter, 500.);

                    pageFinale.getChildren().addAll(texteGagner, imageViewPerso, buttonQuitter);
                    borderPaneId.setCenter(pageFinale);

                } else {
                    // enleve tous les boutons
                    anchorPaneId.getChildren().clear();
                    listeAttributsChoisi.clear();
                    attendSelection = false;

                    // remet les permiers boutons si on a pas éliminé le mauvais perso
                    Label questionText1 = new Label("Le personnage est-il ou a-t-il :");
                    questionText1.setId("questionText1");
                    AnchorPane.setTopAnchor(questionText1, 5.);
                    AnchorPane.setLeftAnchor(questionText1, 5.);
                    anchorPaneId.getChildren().add(questionText1);

                    MenuButton buttonAttribut1 = new MenuButton("___");
                    buttonAttribut1.setId("buttonAttribut1");
                    buttonAttribut1.setLayoutX(168.0);
                    buttonAttribut1.setLayoutY(1.0);
                    anchorPaneId.getChildren().add(buttonAttribut1);

                    creerDernierMenuBouton(buttonAttribut1);
                    creerBoutonestimer();

                    // reset ancienne grille
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    borderPaneId.getChildren().remove(grilleperso);

                    // recreer une nouvelle
                    GridPane newGrillePerso = new GridPane();
                    creerGrille(newGrillePerso);

                    // affiche les tete de mort
                    for (String perso : listeIdPersoSelectionne) {

                        // change valeur etat perso
                        partieEnCour.tuerPersonnage(perso.split("_")[0]);

                        int i = Integer.parseInt(perso.split("_")[1]);
                        int j = Integer.parseInt(perso.split("_")[2]);

                        File f2 = new File("images/mortpng.png");
                        Image imageMort = new Image("file:///" + f2.getAbsolutePath());
                        ImageView imageViewMort = new ImageView(imageMort);
                        imageViewMort.setFitHeight(125);
                        imageViewMort.setFitWidth(90);
                        imageViewMort.setId("mort_" + i + "_" + j);
                        newGrillePerso.add(imageViewMort, i, j);
                    }
                    partieEnCour.sauvegarderPartieEnCour(cheminVersImages, ligne, colonne);
                    listeIdPersoSelectionne.clear();
                }
            } else {
                borderPaneId.getChildren().clear();

                Label textePerdu = new Label("Vous avez perdu car vous avez éliminé "
                        + partieEnCour.getPersonnageChoisi() + ", dommage... :(");
                borderPaneId.setCenter(textePerdu);
            }
        }
    };
    // #endregion

    // #region ferme le programme
    EventHandler<ActionEvent> quitterEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            ((Stage) borderPaneId.getScene().getWindow()).close();
            System.exit(0);
        }
    };
    // #endregion

    // #endregion

    // #region setter
    public static void setDifficulte(String d) {
        difficulte = d;
    }

    public static void setJson(String js) {
        json = js;
    }

    public void setBorderPaneId(BorderPane borderPaneId) {
        this.borderPaneId = borderPaneId;
    }

    public void setAnchorPaneId(AnchorPane anchorPaneId) {
        this.anchorPaneId = anchorPaneId;
    }

    public void setQuestionText1(Label questionText1) {
        this.questionText1 = questionText1;
    }

    public void setButtonAttribut1(MenuButton buttonAttribut1) {
        this.buttonAttribut1 = buttonAttribut1;
    }
    // #endregion
}
