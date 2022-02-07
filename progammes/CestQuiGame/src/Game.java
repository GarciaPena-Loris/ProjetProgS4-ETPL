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
    private String[] personnageChoisi;
    // private HashMap<String, Boolean> listePersonnages;
    private ArrayList<ArrayList<HashMap<ArrayList<HashMap<String, String>>, Boolean>>> listePersonnages;

    public Game(Difficulte d, String cheminJSON) throws Exception {
        this.difficulte = d;
        // creationListePersonnages
        HashMap<String, ?> contenueJSON = recupererJson(cheminJSON);
        int ligne = (int) contenueJSON.get("ligne");
        int colonne = (int) contenueJSON.get("colonne");
        for (int i = 0; i < ligne; i++) {
            for (int j = 0; j < colonne; j++) {
                // for ( ArrayList listePersonnage : contenueJSON.get("possibilites")) {

                // }
            }
        }

        int rand = 0 + (int) (Math.random() * ((contenueJSON.get("possibilites").toString().length() - 0) + 1));
    }

    public Game(Difficulte d, String cheminPersonnages, String cheminSauvegarde) {

    }

    public HashMap<String, ?> recupererJson(String cheminJSON) throws Exception {
        // recuperer le JSON
        // converti le JSON en String[]
        // {
        // "images": "personnages/",
        // "ligne": "3",
        // "colonne": "8",
        // "possibilites": {
        // "0": { "fichier": "samuel.jpg", "prenom": "Samuel", "genre": "homme",
        // "cheveux": "blanc", "lunettes": "oui", "chauve": "oui", ... },
        // "1": { "fichier": "leon.jpg", "prenom": "Léon", "genre": "homme", "cheveux":
        // "blond", "lunettes": "oui", "chauve": "non", ... },
        // "2": { "fichier": "simon.jpg","prenom": "Simon", "genre": "homme", "cheveux":
        // "blanc", "lunettes": "oui", "chauve": "non", ...},
        // ...
        // }
        // }

        // TEST JSON
        // parsing file "JSONExample.json"
        Object ob = new JSONParser().parse(new FileReader("Test.json"));

        // typecasting ob to JSONObject
        JSONObject js = (JSONObject) ob;

        String firstName = (String) js.get("firstName");
        String lastName = (String) js.get("lastName");

        System.out.println("First name is: " + firstName);
        System.out.println("Last name is: " + lastName);
        return null;
    }
}