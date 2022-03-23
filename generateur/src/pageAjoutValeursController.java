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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

public class pageAjoutValeursController implements Initializable {

    private static String nomImage;
    private static String urlImage;
    private static String xGrille;
    private static String yGrille;
    private static GridPane grillePersoCheck;
    private static Button boutonValider;
    private static ArrayList<String> listeAttributs;
    private static ArrayList<HashMap<String, String>> listePersonnage = new ArrayList<>();
    private boolean labelAlreadyDisplayed = false;

    public pageAjoutValeursController(String nomImageController, String urlImageController,
            ArrayList<String> listeAttributsString, String x, String y, GridPane grillePerso, Button validerButton) {
        nomImage = nomImageController;
        urlImage = urlImageController;
        listeAttributs = listeAttributsString;
        xGrille = x;
        yGrille = y;
        grillePersoCheck = grillePerso;
        boutonValider = validerButton;
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

            if (!(i == listeAttributs.size()))
                textField.focusedProperty().addListener(verifierTextListener);
            else {
                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
                            String newPropertyValue) {
                        verifierAttributsTousRemplis();
                    }
                });
            }

            informationsPaneId.getChildren().addAll(label, textField);

            informationsPaneId.setPrefHeight(informationsPaneId.getPrefHeight() + 60);
            if ((textLabel.getBoundsInLocal().getWidth() + 22 + 235) > informationsPaneId.getPrefWidth())
                informationsPaneId.setPrefWidth(textLabel.getBoundsInLocal().getWidth() + 22 + 235 + 10);

            i++;
        }
    }

    private void verifierAttributsTousRemplis() {
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

    // verifie qu'il y est bien des valeurs dans toutes les cases
    ChangeListener<Boolean> verifierTextListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                Boolean newPropertyValue) {
            if (!newPropertyValue) {
                verifierAttributsTousRemplis();
            }
        }
    };

    @FXML
    void validerButtonEvent(ActionEvent event) {
        // creer le JSONobject avec toutes les valeurs pour chaque attribut + etat + nom
        // image
        Scene scene = informationsPaneId.getScene();
        HashMap<String, String> personnageMap = new HashMap<>();
        personnageMap.put("image", String.valueOf(nomImage));
        personnageMap.put("etat", "vivant");
        personnageMap.put("prenom", String.valueOf(((TextField) scene.lookup("#field0")).getText()));

        int i = 1;
        for (String attribut : listeAttributs) {
            personnageMap.put(String.valueOf(attribut.toLowerCase()),
                    String.valueOf(((TextField) scene.lookup("#field" + i)).getText()).toLowerCase());
            i++;
        }
        // passe personnage a la page principale et ferme la feunetre:
        JSONObject personnage = new JSONObject(personnageMap);
        // vérification de la présence ou nom de cet identifiant
        boolean estDejaPresent = false;

        if (!(listePersonnage.isEmpty())) {
            for (HashMap<String, String> persoMap : listePersonnage) {
                if (persoMap.get("prenom").equals(personnageMap.get("prenom"))) {
                    estDejaPresent = true;
                    if (!labelAlreadyDisplayed) {
                        Label erreur = new Label("Identifiant deja présent, impossible de valider");
                        erreur.setFont(new Font("Reem Kufi Regular", 18));
                        erreur.setId("errorLabel");
                        anchorPaneId.getChildren().add(erreur);
                        labelAlreadyDisplayed = true;
                        erreur.setLayoutX(250);
                        erreur.setLayoutY(410);
                        erreur.setTextFill(Color.RED);
                    }
                }
            }
        }
        if (!estDejaPresent) {
            listePersonnage.add(personnageMap);
            pageGenerateurController.addValeursPersonnage(personnage, xGrille, yGrille, grillePersoCheck,
                    boutonValider);
            ((Stage) anchorPaneId.getScene().getWindow()).close();
        }
    }

}
