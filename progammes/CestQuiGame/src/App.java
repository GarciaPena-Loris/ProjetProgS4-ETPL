import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.*;
import java.util.List;

public final class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        // Creer et afficher le menu
        // en fonction des choix créer une interface de QuiEstCe (difficulte / sauvegarde)

        try {
            JSONObject js = (JSONObject) new JSONParser().parse(new FileReader("Test.json"));

            String cheminVersImages = (String) js.get("images");
            int lignes = Integer.parseInt((String) js.get("ligne"));
            int colonnes = Integer.parseInt((String) js.get("colonne"));
            JSONObject personnages = (JSONObject) js.get("personnages");
            
            Game partieEnCour = new Game(Difficulte.normal, personnages, lignes, colonnes);
            partieEnCour.afficheEtatPartie();
            partieEnCour.verifierElimination(cheminVersImages, lignes, colonnes);
            
            List<String> listeAttributs = partieEnCour.getListeAttributs();
            boolean estBinaire = partieEnCour.estQuestionBinaire("chauve");
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