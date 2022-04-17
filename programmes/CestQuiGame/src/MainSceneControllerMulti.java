import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class MainSceneControllerMulti extends MainSceneController implements Initializable {
    private String role;

    public MainSceneControllerMulti(String role) {
        this.role = role;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (role.equals("serveur"))
            System.out.println("Lancement controller multi serveur");
        if (role.equals("client"))
            System.out.println("Lancement controller multi client");
    }
}
