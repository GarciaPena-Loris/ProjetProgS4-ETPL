import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class pageAvantJeuController {
    private static String difficulte;
    private static String jsonName;
    
    @FXML
    private Label jsonNameLabel;
    @FXML
    private Label difficulteName;

    @FXML
    private Button buttonchargerpartie;

    @FXML
    private MenuButton buttonchoixdifficulte;

    @FXML
    private Button buttonchoixjson;

    @FXML
    private Button buttonnouvellepartie;

    @FXML
    void chargerpartie(ActionEvent event) {

    }

    @FXML
    void choixdifficulte(ActionEvent event) {
        difficulte=((MenuItem)event.getSource()).getText();
        estNouvellePartiePossible();
        difficulteName.setText(difficulte);
    }

    @FXML
    void choixjson(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);
        jsonName = selectedFile.getName(); 
        estNouvellePartiePossible();
        jsonNameLabel.setText(jsonName);
    }

    @FXML
    void nouvellepartie(ActionEvent event) throws IOException {
            MainSceneController.setDifficulte(difficulteName.getText());
            MainSceneController.setJson(jsonNameLabel.getText());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            //MainSceneController controller = fxmlLoader.getController();
            //controller.setDifficulte(difficulte);
            //controller.setJson(jsonName);
            Stage stage = new Stage();
            stage.setTitle("QuiEstCe?");
            stage.setScene(new Scene(root1));  
            stage.show();
            ((Stage)jsonNameLabel.getScene().getWindow()).close();
    }   

    private void estNouvellePartiePossible(){
        if (difficulte!=null && jsonName!=null ) buttonnouvellepartie.setDisable(false);
          
    }


}