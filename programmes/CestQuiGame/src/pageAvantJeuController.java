import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class pageAvantJeuController {
    private static String difficulte;
    private static String jsonPath;

    private boolean isSaveJsonFile() {
        File dir = new File("CestQuiGame/bin");
        File[] matches = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("save");
            }
        });
        return matches.length != 0;
    }

    @FXML
    private AnchorPane anchorPaneButtonId;

    @FXML
    private AnchorPane conteneurImage;

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
    private Button multiButton;

    @FXML
    protected void initialize() throws URISyntaxException {
        File fimage = new File("images/quiestcelogo.png");
        Image image = new Image("file:///" + fimage.getAbsolutePath());
        ImageView vimage = new ImageView(image);
        vimage.setFitHeight(292.);
        vimage.setFitWidth(598.);
        conteneurImage.getChildren().add(vimage);

        if (!isSaveJsonFile()) {
            buttonchargerpartie.setDisable(true);
        }
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            jsonPath = selectedFile.getAbsolutePath();
            String jsonNom = selectedFile.getName();
            estNouvellePartiePossible();
            jsonNameLabel.setText(jsonNom);
        }
    }

    @FXML
    void nouvellepartie(ActionEvent event) throws IOException {
        MainSceneController.setDifficulte(difficulteName.getText());
        MainSceneController.setJson(jsonPath);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        fxmlLoader.setController(new MainSceneController());
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("QuiEstCe?");
        if (difficulte.equals("Facile")) {
            stage.setTitle("QuiEstCe? - Mode Facile");
        }
        File logo = new File("images/logoQuiEstCe.png");
        stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
        stage.setScene(new Scene(root1));
        stage.show();
        ((Stage) jsonNameLabel.getScene().getWindow()).close();

    }

    @FXML
    void chargerpartie(ActionEvent event) {
        try {
            File file = new File("CestQuiGame/bin/save.json");
            MainSceneController.setJson(file.getAbsolutePath());
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader(file.getAbsolutePath()));
            MainSceneController.setDifficulte((String) js.get("difficulte"));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            fxmlLoader.setController(new MainSceneController());
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("QuiEstCe?");
            if (((String) js.get("difficulte")).equals("Facile")) {
                stage.setTitle("QuiEstCe? - Mode Facile");
            }
            File logo = new File("images/logoQuiEstCe.png");
            stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
            stage.setScene(new Scene(root1));
            stage.show();
            ((Stage) jsonNameLabel.getScene().getWindow()).close();
        } catch (FileNotFoundException e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        } catch (ParseException e) {
            e.getStackTrace();
        }
    }

    private void estNouvellePartiePossible() {
        if (difficulte != null && jsonPath != null)
            buttonnouvellepartie.setDisable(false);
    }

    // Boutton Multijoueur
    @FXML
    void chargerMulti(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pageMulti.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setResizable(true);
        stage.setTitle("QuiEstCe? - Multi-joueur - initialisation");
        File logo = new File("images/logoQuiEstCe.png");
        stage.getIcons().add(new Image("file:///" + logo.getAbsolutePath()));
        stage.setScene(new Scene(root1));
        stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // System.out.println("Fenetre fermé");
                System.exit(0);
            }
        });
        stage.show();
        ((Stage) jsonNameLabel.getScene().getWindow()).close();
    }
}