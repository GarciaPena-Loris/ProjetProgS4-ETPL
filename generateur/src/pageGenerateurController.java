import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;

public class pageGenerateurController {

    private String cheminVersImage;
    private String ligne;
    private String colonne;
    private ArrayList<String> listePersonnages = new ArrayList<>();;
    private ArrayList<Image> listeImages = new ArrayList<>();

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
        MainAnchorPane.setMaxHeight(Screen.getPrimary().getBounds().getHeight()-80);
        MainAnchorPane.setMinHeight(Screen.getPrimary().getBounds().getHeight()-80);
        MainAnchorPane.setPrefHeight(Screen.getPrimary().getBounds().getHeight()-80);
        File logoFile = new File("images/logoGenerateur.png");
        Image logoImage = new Image("file:///" + logoFile.getAbsolutePath());
        ImageView logoVimage = new ImageView(logoImage);
        logoVimage.setFitHeight(137.);
        logoVimage.setFitWidth(904.);
        topAnchorPane.getChildren().add(logoVimage);
        System.out.println("Hauteur :"+Screen.getPrimary().getBounds().getHeight()+"/ Largeur :"+Screen.getPrimary().getBounds().getWidth());
        
    }

    @FXML
    void choixImageEvent(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(MainAnchorPane.getScene().getWindow());

        if (selectedDirectory != null) {
            int compteurImage = 0;
            System.out.println(selectedDirectory.getAbsolutePath());

            if (selectedDirectory.isDirectory()) {
                int x = 0; //colonne
                int y = 0; //lignes 
                zoneImageId.setHbarPolicy(ScrollBarPolicy.NEVER);
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
                            .setId(nomImage + "_" + x + "_" + y);
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
                    errorText.setOpacity(1);
                    borderPaneId.setCenter(grillePerso);
                    
                    //reactive la partie en dessous
                    explicationText.setOpacity(1);
                    colonnesText.setOpacity(1);
                    ligneText.setOpacity(1);
                    spinnerColonne.setOpacity(1);
                    spinnerLigne.setOpacity(1);
                    spinnerColonne.setDisable(false);
                    spinnerLigne.setDisable(false);
                }
                else {
                    errorText.setOpacity(1);
                }
            }
        }
    }

    @FXML
    void spinnerColonneEvent(ActionEvent event) {

    }

    @FXML
    void spinnerLigneEvent(ActionEvent event) {

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

}
