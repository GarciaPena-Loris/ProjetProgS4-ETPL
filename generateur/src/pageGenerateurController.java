import java.io.File;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class pageGenerateurController {

    private String cheminVersImage;
    private String ligne;
    private String colonne;
    private ArrayList<String> listePersonnages;

    @FXML
    private AnchorPane MainAnchorPane;

    @FXML
    private AnchorPane bottomAnchorPane;

    @FXML
    private Text colonnesText;

    @FXML
    private Text explicationText;

    @FXML
    private Text ligneText;

    @FXML
    private AnchorPane middleAnchorPane;

    @FXML
    private ScrollBar scrollBar;

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
        File logoFile = new File("images/logoGenerateur.png");
        Image logoImage = new Image("file:///" + logoFile.getAbsolutePath());
        ImageView logoVimage = new ImageView(logoImage);
        logoVimage.setFitHeight(137.);
        logoVimage.setFitWidth(904.);
        topAnchorPane.getChildren().add(logoVimage);
    }
    
    @FXML
    void choixImageEvent(ActionEvent event) {

    }

    @FXML
    void spinnerEvent(ActionEvent event) {

    }

    @FXML
    void validerButtonEvent(ActionEvent event) {

    }

}
