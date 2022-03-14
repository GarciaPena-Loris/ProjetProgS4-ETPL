import java.io.File;
import java.io.IOException;

//javafx
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("pageAvantJeu.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Qui est-ce ?");
            File logo = new File("images/logoQuiEstCe.png");
            primaryStage.getIcons().add(new Image(logo.getAbsolutePath()));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

}