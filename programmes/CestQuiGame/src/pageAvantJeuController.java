import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    private boolean isSaveJsonFile(){
        File dir = new File("./CestQuiGame/bin");
        File[] matches = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                return name.startsWith("save");
            }
        });
        return matches.length!=0;
    }

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
    protected void initialize(){
        if(!isSaveJsonFile()){buttonchargerpartie.setDisable(true);}
    }

    @FXML
    void choixdifficulte(ActionEvent event) {
        difficulte = ((MenuItem) event.getSource()).getText();
        estNouvellePartiePossible();
        difficulteName.setText(difficulte);
    }

    @FXML
    void choixjson(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);
        jsonName = selectedFile.getAbsolutePath();
        String jsonNom = selectedFile.getName();
        estNouvellePartiePossible();
        jsonNameLabel.setText(jsonNom);
    }

    @FXML
    void nouvellepartie(ActionEvent event) throws IOException {
        MainSceneController.setDifficulte(difficulteName.getText());
        MainSceneController.setJson(jsonNameLabel.getText());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("QuiEstCe?");
        stage.setScene(new Scene(root1));
        stage.show();
        ((Stage) jsonNameLabel.getScene().getWindow()).close();
    }

    @FXML
    void chargerpartie(ActionEvent event) {
        try {
            MainSceneController.setJson("./CestQuiGame/bin/save.json");
            JSONObject js;
            js = (JSONObject) new JSONParser().parse(new FileReader("./CestQuiGame/bin/save.json"));
            MainSceneController.setDifficulte((String) js.get("difficulte"));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("QuiEstCe?");
            stage.setScene(new Scene(root1));
            stage.show();
            ((Stage) jsonNameLabel.getScene().getWindow()).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void estNouvellePartiePossible() {
        if (difficulte != null && jsonName != null)
            buttonnouvellepartie.setDisable(false);

    }

}