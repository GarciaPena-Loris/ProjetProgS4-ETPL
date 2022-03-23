import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

public class pageAjoutValeursController implements Initializable {

    private static String nomImage;
    private static String urlImage;
    private static ArrayList<String> listeAttributs;

    public pageAjoutValeursController(String nomImageController, String urlImageController,
            ArrayList<String> listeAttributsString) {
        nomImage = nomImageController;
        urlImage = urlImageController;
        listeAttributs = listeAttributsString;
    }

    @FXML
    private AnchorPane anchorPaneId;

    @FXML
    private Label imageName;

    @FXML
    private AnchorPane imagePane;

    @FXML
    private ScrollPane scrollPaneId;

    @FXML
    private TextField field0;

    @FXML
    private Pane informationsPaneId;

    @FXML
    private Button validerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageName.setText(nomImage);
        File urlPersonnage = new File(urlImage);
        Image imagePersonnage = new Image("file:///" + urlPersonnage.getAbsolutePath());
        ImageView vimagePersonnage = new ImageView(imagePersonnage);
        vimagePersonnage.setFitHeight(241.);
        vimagePersonnage.setFitWidth(200.);
        imagePane.getChildren().add(vimagePersonnage);
        field0.focusedProperty().addListener(verifierTextListener);

        int i = 1;
        // pour chaque attributs, créer un text field et un label
        for (String attribut : listeAttributs) {
            Label label = new Label(attribut + " : ");
            label.setId("label" + i);
            label.setFont(new Font("Reem Kufi Regular", 21));
            label.setLayoutX(14);
            label.setLayoutY(70 + (60 * i));

            TextField textField = new TextField();
            textField.setId("field" + i);
            textField.setFont(new Font("Reem Kufi Regular", 21));
            Text textLabel = new Text(label.getText());
            textLabel.setFont(new Font("Reem Kufi Regular", 21));
            textField.setPrefWidth(235);
            textField.setLayoutX(textLabel.getBoundsInLocal().getWidth() + 22);
            textField.setLayoutY(67 + (60 * i));

            textField.focusedProperty().addListener(verifierTextListener);

            informationsPaneId.getChildren().addAll(label, textField);

            informationsPaneId.setPrefHeight(informationsPaneId.getPrefHeight() + 60);
            if ((textLabel.getBoundsInLocal().getWidth() + 22 + 235) > informationsPaneId.getPrefWidth())
                informationsPaneId.setPrefWidth(textLabel.getBoundsInLocal().getWidth() + 22 + 235 + 10);

            i++;
        }
    }

    // verifie qu'il y est bien des valeurs dans toutes les cases
    ChangeListener<Boolean> verifierTextListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                Boolean newPropertyValue) {
            if (!newPropertyValue) {
                boolean estComplet = true;
                for (int i = 0; i <= listeAttributs.size(); i++) {
                    TextField texteField = (TextField) informationsPaneId.getScene().lookup("#field" + i);
                    if (texteField.getText().equals("")) {
                        estComplet = false;
                        break;
                    }
                }
                if (estComplet)
                    validerButton.setDisable(false);
                else
                    validerButton.setDisable(true);
            }
        }
    };

    @FXML
    void validerButtonEvent(ActionEvent event) {
        //creer le JSONobject avec toutes les valeurs pour chaque attribut + etat + nom image
        Scene scene = informationsPaneId.getScene();
        HashMap<String, String> personnageMap = new HashMap<>();
        personnageMap.put("image", String.valueOf(nomImage));
        personnageMap.put("etat", "vivant");
        personnageMap.put("prenom", String.valueOf(((TextField) scene.lookup("#field0")).getText()));

        int i = 1;
        for (String attribut : listeAttributs) {
            personnageMap.put(String.valueOf(attribut.toLowerCase()), String.valueOf(((TextField) scene.lookup("#field" + i)).getText()).toLowerCase());
            i++;
        }
        JSONObject personnage = new JSONObject(personnageMap);

        //passe personnage a la page principale et ferme la feunetre:
    }

}
