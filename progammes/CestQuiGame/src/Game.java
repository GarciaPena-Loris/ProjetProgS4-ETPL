import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Game {
    private Difficulte difficulte;
    private JSONObject personnageChoisi;
    private JSONObject[][] listePersonnages;

    public Game(Difficulte d, JSONObject JSONPersonnages, int ligne, int colonne) {
        this.difficulte = d;

        // creationListePersonnages
        listePersonnages = new JSONObject[ligne][colonne];
        int compteur = 0;
        for (int i = 0; i < ligne; i++) {
            for (int j = 0; j < colonne; j++) {
                listePersonnages[i][j] = (JSONObject) JSONPersonnages.get(String.valueOf(compteur));
                compteur++;
            }
        }

        // choix du personnages aleatoirement
        int rand = (int) (Math.random() * ((JSONPersonnages.size())));
        personnageChoisi = (JSONObject) JSONPersonnages.get(String.valueOf(rand));
    }

    public void afficheEtatPartie() {
        System.out.println("Difficulté : " + this.difficulte + "\n");

        System.out.println("Liste des personnages :");
        for (JSONObject[] jsonObjects : listePersonnages) {
            for (JSONObject jsonObject : jsonObjects) {
                System.out.println(jsonObject + "\n");
            }
        }
        System.out.println("\nPersonnage choisi : " + this.personnageChoisi);
    }

    public boolean verifierReponse(String question) {

        return false;
    }

    public void verifierElimination(String images, String ligne, String colonne) {
        sauvegarderPartieEnCour(images, ligne, colonne);
        
    }

    public void sauvegarderPartieEnCour(String images, String ligne, String colonne) {
        // sauvegarde de la partie
        JSONObject partieSave = new JSONObject();
        partieSave.put("images", String.valueOf(images));
        partieSave.put("ligne", ligne);
        partieSave.put("colonne", colonne);
        partieSave.put("difficulte", String.valueOf(difficulte));
        partieSave.put("personnagesChoisi", personnageChoisi);

        JSONArray array = new JSONArray();
        JSONObject listePerso = new JSONObject();
        int i = 0;
        for (JSONObject[] personnages : listePersonnages) {
            for (JSONObject personnage : personnages) {
                listePerso.put(String.valueOf(i), personnage);
                i++;
            }
        }
        array.add(listePerso);
        partieSave.put("personnages", array);

        try (FileWriter file = new FileWriter(new File("CestQuiGame/bin/save.json"))) {
            file.write(partieSave.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}