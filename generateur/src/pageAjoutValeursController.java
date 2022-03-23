import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class pageAjoutValeursController implements Initializable {

    private static String nomImage;
    private static String urlImage;
    private static ArrayList<String> listeAttributs;
    private HashMap<String, String> listePersonnages;

    public pageAjoutValeursController(String nomImageController, String urlImageController, ArrayList<String> listeAttributsString) {
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
    private TextField prenomField;

    @FXML
    private Pane informationsPaneId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageName.setText(nomImage);
        File urlPersonnage = new File(urlImage);
        Image imagePersonnage = new Image("file:///" + urlPersonnage.getAbsolutePath());
        System.out.println(imagePersonnage.getUrl());
        ImageView vimagePersonnage = new ImageView(imagePersonnage);
        vimagePersonnage.setFitHeight(241.);
        vimagePersonnage.setFitWidth(200.);
        imagePane.getChildren().add(vimagePersonnage);

        int i = 1;
        //pour chaque attributs, créer un text field 
        for (String attribut : listeAttributs) {
            Label label = new Label(attribut + " : ");
            label.setId("label" + i);
            label.setFont(new Font("Reem Kufi Regular", 21));
            label.setLayoutX(14);
            label.setLayoutY(67 + (60 * i));

            TextField textField = new TextField();
            textField.setId("field" + i);
            textField.setFont(new Font("Reem Kufi Regular", 21));
            textField.setLayoutX(new Text(label.getText()).getBoundsInLocal().getWidth() + 50);
            textField.setLayoutY(67 + (60 * i));

            informationsPaneId.getChildren().addAll(label, textField);
            informationsPaneId.setPrefHeight(informationsPaneId.getPrefHeight() + 60);

            i++;
        }
    }

    //obligatoirement prenom

}
