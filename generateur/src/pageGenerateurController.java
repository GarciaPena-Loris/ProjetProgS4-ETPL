import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;

import org.json.simple.JSONObject;

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
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class pageGenerateurController {

    private String cheminVersImage;
    private String ligne;
    private String colonne;
    private ArrayList<JSONObject> listePersonnages = new ArrayList<>();;
    private ArrayList<Image> listeImages = new ArrayList<>();
    private ArrayList<String> listeAttributsString = new ArrayList<>();

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
        borderPaneId.setPrefHeight(zoneImageId.getPrefHeight() - 5);

        spinnerColonne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Integer.parseInt(newValue) * Integer.parseInt(spinnerLigne.getEditor().textProperty().getValue()) >= listeImages.size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
        });
        spinnerLigne.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Integer.parseInt(newValue) * Integer.parseInt(spinnerColonne.getEditor().textProperty().getValue()) >= listeImages.size()) {
                validerButton.setDisable(false);
            } else {
                validerButton.setDisable(true);
            }
        });
    }

    @FXML
    void choixImageEvent(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(MainAnchorPane.getScene().getWindow());

        if (selectedDirectory != null) {
            int compteurImage = 0;
            System.out.println(selectedDirectory.getAbsolutePath());

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
                    imageViewPerso.setFitHeight(174);
                    imageViewPerso.setFitWidth(144);
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

                if (compteurImage >= 6) {
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
    void spinnerColonneEvent(ActionEvent event) {
        System.out.println(event);
    }

    @FXML
    void spinnerLigneEvent(ActionEvent event) {
        System.out.println(event);
    }

    @FXML
    void validerButtonEvent(ActionEvent event) {
        bottomAnchorPane.getChildren().removeAll(spinnerColonne, spinnerLigne, ligneText, colonnesText);
        Button buttonAjoutAttribut = new Button("Ajout Attribut");
        buttonAjoutAttribut.setId("AjoutAttribut");
        // buttonAjoutAttribut.setOnAction(AjoutAttributEvent);

        AnchorPane.setBottomAnchor(buttonAjoutAttribut, 20.);
        AnchorPane.setRightAnchor(buttonAjoutAttribut, 85.);
        bottomAnchorPane.getChildren().add(buttonAjoutAttribut);
    }

    EventHandler<MouseEvent> ajouterValeurAttributPersonnage = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            ImageView cibleActuel = (ImageView) mouseEvent.getSource();
            String[] cibleSplit = cibleActuel.getId().split("\\*");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("pageAjoutValeurs.fxml"));

                //temporaire
                listeAttributsString.add("Couleur cheveux");
                listeAttributsString.add("lunette");
                listeAttributsString.add("cheveux");
                listeAttributsString.add("sexe");
                listeAttributsString.add("age");
                listeAttributsString.add("Nombre de dents");
                listeAttributsString.add("Chapeau");
                listeAttributsString.add("Moustache ? / Barbes ? / poils du nez qui depasse ?");
                pageAjoutValeursController controller = new pageAjoutValeursController(cibleSplit[0], cibleSplit[3], listeAttributsString);
                loader.setController(controller);

                Parent parent = (Parent) loader.load();
                Stage stage = new Stage();
                stage.setTitle("Ajouter les valeurs pour l'image " + cibleSplit[0]);
                File logo = new File("images/iconeGenerateur.png");
                stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));

                stage.setScene(new Scene(parent));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    };

}
