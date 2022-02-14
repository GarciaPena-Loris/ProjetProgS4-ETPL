import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public boolean verifierReponse(HashMap<String, String> propositions) {
        boolean correspondPersonnage = true;
        for (String key : propositions.keySet()) {
            if (personnageChoisi.get(key) != null && propositions.get(key) != null)
                correspondPersonnage &= personnageChoisi.get(key).equals(propositions.get(key));
        }
        return correspondPersonnage;
    }

    public boolean verifierReponse(String key, String value) {
        if (personnageChoisi.get(key) != null)
            return personnageChoisi.get(key).equals(value);
        return false;
    }

    public List<String> getListeAttributs() {
        List<String> attributs = new ArrayList<>(listePersonnages[0][0].keySet());
        attributs.remove("image");
        return attributs;
    }

    public boolean estQuestionBinaire(String attribut) {
        String value = (String) listePersonnages[0][0].get(attribut);
        if (value == null) {
            return false;
        }
        return value.equals("oui") || value.equals("non");
    }

    public void verifierElimination(String images, int ligne, int colonne) {
        sauvegarderPartieEnCour(images, ligne, colonne);

    }

    public void sauvegarderPartieEnCour(String images, int ligne, int colonne) {
        // sauvegarde de la partie
        JSONObject partieSave = new JSONObject();
        partieSave.put("images", String.valueOf(images));
        partieSave.put("ligne", String.valueOf(ligne));
        partieSave.put("colonne", String.valueOf(colonne));
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