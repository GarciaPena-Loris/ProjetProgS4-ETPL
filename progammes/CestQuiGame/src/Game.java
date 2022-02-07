import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Game {
    private Difficulte difficulte;
    private JSONObject personnageChoisi;
    private ArrayList<ArrayList<HashMap<JSONObject, Boolean>>> listePersonnages;

    public Game(Difficulte d, JSONObject JSONPersonnages) throws Exception {
        this.difficulte = d;
        // creationListePersonnages
        


        //int rand = 0 + (int) (Math.random() * ((contenueJSON.get("possibilites").toString().length() - 0) + 1));
    }

    public Game(Difficulte d, JSONObject JSONPersonnages, String cheminSauvegarde) {

    }

    public boolean verifierReponse(String question) {

        return false;
    }

    public void verifierElimination() {
        //verifier qu'il a pas eliminer la mauvaise personne

        //sauvegarder
        //double boucle dans laquelle tu ajoute chaque element de "listePersonnages" dans notre Json de sauvegarde.
        //enregistrer quelquepart

    }
    
}