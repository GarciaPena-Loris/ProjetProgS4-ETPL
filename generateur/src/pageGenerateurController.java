
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class pageGenerateurController {

    private static String cheminVersImage;
    private static String ligne;
    private static String colonne;

    private String cheminJson;

    private static boolean estValeursAjoutable = false;
    private static Boolean estOuvertAttribut = false;
    private boolean estNombreImagesCustomsSelectionnees = false;
    private boolean estImageCliquable = false;
    private static int nombrePersonnageTotal = 0;
    private static int nombrePersonnageTermines = 0;
    private int nombreImagesSelectionnees = 0;
    private int nombreImagesNecessaires = -1;
    private ArrayList<ImageView> listeImages = new ArrayList<>();
    private static ArrayList<ImageView> listeImageSelectionnees = new ArrayList<>();
    private static ArrayList<JSONObject> listePersonnages = new ArrayList<>();;
    private static ArrayList<String> listeAttributsStrings = new ArrayList<>();
    private static ArrayList<Label> listeAttributsLabel = new ArrayList<>();
    private static ArrayList<Label> listeSupprLabel = new ArrayList<>();

    private static int etape = 0;
    private static GridPane grillePerso;

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

    private boolean isSaveJsonFile() {
        File dir = new File("bin");
        File[] matches = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("save");
            }
        });
        return matches.length != 0;
    }

    @FXML
    private AnchorPane MainAnchorPane;

    @FXML
    private AnchorPane bottomAnchorPane;

    @FXML
    private BorderPane borderPaneId;

    @FXML
    private ScrollPane zoneImageId;

    @FXML
    private Text colonnesText;

    @FXML
    private Text explicationText;

    @FXML
    private Text ligneText;

    @FXML
    private Label errorText;

    @FXML
    private AnchorPane middleAnchorPane;

    @FXML
    private Spinner<?> spinnerColonne;

    @FXML
    private Spinner<?> spinnerLigne;

    @FXML
    private AnchorPane topAnchorPane;

    @FXML
    private Button choixImagesButton;

    @FXML
    private Button validerButton;

    @FXML
    private Label nombreImageLabel;

    @FXML
    private Button chargerGenerateurButton;

    @FXML
    protected void initialize() {
        MainAnchorPane.setMaxHeight(Screen.getPrimary().getBounds().getHeight() - 80);
        MainAnchorPane.setMinHeight(Screen.getPrimary().getBounds().getHeight() - 80);
        MainAnchorPane.setPrefHeight(Screen.getPrimary().getBounds().getHeight() - 80);

        if (isSaveJsonFile()) {
            chargerGenerateurButton.setVisible(true);
        }

        File logoFile = new File("images/logoGenerateur.png");
        Image logoImage = new Image("file:///" + logoFile.getAbsolutePath());
        ImageView logoVimage = new ImageView(logoImage);
        logoVimage.setFitHeight(137.);
        logoVimage.setFitWidth(904.);
        topAnchorPane.getChildren().add(logoVimage);
        borderPaneId.setPrefHeight(Screen.getPrimary().getBounds().getHeight() - 500);
        spinnerColonne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if ((Integer.parseInt(newValue) > 1 &&
                    Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) > 0
                    || Integer.parseInt(newValue) > 0 &&
                            Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) > 1)
                    && Integer.parseInt(newValue)
                            * Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) <= listeImages
                                    .size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
            if (Integer.parseInt(newValue)
                    * Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) == listeImages
                            .size()) {
                validerButton.setText("Passer aux attributs");
                validerButton.setOnAction(validerButtonEvent);
                estNombreImagesCustomsSelectionnees = false;
            } else {
                validerButton.setText("Passer à la selection d'images");
                validerButton.setOnAction(selectionImageEvent);
                estNombreImagesCustomsSelectionnees = true;
            }
        });
        spinnerLigne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if ((Integer.parseInt(newValue) > 1 &&
                    Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) > 0
                    || Integer.parseInt(newValue) > 0 &&
                            Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) > 1)
                    && Integer.parseInt(newValue)
                            * Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) <= listeImages
                                    .size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
            if (Integer.parseInt(newValue)
                    * Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) == listeImages
                            .size()) {
                validerButton.setText("Passer aux attributs");
                validerButton.setOnAction(validerButtonEvent);
                estNombreImagesCustomsSelectionnees = false;
            } else {
                validerButton.setText("Passer à la selection d'images");
                validerButton.setOnAction(selectionImageEvent);
                estNombreImagesCustomsSelectionnees = true;
            }
        });
    }

    @FXML
    @SuppressWarnings("unchecked")
    void chargerGenerateurEvent(ActionEvent event) {
        // chargar partie
        System.out.println("Chargement...");
        File save = new File("bin/save.json");
        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader(save.getAbsolutePath()));
            long etapeChargée = (long) js.get("etape");
            HashMap<String, String> imagesCoordonneesMap = new HashMap<>();

            if (etapeChargée >= 1) {
                cheminVersImage = (String) js.get("cheminVersImage");
                etape = 1;
                choixImageEvent(new ActionEvent());
            }
            if (etapeChargée >= 2) {
                ligne = (String) js.get("ligne");
                colonne = (String) js.get("colonne");
                ((JSONObject) js.get("listeImageSelectionnees")).forEach((key, value) -> {
                    ImageView iv = new ImageView(new Image((String) ((JSONObject) value).get("url")));
                    String id = (String) ((JSONObject) value).get("id");
                    iv.setId(id);
                    listeImageSelectionnees.add(iv);
                    String[] idSplit = id.split("\\*");
                    String xy = idSplit[1] + "-" + idSplit[2];
                    imagesCoordonneesMap.put(idSplit[0], xy);
                });
                etape = 2;
                estNombreImagesCustomsSelectionnees = true;
                nombreImageLabel.setVisible(false);
                validerButtonEvent.handle(new ActionEvent());
            }
            if (etapeChargée >= 3) {
                ((JSONArray) js.get("listeAttributsStrings")).forEach(attribut -> {
                    listeAttributsStrings.add((String) attribut);
                });
                etape = 3;
                ajoutValeurEvent.handle(new ActionEvent());
            }
            if (etapeChargée >= 4) {
                ((JSONObject) js.get("listePersonnages")).forEach((key, value) -> {
                    String[] xy = imagesCoordonneesMap.get(((JSONObject) value).get("image")).split("-");
                    addValeursPersonnage((JSONObject) value, xy[0], xy[1], validerButton);
                });
                etape = 4;
            }
            if (etapeChargée == 5) {
                passerAuJsonEvent.handle(new ActionEvent());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        chargerGenerateurButton.setVisible(false);

    }

    @FXML
    void choixImageEvent(ActionEvent event) {
        File selectedDirectory;
        if (etape < 1) {
            chargerGenerateurButton.setVisible(false);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedDirectory = directoryChooser.showDialog(MainAnchorPane.getScene().getWindow());
        } else {
            selectedDirectory = new File(cheminVersImage);
        }

        if (selectedDirectory != null) {
            int compteurImage = 0;

            if (selectedDirectory.isDirectory()) {
                int x = 0; // colonne
                int y = 0; // lignes
                grillePerso = new GridPane();
                grillePerso.setId("grillePerso");
                grillePerso.setGridLinesVisible(true);
                grillePerso.setMaxSize(900, 10000);
                grillePerso.setHgap(2);
                grillePerso.setVgap(2);

                for (File image : selectedDirectory.listFiles(imageFiltre)) {
                    String nomImage = image.getName();
                    String urlImage = image.getAbsolutePath();
                    Image imagePerso = new Image("file:///" + urlImage);
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    listeImages.add(imageViewPerso);
                    imageViewPerso.setFitHeight(175);
                    imageViewPerso.setFitWidth(145);
                    imageViewPerso
                            .setId(nomImage + "*" + x + "*" + y + "*" + urlImage);
                    imageViewPerso.setOnMouseClicked(selectionnerPersonnageEvent);
                    grillePerso.add(imageViewPerso, x, y);
                    compteurImage++;

                    x++;
                    if (x == 6) {
                        x = 0;
                        y++;
                    }
                }

                if (compteurImage >= 2) {
                    nombrePersonnageTotal = compteurImage;
                    nombreImageLabel.setText(nombreImageLabel.getText() + " " + compteurImage + " images)");
                    nombreImageLabel.setVisible(true);
                    cheminVersImage = selectedDirectory.getAbsolutePath();
                    borderPaneId.getChildren().clear();
                    errorText.setOpacity(0);
                    borderPaneId.setCenter(grillePerso);

                    // reactive la partie en dessous
                    explicationText.setOpacity(1);
                    colonnesText.setOpacity(1);
                    ligneText.setOpacity(1);
                    spinnerColonne.setOpacity(1);
                    spinnerLigne.setOpacity(1);
                    spinnerColonne.setDisable(false);
                    spinnerLigne.setDisable(false);

                    // save
                    if (etape < 1) {
                        etape = 1;
                        sauvegarderPartieEnCour();
                    }
                } else {
                    errorText.setOpacity(1);
                }
            }
        }
    }

    EventHandler<ActionEvent> selectionImageEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            colonne = (String) spinnerColonne.getValue().toString();
            ligne = (String) spinnerLigne.getValue().toString();
            nombreImagesNecessaires = Integer.parseInt(ligne) * Integer.parseInt(colonne);
            bottomAnchorPane.getChildren().removeAll(spinnerColonne, spinnerLigne, ligneText, colonnesText,
                    nombreImageLabel);

            explicationText.setText(
                    "Cliquez sur les " + nombreImagesNecessaires + " images ci-dessus que vous souhaitez utiliser.");
            validerButton.setDisable(true);
            validerButton.setText("Passer aux attributs");
            validerButton.setOnAction(validerButtonEvent);

            estImageCliquable = true;
        }
    };

    EventHandler<MouseEvent> selectionnerPersonnageEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (estImageCliquable && nombreImagesSelectionnees < nombreImagesNecessaires) {
                ImageView cibleActuel = (ImageView) mouseEvent.getSource();
                String[] cibleSplit = cibleActuel.getId().split("\\*");

                File checkFile = new File("images/check.png");
                Image checkImage = new Image("file:///" + checkFile.getAbsolutePath());
                ImageView checkVimage = new ImageView(checkImage);
                checkVimage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        ImageView checkActuel = (ImageView) mouseEvent.getSource();
                        listeImageSelectionnees.remove(cibleActuel);

                        grillePerso.getChildren().remove(checkActuel);
                        nombreImagesSelectionnees--;
                        validerButton.setDisable(true);
                    }
                });
                checkVimage.setId(cibleActuel.getId() + "cible");
                checkVimage.setFitHeight(175.);
                checkVimage.setFitWidth(145.);

                listeImageSelectionnees.add(cibleActuel);

                grillePerso.add(checkVimage, Integer.parseInt(cibleSplit[1]), Integer.parseInt(cibleSplit[2]));

                nombreImagesSelectionnees++;
            }
            if (nombreImagesSelectionnees == nombreImagesNecessaires) {
                validerButton.setDisable(false);
            }
        }
    };

    EventHandler<ActionEvent> validerButtonEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (estNombreImagesCustomsSelectionnees) {
                // regenere la grille avec les images selectionnées
                int x = 0; // colonne
                int y = 0; // lignes
                int compteurImage = 0;

                // supprime l'ancienne grille
                borderPaneId.getChildren().remove(grillePerso);

                grillePerso = new GridPane();
                grillePerso.setId("grillePerso");
                grillePerso.setGridLinesVisible(true);
                grillePerso.setMaxSize(900, 10000);
                grillePerso.setHgap(2);
                grillePerso.setVgap(2);

                for (ImageView image : listeImageSelectionnees) {
                    image.setOnMouseClicked(ajouterValeurAttributPersonnageEvent);

                    // on change l'id
                    String[] holdImageIdSplit = image.getId().split("\\*");
                    image.setId(holdImageIdSplit[0] + "*" + x + "*" + y + "*" + holdImageIdSplit[3]);
                    image.setFitHeight(175);
                    image.setFitWidth(145);

                    grillePerso.add(image, x, y);
                    compteurImage++;
                    x++;
                    if (x == Integer.parseInt(colonne)) {
                        x = 0;
                        y++;
                        if (y == Integer.parseInt(ligne)) {
                            break;
                        }
                    }
                }
                nombrePersonnageTotal = compteurImage;
                borderPaneId.setCenter(grillePerso);
            } else {
                colonne = (String) spinnerColonne.getValue().toString();
                ligne = (String) spinnerLigne.getValue().toString();
                bottomAnchorPane.getChildren().removeAll(spinnerColonne, spinnerLigne, ligneText, colonnesText,
                        nombreImageLabel);

                // remet le bon event sur les images
                grillePerso.getChildren()
                        .forEach(image -> image.setOnMouseClicked(ajouterValeurAttributPersonnageEvent));
            }
            // save
            if (etape < 2) {
                etape = 2;
                sauvegarderPartieEnCour();
            }

            grillePerso.setOpacity(0.5);
            explicationText.setText(
                    "Cliquez sur le bouton 'Ajouter des attributs' pour définir les attributs commun de vos personnages :");

            bottomAnchorPane.getChildren().removeAll(spinnerColonne, spinnerLigne, ligneText, colonnesText);
            Button buttonAjoutAttribut = new Button("Ajouter des attributs");
            buttonAjoutAttribut.setId("ajoutAttributButton");
            buttonAjoutAttribut.setOnAction(ajoutAttributEvent);
            buttonAjoutAttribut.setPrefHeight(66);
            buttonAjoutAttribut.setFont(new Font("Calibri", 19));
            pageAttributController.setBtnAttribut(buttonAjoutAttribut);
            pageAttributController.setBtnValider(validerButton);

            AnchorPane.setTopAnchor(buttonAjoutAttribut, 85.);
            AnchorPane.setLeftAnchor(buttonAjoutAttribut, 60.);
            bottomAnchorPane.getChildren().add(buttonAjoutAttribut);

            validerButton.setText("Passer aux valeurs");
            validerButton.setOnAction(ajoutValeurEvent);
            validerButton.setDisable(true);
        }
    };

    EventHandler<ActionEvent> ajoutAttributEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pageAjoutAttribut.fxml"));
            Parent root1;
            try {
                root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Ajouter des attributs");
                File logo = new File("images/iconeGenerateur.png");
                stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
                stage.setScene(new Scene(root1));
                // Quand on ferme la fenetre elle devient réouvrable
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        estOuvertAttribut = false;
                        bottomAnchorPane.getScene().lookup("#ajoutAttributButton").setDisable(false);
                    }
                });
                stage.show();
                estOuvertAttribut = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            // si la fenêtre est ouverte on ne peut la dupliquer
            if (estOuvertAttribut) {
                bottomAnchorPane.getScene().lookup("#ajoutAttributButton").setDisable(true);
            }

        }
    };

    public static void setListAttributString(ArrayList<String> lA) {
        listeAttributsStrings = lA;
        estOuvertAttribut = false;
    }

    public static ArrayList<String> getListAttributString() {
        return listeAttributsStrings;
    }

    public static void setListAttributLabel(ArrayList<Label> lA) {
        listeAttributsLabel = lA;
    }

    public static ArrayList<Label> getListAttributLabel() {
        return listeAttributsLabel;
    }

    public static void setListSupprLabel(ArrayList<Label> lA) {
        listeSupprLabel = lA;
    }

    public static ArrayList<Label> getListSupprLabel() {
        return listeSupprLabel;
    }

    EventHandler<ActionEvent> ajoutValeurEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (etape < 3) {
                etape = 3;
                sauvegarderPartieEnCour();
            }

            estValeursAjoutable = true;
            grillePerso.setOpacity(1);

            explicationText.setText(
                    "Cliquez sur les images des personnages pour définir les valeurs de leurs attributs.");

            bottomAnchorPane.getChildren().remove((Button) bottomAnchorPane.getScene().lookup("#ajoutAttributButton"));

            validerButton.setText("Passer au Json");
            validerButton.setOnAction(passerAuJsonEvent);
            validerButton.setDisable(true);

        }
    };

    EventHandler<MouseEvent> ajouterValeurAttributPersonnageEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (estValeursAjoutable) {
                try {
                    ImageView cibleActuel = (ImageView) mouseEvent.getSource();
                    String[] cibleSplit = cibleActuel.getId().split("\\*");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("pageAjoutValeurs.fxml"));

                    pageAjoutValeursController controller = new pageAjoutValeursController(cibleSplit[0], cibleSplit[3],
                            listeAttributsStrings, cibleSplit[1], cibleSplit[2], validerButton);
                    loader.setController(controller);

                    Parent parent = (Parent) loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Ajouter les valeurs pour l'image " + cibleSplit[0]);
                    File logo = new File("images/iconeGenerateur.png");
                    stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            estValeursAjoutable = true;
                        }
                    });
                    stage.setScene(new Scene(parent));
                    stage.show();

                    estValeursAjoutable = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // getter Pour les valeurs de chaques perso
    public static void addValeursPersonnage(JSONObject e, String x, String y, Button validerButton) {
        listePersonnages.add(e);

        File checkFile = new File("images/check.png");
        Image checkImage = new Image("file:///" + checkFile.getAbsolutePath());
        ImageView checkVimage = new ImageView(checkImage);
        checkVimage.setFitHeight(175.);
        checkVimage.setFitWidth(145.);

        grillePerso.add(checkVimage, Integer.parseInt(x), Integer.parseInt(y));

        nombrePersonnageTermines++;
        estValeursAjoutable = true;

        // save
        etape = 4;
        sauvegarderPartieEnCour();

        if (nombrePersonnageTermines == nombrePersonnageTotal) {
            estValeursAjoutable = false;
            validerButton.setDisable(false);
        }
    }

    private boolean verification() {
        List<String> attributs = new ArrayList<String>(listePersonnages.get(0).keySet());
        for (JSONObject personnage : listePersonnages) {
            for (String attribut : attributs) {
                if (personnage.get(attribut) == null) {
                    return false;
                }
            }
            String p = (String) personnage.get("prenom");
            for (JSONObject personnage2 : listePersonnages) {
                if (!personnage.equals(personnage2)) {
                    if (p.equals(personnage2.get("prenom"))) {
                        for (String attribut : attributs) {
                            if (personnage.get(attribut).equals(personnage2.get(attribut))) {
                                return false;
                            }
                        }

                    }

                }
            }
        }
        return true;
    }

    EventHandler<ActionEvent> passerAuJsonEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            estValeursAjoutable = false;

            if (etape < 5) {
                etape = 5;
                sauvegarderPartieEnCour();
            }

            grillePerso.setOpacity(0.5);
            explicationText.setText(
                    "Donnez un nom pour votre fichier Json et selectionnez son dossier de destination :");

            // on ajoute le bouton et le textField
            Label textLabel = new Label("Nom du Json : ");
            textLabel.setId("nomJsonLabel");
            textLabel.setFont(new Font("Ebrima", 23.0));
            AnchorPane.setTopAnchor(textLabel, 73.);
            AnchorPane.setLeftAnchor(textLabel, 60.);

            TextField textField = new TextField();
            textField.setId("nomJsonField");
            textField.setFont(new Font("Calibri", 21));
            textField.setPrefWidth(200);
            textField.setPrefHeight(40);
            textField.setFont(new Font("Ebrima", 21.0));
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
                        String newPropertyValue) {
                    if (!((Label) bottomAnchorPane.getScene().lookup("#destinationJsonLabel")).getText().equals(""))
                        validerButton.setDisable(false);
                }
            });
            AnchorPane.setTopAnchor(textField, 68.);
            AnchorPane.setLeftAnchor(textField, 220.);

            Button buttonChoixDestionation = new Button("Choisir destination Json");
            buttonChoixDestionation.setId("choixDestinationButton");
            buttonChoixDestionation.setOnAction(choixDestinationJsonEvent);
            buttonChoixDestionation.setPrefHeight(40);
            buttonChoixDestionation.setPrefWidth(300);
            buttonChoixDestionation.setFont(new Font("Ebrima", 23.0));
            AnchorPane.setTopAnchor(buttonChoixDestionation, 138.);
            AnchorPane.setLeftAnchor(buttonChoixDestionation, 58.);

            Label buttonLabel = new Label();
            buttonLabel.setId("destinationJsonLabel");
            buttonLabel.setFont(new Font("Ebrima", 13.0));
            buttonLabel.setMaxWidth(260);
            buttonLabel.setWrapText(true);
            AnchorPane.setTopAnchor(buttonLabel, 143.);
            AnchorPane.setLeftAnchor(buttonLabel, 375.);

            bottomAnchorPane.getChildren().addAll(buttonChoixDestionation, textLabel, textField, buttonLabel);

            validerButton.setText("Generer le Json");
            validerButton.setOnAction(genererJsonEvent);
            validerButton.setDisable(true);
        }
    };

    EventHandler<ActionEvent> choixDestinationJsonEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(MainAnchorPane.getScene().getWindow());
            ((Label) bottomAnchorPane.getScene().lookup("#destinationJsonLabel"))
                    .setText(selectedDirectory.getAbsolutePath());
            cheminJson = selectedDirectory.getAbsolutePath();

            if (!((TextField) bottomAnchorPane.getScene().lookup("#nomJsonField")).getText().equals(""))
                validerButton.setDisable(false);
        }
    };

    EventHandler<ActionEvent> genererJsonEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            // supprime tous les éléments
            Label textLabel = (Label) bottomAnchorPane.getScene().lookup("#nomJsonLabel");
            TextField textField = (TextField) bottomAnchorPane.getScene().lookup("#nomJsonField");
            Button buttonChoixDestionation = (Button) bottomAnchorPane.getScene()
                    .lookup("#choixDestinationButton");

            Label buttonLabel = (Label) bottomAnchorPane.getScene().lookup("#destinationJsonLabel");
            String nomJson = textField.getText();

            bottomAnchorPane.getChildren().removeAll(textLabel, textField, buttonChoixDestionation, buttonLabel,
                    validerButton);

            AnchorPane.setTopAnchor(explicationText, 50.);

            Button fermerFenetre = new Button("Fermer le générateur");
            fermerFenetre.setPrefHeight(40);
            fermerFenetre.setPrefWidth(300);
            fermerFenetre.setFont(new Font("Ebrima", 20.0));
            fermerFenetre.setOnAction(fermerGenerateurEvent);

            AnchorPane.setTopAnchor(fermerFenetre, 100.);
            AnchorPane.setLeftAnchor(fermerFenetre, 300.);

            bottomAnchorPane.getChildren().add(fermerFenetre);

            if (verification() && !nomJson.equals("")) {
                // sauvegarde de la partie
                JSONObject jsonFinal = new JSONObject();
                jsonFinal.put("images", String.valueOf(cheminVersImage));
                jsonFinal.put("ligne", String.valueOf(ligne));
                jsonFinal.put("colonne", String.valueOf(colonne));

                JSONObject listePerso = new JSONObject();
                int i = 0;
                for (JSONObject personnage : listePersonnages) {
                    listePerso.put(String.valueOf(i), personnage);
                    i++;

                }
                jsonFinal.put("personnages", listePerso);

                try (FileWriter file = new FileWriter(new File(cheminJson + "/" + nomJson + ".json"))) {
                    file.write(jsonFinal.toJSONString());

                    explicationText.setText(
                            "Votre Json a été correctement généré !");
                    explicationText.setFill(Color.GREEN);

                } catch (IOException e) {
                    e.printStackTrace();
                    explicationText.setText(
                            "Un problème est survenue durant la génération du Json...");
                    explicationText.setFill(Color.RED);
                }
            } else {
                explicationText.setText(
                        "Un problème est survenue durant la génération du Json...");
                explicationText.setFill(Color.RED);

            }
        }
    };

    @SuppressWarnings("unchecked")
    public static void sauvegarderPartieEnCour() {
        // sauvegarde du generateur
        JSONObject generateurSave = new JSONObject();
        if (etape >= 1) {
            generateurSave.put("cheminVersImage", String.valueOf(cheminVersImage));
            generateurSave.put("etape", 1);
        }
        if (etape >= 2) {
            generateurSave.put("ligne", String.valueOf(ligne));
            generateurSave.put("colonne", String.valueOf(colonne));

            int i = 0;
            JSONObject JlisteImageSelectionnees = new JSONObject();
            if (listeImageSelectionnees.isEmpty()) {
                grillePerso.getChildren()
                        .forEach(image -> {
                            if (image instanceof ImageView) {
                                listeImageSelectionnees.add((ImageView) image);
                            }
                        });
            }
            for (ImageView imagesSelectionnes : listeImageSelectionnees) {
                JSONObject detailsImage = new JSONObject();
                detailsImage.put("url", imagesSelectionnes.getImage().getUrl());
                detailsImage.put("id", imagesSelectionnes.getId());
                JlisteImageSelectionnees.put(String.valueOf(i), detailsImage);
                i++;
            }
            generateurSave.put("listeImageSelectionnees", JlisteImageSelectionnees);
            generateurSave.put("etape", 2);
        }
        if (etape >= 3) {
            int i = 0;
            JSONObject JlisteAttributsStrings = new JSONObject();
            for (String attribut : listeAttributsStrings) {
                JlisteAttributsStrings.put(String.valueOf(i), attribut);
                i++;
            }
            generateurSave.put("listeAttributsStrings", listeAttributsStrings);
            generateurSave.put("etape", 3);
        }
        if (etape >= 4) {
            int i = 0;
            JSONObject JlistePersonnages = new JSONObject();
            for (JSONObject attribut : listePersonnages) {
                JlistePersonnages.put(String.valueOf(i), attribut);
                i++;
            }
            generateurSave.put("listePersonnages", JlistePersonnages);
            generateurSave.put("etape", 4);
        }
        if (etape >= 5) {
            generateurSave.put("etape", 5);
        }

        try (FileWriter file = new FileWriter(new File("bin/save.json"))) {
            file.write(generateurSave.toJSONString());
            System.out.println("saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    EventHandler<ActionEvent> fermerGenerateurEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            File fileSave = new File("bin/save.json");
            fileSave.delete();
            ((Stage) bottomAnchorPane.getScene().getWindow()).close();
            System.exit(0);
        }
    };
}
