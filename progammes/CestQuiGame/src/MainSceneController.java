import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javafx.event.EventHandler;
import java.io.*;
import java.util.HashMap;
import java.util.List;

public class MainSceneController {

    // @FXML
    // private TextField tfTitle;

    // @FXML
    // void btnOkClicked(ActionEvent event) {
    // Stage mainWindow = (Stage) tfTitle.getScene().getWindow();
    // String title = tfTitle.getText();
    // mainWindow.setTitle(title);
    // }

    @FXML
    private BorderPane borderPaneId;
    @FXML
    private MenuButton buttonAttribut1;
    @FXML
    private MenuButton buttonAttribut2;

    private static Game partieEnCour;

    // initialize et met la grille dans la partie central
    // créée le button avec les choix d'attributs
    @FXML
    protected void initialize() {
        // Recuperer les données du JSON ici
        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader("Test.json"));

            String cheminVersImages = (String) js.get("images");
            int lignes = Integer.parseInt((String) js.get("ligne"));
            int colonnes = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");

            partieEnCour = new Game(Difficulte.normal, personnages, lignes,
                    colonnes);
            partieEnCour.afficheEtatPartie();
            partieEnCour.verifierElimination(cheminVersImages, lignes, colonnes);

            List<String> listeAttributs = partieEnCour.getListeAttributs();
            List<String> listeValeurs = partieEnCour.getListeValeurs("cheveux");

            HashMap<String, String> questions = new HashMap<>();

            // questions.put("cheveux", "blond");
            // questions.put("lunettes", "oui");
            // questions.put("chauve", "non");
            String[] listConnec = { "et", "et", "ou" };
            boolean reponseQuestion = partieEnCour.verifierReponse(questions,
                    listConnec);
            System.out.println(reponseQuestion);

            partieEnCour.NbrePersonnagesACocher(questions);

            // javafx
            buttonAttribut2.setVisible(false);
            GridPane grilleperso = new GridPane();
            grilleperso.setHgap(5);
            grilleperso.setVgap(5);
            int compteur = 1;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++) {
                    // Label label=new Label("YO");
                    String url = "C:/Users/loloy/Desktop/cours/L2_Info/Sem2/ProjetProgS4-ETPL/images/personnages/imageonline-co-split-image-"
                            + compteur + ".png";
                    String urlImage = url;
                    Image imagePerso = new Image(urlImage);
                    ImageView imageViewPerso = new ImageView(imagePerso);
                    imageViewPerso.setFitHeight(50);
                    imageViewPerso.setFitWidth(50);
                    // imageViewPerso.setId()
                    grilleperso.add(imageViewPerso, i, j);
                    compteur++;
                }
                grilleperso.setMaxSize(300, 500);
            }
            borderPaneId.setCenter(grilleperso);

            for (int i = 0; i < listeAttributs.size(); i++) {
                MenuItem attributs = new MenuItem(listeAttributs.get(i));
                buttonAttribut1.getItems().add(attributs);
                attributs.setOnAction(event1);
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Met en texte le choix selectionné et fait apparaitre les valeusr de
    // l'attribut selectionner si valeurs y a
    EventHandler<ActionEvent> event1 = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            String selectedAttribut = ((MenuItem) actionEvent.getSource()).getText();
            buttonAttribut1.setText(selectedAttribut);
            buttonAttribut2.getItems().clear();
            buttonAttribut2.setText("___");
            boolean estBinaire = partieEnCour.estQuestionBinaire(selectedAttribut);
            if (!estBinaire) {
                List<String> listeValeurs = partieEnCour.getListeValeurs(selectedAttribut);

                for (int i = 0; i < listeValeurs.size(); i++) {
                    MenuItem valeurs = new MenuItem(listeValeurs.get(i));
                    buttonAttribut2.getItems().add(valeurs);
                    valeurs.setOnAction(event2);
                }
                buttonAttribut2.setVisible(true);
            }
            else {
                buttonAttribut2.setVisible(false);
            }
        }
    };

    EventHandler<ActionEvent> event2 = new EventHandler<>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            String selectedAttribut = ((MenuItem) actionEvent.getSource()).getText();
            buttonAttribut2.setText(selectedAttribut);
        }
    };

    /*
     * @FXML
     * protected void AfficheText(){
     * textAffiche.setText("Le texte est affiché gg");}
     */

}
