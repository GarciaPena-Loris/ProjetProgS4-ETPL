import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class pageAttributController {

    private static int numeroID=0;
    private int positionY;
    ArrayList<String> listAttributsString = new ArrayList<>();
    ArrayList<Label> listAttributsLabel = new ArrayList<>();
    ArrayList<Label> listSupprLabel = new ArrayList<>();
   
 
    @FXML 
    private static Button btnAttribut;

    @FXML
    private Button submitButton;

    @FXML
    private AnchorPane anchorPaneAttributs;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button addButton;

    @FXML
    private TextField champDeTexte;

    @FXML
    public void initialize(){
       if (!(pageGenerateurController.getListAttributLabel().isEmpty())){
           int foo1=1;
           int foo2=1;
           listAttributsLabel = pageGenerateurController.getListAttributLabel();
           listAttributsString = pageGenerateurController.getListAttributString();
           listSupprLabel = pageGenerateurController.getListSupprLabel();
           for (Label label : listAttributsLabel) {
            anchorPaneAttributs.getChildren().add(label);
            label.setLayoutY(25+(foo1*15));
            foo1++;
           }
           for (Label suppr : listSupprLabel) {
            //int valeurY = listSupprLabel.indexOf(label)+1;
            //Label suppr = new Label("supprimer");
            //suppr.setUnderline(true);
            //suppr.setOnMouseClicked(supprimerAttribut);
            //suppr.setId("suppr"+foo2);
            anchorPaneAttributs.getChildren().add(suppr);
            suppr.setOnMouseClicked(supprimerAttribut);
            suppr.setLayoutX(200);
            suppr.setLayoutY(25+(foo2*15));
            foo2++;
           }
       } 
    }

    @FXML
    void ajouterAttribut(ActionEvent event) {
        if(champDeTexte.getText()!="" && !(listAttributsString.contains(champDeTexte.getText()))){
            //ajout des attributs
            listAttributsString.add(champDeTexte.getText());
            Label attribut = new Label(champDeTexte.getText());
            listAttributsLabel.add(attribut);
            anchorPaneAttributs.getChildren().add(attribut);
            numeroID++;
            attribut.setId(""+numeroID);
            attribut.setLayoutX(14);
            attribut.setLayoutY(25+(listAttributsLabel.size()*15));
            champDeTexte.setText("");
            //ajout des suppr
            Label suppr = new Label("supprimer");
            listSupprLabel.add(suppr);
            suppr.setUnderline(true);
            suppr.setOnMouseClicked(supprimerAttribut);
            suppr.setId("suppr"+numeroID);
            anchorPaneAttributs.getChildren().add(suppr);
            suppr.setLayoutX(200);
            suppr.setLayoutY(25+(listAttributsLabel.size()*15));
           
        }
    }
    //fonction suppr
    EventHandler<MouseEvent> supprimerAttribut = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent event){
            int foo=1;
            String source = ((Label)event.getSource()).getId();
            Label sourceSuppr = ((Label)event.getSource());
            String labelSource = source.substring(5,source.length());
            Label attributSuppr = (Label) anchorPaneAttributs.getScene().lookup("#"+labelSource);
            listAttributsLabel.remove(attributSuppr);
            listAttributsString.remove(attributSuppr.getText());
            attributSuppr.setText("");
            attributSuppr.setDisable(true);
            sourceSuppr.setText("");
            sourceSuppr.setDisable(true);
            listSupprLabel.remove(sourceSuppr);
            for (Label label : listAttributsLabel) {
                label.setLayoutY(25+(foo*15));
                Label supprOfAttribute = (Label) anchorPaneAttributs.getScene().lookup("#suppr"+label.getId());
                supprOfAttribute.setLayoutY(25+(foo*15));
                foo++;
            }
        }    
    };

    //recupere le boutton Attribut pour 
    @FXML
    public static void setBtnAttribut(Button btn){
        btnAttribut=btn;
    }
    // Valide les attributs, ferme la fenetre et envoie la liste au Controleur. Rend la fenêtre réouvrable
    @FXML
    void submit(ActionEvent event){
        pageGenerateurController.setListAttributString(listAttributsString);
        pageGenerateurController.setListAttributLabel(listAttributsLabel);
        pageGenerateurController.setListSupprLabel(listSupprLabel);
        ((Stage) submitButton.getScene().getWindow()).close();
        btnAttribut.setDisable(false);

    }
}
