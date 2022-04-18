import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class MainSceneController extends UtilController {
    @FXML
    private BorderPane borderPaneId;
    @FXML
    private AnchorPane anchorPaneId;
    @FXML
    private Label questionText1;
    @FXML
    private MenuButton buttonAttribut1;

    @FXML
    protected void initialize() {
        //injecte les differents element FXML dans le pere
        setBorderPaneId(borderPaneId);
        setAnchorPaneId(anchorPaneId);
        setQuestionText1(questionText1);
        setButtonAttribut1(buttonAttribut1);

        // Recuperer les données du JSON ici
        lireJson();

        GridPane grillePerso = new GridPane();
        creerGrille(grillePerso);

        creerDernierMenuBouton(buttonAttribut1);
    }

}
