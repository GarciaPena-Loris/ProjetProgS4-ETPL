import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.*;

public final class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        // Creer et afficher le menu
        // en fonction des choix créer une interface de QuiEstCe (difficulte / sauvegarde)

        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader("Test.json"));

            String cheminVersImages = (String) js.get("images");
            int ligne = Integer.parseInt((String) js.get("ligne"));
            int colonne = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");
            
            Game partieEnCour = new Game(Difficulte.normal, personnages, ligne, colonne);
            partieEnCour.afficheEtatPartie();
            partieEnCour.verifierElimination(cheminVersImages, ligne, colonne);
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}