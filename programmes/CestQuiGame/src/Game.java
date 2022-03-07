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
    private JSONObject[] listePersonnages;

    public Game(Difficulte d, JSONObject JSONPersonnages, int ligne, int colonne) {
        this.difficulte = d;

        // creationListePersonnages
        listePersonnages = new JSONObject[ligne * colonne];
        for (int i = 0; i < ligne * colonne; i++) {
            listePersonnages[i] = (JSONObject) JSONPersonnages.get(String.valueOf(i));
        }

        // choix du personnages aleatoirement
        int rand = (int) (Math.random() * ((JSONPersonnages.size())));
        personnageChoisi = (JSONObject) JSONPersonnages.get(String.valueOf(rand));
    }

    public void afficheEtatPartie() {
        System.out.println("Difficulté : " + this.difficulte + "\n");

        System.out.println("Liste des personnages :");
        for (JSONObject jsonObject : listePersonnages) {
            System.out.println(jsonObject + "\n");
        }
        System.out.println("\nPersonnage choisi : " + this.personnageChoisi);
    }

    public boolean verifierReponse(ArrayList<String> listeAttribut, ArrayList<String> listeValeurs,
            ArrayList<String> listConnecteurs) {
        boolean correspondPersonnage = personnageChoisi.get(listeAttribut.get(0)).equals(listeValeurs.get(0));
        for (int i = 1; i < listeAttribut.size(); i++) {
            // if listConnecteurs.get(i - 1) != null
            if (listConnecteurs.get(i - 1) == "et") {
                correspondPersonnage &= personnageChoisi.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
            } else {
                correspondPersonnage |= personnageChoisi.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
            }
        }
        return correspondPersonnage;
    }

    public List<String> getListeAttributs() {
        List<String> attributs = new ArrayList<>(listePersonnages[0].keySet());
        attributs.remove("image");
        attributs.remove("etat");
        return attributs;
    }

    public List<String> getListeValeurs(String attribut) {
        List<String> valeurs = new ArrayList<>();

        for (JSONObject personnage : listePersonnages) {
            String value = (String) personnage.get(attribut);
            if (value != null)
                if (!valeurs.contains(value))
                    valeurs.add(value);

        }
        return valeurs;
    }

    public int NbrePersonnagesACocher(HashMap<String, String> propositions) {
        int i = 0, j = 0;
        for (JSONObject personnage : listePersonnages) {
            for (String key : propositions.keySet()) {
                if (personnage.containsValue(propositions.get(key)) && personnage.get("etat").equals("vivant")) {
                    i++;
                }
            }
            if (personnage.get("etat").equals("vivant")) {
                j++;
            }
        }
        System.out.println("Elimination de " + i + " sur " + j);
        return i;
    }

    public boolean estQuestionBinaire(String attribut) {
        String value = (String) listePersonnages[0].get(attribut);
        if (value == null) {
            return false;
        }
        return value.equals("oui") || value.equals("non");
    }

    public boolean verifierElimination(ArrayList<String> listePersonnagseElimines, String images, int ligne,
            int colonne) {
        if (!listePersonnagseElimines.contains(((String) personnageChoisi.get("prenom")).toLowerCase())) {
            sauvegarderPartieEnCour(images, ligne, colonne);
            return true;
        } else {
            return false;
        }
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
        for (JSONObject personnage : listePersonnages) {
            listePerso.put(String.valueOf(i), personnage);
            i++;

        }
        array.add(listePerso);
        partieSave.put("personnages", array);

        try (FileWriter file = new FileWriter(new File("CestQuiGame/bin/save.json"))) {
            file.write(partieSave.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPersonnageChoisi() {
        return (String) personnageChoisi.get("prenom");
    }

    public int getNombrePersonnages() {
        return listePersonnages.length;
    }

}