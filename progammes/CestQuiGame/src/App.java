import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.*;

public final class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        // Creer et afficher le menu
        // en fonction des choix créer une interface de QuiEstCe
        // (difficulte / sauvegarde)
        // creer une "Game" avec les différents parametres et le lien du JSON

        // converti le JSON en String[]

        // parsing file "JSONExample.json"
        JSONObject js = (JSONObject) new JSONParser().parse(new FileReader("Test.json"));

        String cheminVersImages = (String) js.get("images");
        String ligne = (String) js.get("ligne");
        String colonne = (String) js.get("colonne");
        JSONObject personnages = (JSONObject) js.get("personnages");

        System.out.println("cheminVersImages : " + cheminVersImages);
        System.out.println("ligne : " + ligne);
        System.out.println("colonne : " + colonne);
        System.out.println(personnages);

        
        //Game partieEnCour = new Game(Difficulte.normal, personnages, ligne, colonne);
        //partieEnCour.verifierElimination();

    }
}