import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javafx.event.EventHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainSceneController {
    private static Game partieEnCour;
    private String cheminVersImages;
    private int ligne;
    private int colonne;
    private static List<String> listeAttributs;

    private static ArrayList<String> listeAttributsChoisi = new ArrayList<>();
    private static ArrayList<String> listePersoSelectionne = new ArrayList<>();
    private static ArrayList<String> listeTotalPersoElimine = new ArrayList<>();
    private static boolean attendSelection = false;
    private static String difficulte;
    private static String json;

    public static void setDifficulte(String d) {
        difficulte = d;
    }

    public static void setJson(String js) {
        json = js;
    }

    private static FilenameFilter imageFiltre = new FilenameFilter() {
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

    @FXML
    private BorderPane borderPaneId;
    @FXML
    private AnchorPane AnchorPaneId;
    @FXML
    private Label questionText1;
    @FXML
    private MenuButton buttonAttribut1;

    @FXML
    protected void initialize() {
        // Recuperer les données du JSON ici
        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader(json));

            cheminVersImages = (String) js.get("images");
            ligne = Integer.parseInt((String) js.get("ligne"));
            colonne = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");

            // javafx
            GridPane grilleperso = new GridPane();
            grilleperso.setId("grillePerso");
            grilleperso.setHgap(colonne);
            grilleperso.setVgap(ligne);
            grilleperso.setMaxSize(100, 50);

            int x = 0; // colonne
            int y = 0; // ligne
            File dossierImage = new File(cheminVersImages);
            if (dossierImage.isDirectory()) {
                if ((String) js.get("difficulte") != null) {
                    // partie chargée
                    difficulte = ((String) js.get("difficulte"));
                    partieEnCour = new Game(difficulte, personnages, ligne, colonne,
                            (JSONObject) js.get("personnagesChoisi"));
                    ArrayList<String> listePersonnageMort = partieEnCour.getListePersonnageMort();
                    for (File image : dossierImage.listFiles(imageFiltre)) {
                        String nomImage = image.getName();
                        String urlImage = image.getAbsolutePath();
                        Image imagePerso = new Image(urlImage);
                        ImageView imageViewPerso = new ImageView(imagePerso);
                        imageViewPerso.setFitHeight(100);
                        imageViewPerso.setFitWidth(70);
                        imageViewPerso
                                .setId(nomImage.substring(0, nomImage.length() - 4) + "_" + x + "_" + y);
                        imageViewPerso.setOnMouseClicked(afficheCibleEvent);
                        grilleperso.add(imageViewPerso, x, y);

                        if (!listePersonnageMort.isEmpty()) {
                            if (listePersonnageMort
                                    .contains(image.getName().substring(0, image.getName().length() - 4))) {
                                File f2 = new File("../programmes/images/mortpng.png");
                                Image imageMort = new Image(f2.getAbsolutePath());
                                ImageView imageViewMort = new ImageView(imageMort);
                                imageViewMort.setId("mort_" + x + "_" + y);
                                imageViewMort.setFitHeight(100);
                                imageViewMort.setFitWidth(70);
                                grilleperso.add(imageViewMort, x, y);

                            }
                            x++;
                            if (x == colonne) {
                                x = 0;
                                y++;
                            }
                        }
                    }
                } else {
                    // nouvelle partie
                    partieEnCour = new Game(difficulte, personnages, ligne,
                            colonne);
                    for (File image : dossierImage.listFiles(imageFiltre)) {
                        String nomImage = image.getName();

                        String urlImage = image.getAbsolutePath();
                        Image imagePerso = new Image(urlImage);
                        ImageView imageViewPerso = new ImageView(imagePerso);
                        imageViewPerso.setFitHeight(100);
                        imageViewPerso.setFitWidth(70);
                        imageViewPerso
                                .setId(nomImage.substring(0, nomImage.length() - 4) + "_" + x + "_" + y);
                        imageViewPerso.setOnMouseClicked(afficheCibleEvent);
                        grilleperso.add(imageViewPerso, x, y);

                        x++;
                        if (x == colonne) {
                            x = 0;
                            y++;
                        }
                    }
                }
                borderPaneId.setCenter(grilleperso);
            }

            listeAttributs = partieEnCour.getListeAttributs();
            creerDernierMenuBouton(buttonAttribut1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void creerBoutonAjoutQuestion() {
        MenuButton ancienButtonAjout = (MenuButton) borderPaneId.getScene().lookup("#buttonAjoutQuestion");
        if (ancienButtonAjout != null) {
            AnchorPaneId.getChildren().remove(ancienButtonAjout);
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
            List<String> listeValeurs = partieEnCour.getListeValeurs(
                    ((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut" + ligne)).getText());
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

    private void creerBoutonValider() {
        Button ancienButtonValider = (Button) borderPaneId.getScene().lookup("#buttonValiderQuestion");
        if (ancienButtonValider != null) {
            AnchorPaneId.getChildren().remove(ancienButtonValider);
        }
        if (((MenuButton) borderPaneId.getScene().lookup("#buttonAttribut1")).getText() != "___") {
            Button buttonValiderQuestion = new Button("Valider");
            buttonValiderQuestion.setId("buttonValiderQuestion");
            buttonValiderQuestion.setOnAction(valiquerQuestionEvent);

            AnchorPane.setBottomAnchor(buttonValiderQuestion, 15.);
            AnchorPane.setRightAnchor(buttonValiderQuestion, 20.);
            AnchorPaneId.getChildren().add(buttonValiderQuestion);
        }
    }

    private void creerBoutonestimer() {
        if (difficulte.equals("Facile")) {
            Button ancienButtonEstimer = (Button) borderPaneId.getScene().lookup("#boutonEstimer");
            if (ancienButtonEstimer != null) {
                AnchorPaneId.getChildren().remove(ancienButtonEstimer);
                if (borderPaneId.getScene().lookup("#estimationTexte") != null) {
                    System.out.println("je suis la ");
                    AnchorPaneId.getChildren().remove((Label) AnchorPaneId.getScene().lookup("#estimationTexte"));
                }
            }
            Button estimerButton = new Button("Estimer");
            estimerButton.setId("boutonEstimer");
            estimerButton.setOnAction(estimerEliminationEvent);
            AnchorPane.setBottomAnchor(estimerButton, 15.);
            AnchorPane.setRightAnchor(estimerButton, 80.);
            AnchorPaneId.getChildren().add(estimerButton);
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
                creerBoutonValider();
                creerBoutonestimer();
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
            creerBoutonValider();
            creerBoutonestimer();
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
            creerBoutonestimer();
        }
        // gerer quand nbattribut > 10
    };

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
                    AnchorPaneId.getChildren().remove(currentValueButton);
                    deplacerValueButton(nextValueButton, i);
                } else if (currentValueButton != null && nextValueButton == null) {
                    // supprimer le bouton
                    AnchorPaneId.getChildren().remove(currentValueButton);
                }
            }

            // supprimer le dernier
            AnchorPaneId.getChildren().remove(scene.lookup("#questionText" + (listeAttributsChoisi.size() + 1)));
            AnchorPaneId.getChildren().remove(scene.lookup("#buttonAttribut" + (listeAttributsChoisi.size() + 1)));
            MenuButton lastValueButton = (MenuButton) scene.lookup("#buttonValeur" + (listeAttributsChoisi.size() + 1));
            if (lastValueButton != null)
                AnchorPaneId.getChildren().remove(lastValueButton);
            AnchorPaneId.getChildren().remove(scene.lookup("#deleteButton" + (listeAttributsChoisi.size())));

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
                String premierTexte = ((Label) scene.lookup("#questionText1")).getText();
                premierTexte = "L" + premierTexte.substring(4, premierTexte.length());
                ((Label) scene.lookup("#questionText1")).setText(premierTexte);
            }

            // deplacer le bouton valider
            creerBoutonValider();
            creerBoutonestimer();
        }
    };

    EventHandler<ActionEvent> valiquerQuestionEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            ArrayList<String> listeQuestion = new ArrayList<>();

            // recuperer les valeurs et enleve
            Scene scene = borderPaneId.getScene();
            ArrayList<String> listeAttribut = new ArrayList<>();
            ArrayList<String> listeValeur = new ArrayList<>();
            ArrayList<String> listeConnecteur = new ArrayList<>();

            boolean aPlusieurQuestion = false;
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

            AnchorPaneId.getChildren().clear();

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
            AnchorPaneId.getChildren().add(reponseText);

            // affiche texte pour selection personnage + ajout bouton validation selection
            Label validationText;
            if (!difficulte.equals("Facile")) {
                attendSelection = true;
                validationText = new Label("Veuillez cliquer sur les personnages à éliminer.");
            } else {
                if (aPlusieurQuestion) {
                    validationText = new Label(
                            "Validez pour élimner le(s) personne(s) qui ne correspondent pas à ces critères.");
                } else {
                    validationText = new Label(
                            "Validez pour élimner le(s) personne(s) qui ne correspondent pas à ce critère.");
                }

                // inverse les cileble si la réponse est fausse
                if (!reponseQuestion) {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    Object[] listeImage = (Object[]) grilleperso.getChildren().toArray();

                    for (Object image : listeImage) {
                        System.out.println(image);
                        String[] idSplit = ((String) ((ImageView) image).getId()).split("_");
                        System.out.println(idSplit[0]);
                        if (idSplit[0].equals("cible")) {
                            File dossierImage = new File(cheminVersImages);

                            //recupere image
                            File f = new File(dossierImage.getAbsolutePath() + "/" + idSplit[3]);
                            Image imagePerso = new Image(f.getAbsolutePath());
                            ImageView imageViewPerso = new ImageView(imagePerso);
                            imageViewPerso.setFitHeight(100);
                            imageViewPerso.setFitWidth(70);
                            imageViewPerso.setId("cible_" + idSplit[1] + "_" + idSplit[2] + "_" + idSplit[0]);
                            grilleperso.add(imageViewPerso, Integer.parseInt(idSplit[1]), Integer.parseInt(idSplit[2]));

                            // mettre dans la liste le perso éliminé
                            listePersoSelectionne.add(idSplit[0]);
                            //ici
                        }
                    }
                }
            }
            AnchorPane.setTopAnchor(validationText, (listeQuestion.size() * 40.) + 20.);
            AnchorPane.setLeftAnchor(validationText, 5.);
            AnchorPaneId.getChildren().add(validationText);

            Button validerValidation = new Button("Valider");
            AnchorPane.setTopAnchor(validerValidation, 5.);
            AnchorPane.setRightAnchor(validerValidation, 20.);
            validerValidation.setOnAction(varifierEliminationEvent);
            AnchorPaneId.getChildren().add(validerValidation);

        }
    };

    // selectionne les personnages a eliminer apres avoir valider la/les question(s)
    EventHandler<MouseEvent> afficheCibleEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent actionEvent) {
            if (attendSelection) {
                ImageView cibleActuel = (ImageView) actionEvent.getSource();
                String[] coordonnee = cibleActuel.getId().split("_");
                if (!cibleActuel.getId().split("_")[0].equals("cible")) {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");

                    File f = new File("../programmes/images/ciblepng.png");
                    Image imageCible = new Image(f.getAbsolutePath());
                    ImageView imageViewCible = new ImageView(imageCible);
                    imageViewCible.setFitHeight(100);
                    imageViewCible.setFitWidth(70);
                    imageViewCible.setId("cible_" + coordonnee[1] + "_" + coordonnee[2] + "_" + coordonnee[0]);
                    imageViewCible.setOnMouseClicked(afficheCibleEvent);
                    grilleperso.add(imageViewCible, Integer.parseInt(coordonnee[1]), Integer.parseInt(coordonnee[2]));

                    // mettre dans la liste le perso éliminé
                    listePersoSelectionne.add(cibleActuel.getId());

                } else {
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    File f = new File("../programmes/images/personnages/" + cibleActuel.getId().split("_")[3] + ".png");
                    Image imagePerso = new Image(f.getAbsolutePath());
                    ImageView iamgeViewPerso = new ImageView(imagePerso);
                    iamgeViewPerso.setFitHeight(100);
                    iamgeViewPerso.setFitWidth(70);
                    iamgeViewPerso.setId(cibleActuel.getId().split("_")[3] + "_" + Integer.parseInt(coordonnee[1]) + "_"
                            + Integer.parseInt(coordonnee[2]));
                    iamgeViewPerso.setOnMouseClicked(afficheCibleEvent);
                    grilleperso.add(iamgeViewPerso, Integer.parseInt(coordonnee[1]), Integer.parseInt(coordonnee[2]));

                    listePersoSelectionne.remove(cibleActuel.getId().split("_")[3] + "_"
                            + Integer.parseInt(coordonnee[1]) + "_" + Integer.parseInt(coordonnee[2]));
                }
            }
        }
    };

    EventHandler<ActionEvent> varifierEliminationEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            // tue tous les personnages selectionné
            ArrayList<String> nomPerso = new ArrayList<>();
            for (String perso : listePersoSelectionne) {
                nomPerso.add(perso.split("_")[0]);
            }
            boolean etatPartie = partieEnCour.verifierElimination(nomPerso);

            if (etatPartie) {
                listeTotalPersoElimine.addAll(listePersoSelectionne);
                if (listeTotalPersoElimine.size() == partieEnCour.getNombrePersonnages() - 1) {
                    borderPaneId.getChildren().clear();

                    Label texteGagner = new Label("Bravo ! Vous avez gagné ! Le personnage était bien "
                            + partieEnCour.getPersonnageChoisi() + " :)");
                    borderPaneId.setCenter(texteGagner);
                    File fileSave = new File("../programmes/CestQuiGame/bin/save.json");
                    fileSave.delete();
                } else {
                    // enleve tous les boutons
                    AnchorPaneId.getChildren().clear();
                    listeAttributsChoisi.clear();
                    attendSelection = false;

                    // remet les permiers boutons si on a pas éliminé le mauvais perso
                    Label questionText1 = new Label("Le personnage est-il ou a-t-il :");
                    questionText1.setId("questionText1");
                    AnchorPane.setTopAnchor(questionText1, 5.);
                    AnchorPane.setLeftAnchor(questionText1, 5.);
                    AnchorPaneId.getChildren().add(questionText1);

                    MenuButton buttonAttribut1 = new MenuButton("___");
                    buttonAttribut1.setId("buttonAttribut1");
                    buttonAttribut1.setLayoutX(168.0);
                    buttonAttribut1.setLayoutY(1.0);
                    AnchorPaneId.getChildren().add(buttonAttribut1);

                    creerDernierMenuBouton(buttonAttribut1);
                    creerBoutonestimer();

                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");
                    // affiche les tete de mort
                    for (String perso : listePersoSelectionne) {

                        // change valeur etat perso
                        partieEnCour.tuerPersonnage(perso.split("_")[0]);

                        int i = Integer.parseInt(perso.split("_")[1]);
                        int j = Integer.parseInt(perso.split("_")[2]);

                        borderPaneId.getChildren()
                                .remove((ImageView) borderPaneId.getScene().lookup("#cible_" + i + "_" + j));

                        File f = new File("../programmes/images/personnages/" + perso.split("_")[0] + ".png");
                        Image imagePerso = new Image(f.getAbsolutePath());
                        ImageView iamgeViewPerso = new ImageView(imagePerso);
                        iamgeViewPerso.setFitHeight(100);
                        iamgeViewPerso.setFitWidth(70);
                        grilleperso.add(iamgeViewPerso, i, j);

                        File f2 = new File("../programmes/images/mortpng.png");
                        Image imageMort = new Image(f2.getAbsolutePath());
                        ImageView imageViewMort = new ImageView(imageMort);
                        imageViewMort.setFitHeight(100);
                        imageViewMort.setFitWidth(70);
                        imageViewMort.setId("mort" + i + "_" + j);
                        grilleperso.add(imageViewMort, i, j);
                    }
                    partieEnCour.sauvegarderPartieEnCour(cheminVersImages, ligne, colonne);
                    listePersoSelectionne.clear();
                }
            } else {
                borderPaneId.getChildren().clear();

                Label textePerdu = new Label("Vous avez perdu car vous avez éliminé "
                        + partieEnCour.getPersonnageChoisi() + ", dommage... :(");
                borderPaneId.setCenter(textePerdu);
            }
        }
    };
    // Affiche le nbr de personnage à eliminer
    EventHandler<ActionEvent> estimerEliminationEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            // recuperer les valeurs et enleve
            Scene scene = borderPaneId.getScene();
            ArrayList<String> listeAttribut = new ArrayList<>();
            ArrayList<String> listeValeur = new ArrayList<>();
            ArrayList<String> listeConnecteur = new ArrayList<>();
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
                if (AnchorPaneId.getScene().lookup("#estimationTexte") == null) {
                    ArrayList<String> listePersoAEliminer = partieEnCour.nombrePersonnagesAEliminer(listeAttribut,
                            listeValeur, listeConnecteur);
                    Label estimationLabel = new Label("Elimination de "
                            + listePersoAEliminer.size()
                            + " sur " + partieEnCour.getNombrePersonnages());
                    estimationLabel.setId("estimationTexte");
                    AnchorPane.setRightAnchor(estimationLabel, 150.);
                    AnchorPane.setBottomAnchor(estimationLabel, 20.);
                    AnchorPaneId.getChildren().add(estimationLabel);
                    GridPane grilleperso = (GridPane) borderPaneId.getScene().lookup("#grillePerso");

                    Object[] listeImage = (Object[]) grilleperso.getChildren().toArray();

                    for (Object image : listeImage) {
                        String[] idSplit = ((String) ((ImageView) image).getId()).split("_");
                        if (listePersoAEliminer.contains(idSplit[0])) {

                            File f = new File("../programmes/images/ciblepng.png");
                            Image imageCible = new Image(f.getAbsolutePath());
                            ImageView imageViewCible = new ImageView(imageCible);
                            imageViewCible.setFitHeight(100);
                            imageViewCible.setFitWidth(70);
                            imageViewCible.setId("cible_" + idSplit[1] + "_" + idSplit[2] + "_" + idSplit[0]);
                            grilleperso.add(imageViewCible, Integer.parseInt(idSplit[1]), Integer.parseInt(idSplit[2]));

                            // mettre dans la liste le perso éliminé
                            listePersoSelectionne.add(idSplit[0]);
                        }
                    }
                }
            }

        }
    };
}
