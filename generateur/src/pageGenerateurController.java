
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class pageGenerateurController {

    private String cheminVersImage;
    private String ligne;
    private String colonne;

    private String cheminJson;
    private String nomJson;

    private static boolean estValeursAjoutable = false;
    private static Boolean estOuvertAttribut = false;
    private static int nombrePersonnageTotal = 0;
    private static int nombrePersonnageTermines = 0;
    private ArrayList<Image> listeImages = new ArrayList<>();
    private static ArrayList<JSONObject> listePersonnages = new ArrayList<>();;
    private static ArrayList<String> listeAttributsStrings = new ArrayList<>();
    private static ArrayList<Label> listeAttributsLabel = new ArrayList<>();
    private static ArrayList<Label> listeSupprLabel = new ArrayList<>();

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
    protected void initialize() {
        MainAnchorPane.setMaxHeight(Screen.getPrimary().getBounds().getHeight() - 80);
        MainAnchorPane.setMinHeight(Screen.getPrimary().getBounds().getHeight() - 80);
        MainAnchorPane.setPrefHeight(Screen.getPrimary().getBounds().getHeight() - 80);
        File logoFile = new File("images/logoGenerateur.png");
        Image logoImage = new Image("file:///" + logoFile.getAbsolutePath());
        ImageView logoVimage = new ImageView(logoImage);
        logoVimage.setFitHeight(137.);
        logoVimage.setFitWidth(904.);
        topAnchorPane.getChildren().add(logoVimage);
        borderPaneId.setPrefHeight(Screen.getPrimary().getBounds().getHeight() - 500);
        spinnerColonne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Integer.parseInt(newValue)
                    * Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) >= listeImages.size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
        });
        spinnerLigne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Integer.parseInt(newValue)
                    * Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) >= listeImages.size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
        });


        //temporaire
        Button buttonChoixDestionation = new Button("Choisir destination Json");
        buttonChoixDestionation.setOnAction(passerAuJsonEvent);
        AnchorPane.setTopAnchor(buttonChoixDestionation, 65.);
        AnchorPane.setLeftAnchor(buttonChoixDestionation, 60.);
        MainAnchorPane.getChildren().add(buttonChoixDestionation);
    }

    @FXML
    void choixImageEvent(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(MainAnchorPane.getScene().getWindow());

        if (selectedDirectory != null) {
            int compteurImage = 0;

            if (selectedDirectory.isDirectory()) {
                int x = 0; // colonne
                int y = 0; // lignes
                GridPane grillePerso = new GridPane();
                grillePerso.setId("grillePerso");
                grillePerso.setGridLinesVisible(true);
                grillePerso.setMaxSize(900, 10000);
                grillePerso.setHgap(2);
                grillePerso.setVgap(2);

                for (File image : selectedDirectory.listFiles(imageFiltre)) {
                    String nomImage = image.getName();
                    String urlImage = image.getAbsolutePath();
                    Image imagePerso = new Image("file:///" + urlImage);
                    listeImages.add(imagePerso);
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    imageViewPerso.setFitHeight(175);
                    imageViewPerso.setFitWidth(145);
                    imageViewPerso
                            .setId(nomImage + "*" + x + "*" + y + "*" + urlImage);
                    imageViewPerso.setOnMouseClicked(ajouterValeurAttributPersonnage);
                    grillePerso.add(imageViewPerso, x, y);
                    compteurImage++;

                    x++;
                    if (x == 6) {
                        x = 0;
                        y++;
                    }
                }

                if (compteurImage >= 3) {
                    nombrePersonnageTotal = compteurImage;
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
                } else {
                    errorText.setOpacity(1);
                }
            }
        }
    }

    @FXML
    void validerButtonEvent(ActionEvent event) {
        colonne = (String) spinnerColonne.getValue().toString();
        ligne = (String) spinnerLigne.getValue().toString();
        ((GridPane) middleAnchorPane.getScene().lookup("#grillePerso")).setOpacity(0.5);
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

    EventHandler<MouseEvent> ajouterValeurAttributPersonnage = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (estValeursAjoutable) {
                try {
                    ImageView cibleActuel = (ImageView) mouseEvent.getSource();
                    String[] cibleSplit = cibleActuel.getId().split("\\*");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("pageAjoutValeurs.fxml"));

                    pageAjoutValeursController controller = new pageAjoutValeursController(cibleSplit[0], cibleSplit[3],
                            listeAttributsStrings, cibleSplit[1], cibleSplit[2],
                            (GridPane) middleAnchorPane.getScene().lookup("#grillePerso"), validerButton);
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

    EventHandler<ActionEvent> ajoutValeurEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            estValeursAjoutable = true;
            ((GridPane) middleAnchorPane.getScene().lookup("#grillePerso")).setOpacity(1);

            explicationText.setText(
                    "Cliquez sur les images des personnages pour définir les valeurs de leurs attributs.");

            bottomAnchorPane.getChildren().remove((Button) bottomAnchorPane.getScene().lookup("#ajoutAttributButton"));

            validerButton.setText("Passer au Json");
            validerButton.setOnAction(passerAuJsonEvent);
            validerButton.setDisable(true);

        }
    };

    // getter Pour les valeurs de chaques perso
    public static void addValeursPersonnage(JSONObject e, String x, String y, GridPane grillePerso,
            Button validerButton) {
        listePersonnages.add(e);

        File checkFile = new File("images/check.png");
        Image checkImage = new Image("file:///" + checkFile.getAbsolutePath());
        ImageView checkVimage = new ImageView(checkImage);
        checkVimage.setFitHeight(175.);
        checkVimage.setFitWidth(145.);

        grillePerso.add(checkVimage, Integer.parseInt(x), Integer.parseInt(y));

        nombrePersonnageTermines++;
        estValeursAjoutable = true;
        if (nombrePersonnageTermines == nombrePersonnageTotal) {
            estValeursAjoutable = false;
            validerButton.setDisable(false);
        }
    }

    boolean verification() {
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
            AnchorPane.setTopAnchor(validerButton, 65.);

        bottomAnchorPane.getChildren().removeAll(spinnerColonne, spinnerLigne, ligneText, colonnesText);

            ((GridPane) MainAnchorPane.getScene().lookup("#grillePerso")).setOpacity(0.5);
            explicationText.setText(
                    "Donnez un nom pour votre fichier Json et selectionnez son dossier de destination :");

            // on ajoute le bouton et le textField
            Label textLabel = new Label("Nom du Json : ");
            textLabel.setId("nomJsonLabel");
            textLabel.setFont(new Font("Ebrima", 23.0));
            AnchorPane.setTopAnchor(textLabel, 70.);
            AnchorPane.setLeftAnchor(textLabel, 60.);
            
            TextField textField = new TextField();
            textField.setId("nomJsonField");
            textField.setFont(new Font("Calibri", 21));
            textField.setPrefWidth(200);
            textField.setPrefHeight(40);
            textField.setFont(new Font("Ebrima", 23.0));
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
                        String newPropertyValue) {
                }
            });
            AnchorPane.setTopAnchor(textField, 65.);
            AnchorPane.setLeftAnchor(textField, 220.);
            
            Button buttonChoixDestionation = new Button("Choisir destination Json");
            buttonChoixDestionation.setId("choixDestinationButton");
            buttonChoixDestionation.setOnAction(choixDestinationJsonEvent);
            buttonChoixDestionation.setPrefHeight(40);
            buttonChoixDestionation.setPrefWidth(300);
            buttonChoixDestionation.setFont(new Font("Ebrima", 23.0));
            AnchorPane.setTopAnchor(buttonChoixDestionation, 135.);
            AnchorPane.setLeftAnchor(buttonChoixDestionation, 58.);

            Label buttonLabel = new Label();
            buttonLabel.setId("destinationJsonLabel");
            buttonLabel.setFont(new Font("Ebrima", 18.0));
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
            ((Label) bottomAnchorPane.getScene().lookup("#destinationJsonLabel")).setText(selectedDirectory.getName());
            cheminJson = selectedDirectory.getAbsolutePath(); 
        }
    };

    EventHandler<ActionEvent> genererJsonEvent = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        }
    };
}
