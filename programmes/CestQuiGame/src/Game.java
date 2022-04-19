import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class Game {
    private String difficulte;
    private JSONObject personnageChoisi;
    private ArrayList<JSONObject> listePersonnages;

    public Game(String d, JSONObject JSONPersonnages, int ligne, int colonne) {
        this.difficulte = d;
        // creationListePersonnages
        listePersonnages = new ArrayList<>();
        for (int i = 0; i < ligne * colonne; i++) {
            JSONObject personnage = (JSONObject) JSONPersonnages.get(String.valueOf(i));
            if (personnage != null)
                listePersonnages.add(personnage);
        }

        // choix du personnages aleatoirement
        int rand = (int) (Math.random() * ((JSONPersonnages.size())));
        personnageChoisi = (JSONObject) JSONPersonnages.get(String.valueOf(rand));
    }

    public Game(String d, JSONObject JSONPersonnages, int ligne, int colonne, JSONObject personnageChoisi) {
        this.difficulte = d;
        listePersonnages = new ArrayList<>();

        for (int i = 0; i < ligne * colonne; i++) {
            JSONObject personnage = (JSONObject) JSONPersonnages.get(String.valueOf(i));
            if (personnage != null)
                listePersonnages.add(personnage);
        }
        this.personnageChoisi = personnageChoisi;

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
            if (listConnecteurs.get(i - 1).equals("et")) {
                correspondPersonnage &= personnageChoisi.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
            } else {
                correspondPersonnage |= personnageChoisi.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
            }
        }
        return correspondPersonnage;
    }


    public ArrayList<String> getListeAttributs() {
        ArrayList<String> attributs = new ArrayList<String>(personnageChoisi.keySet());
        attributs.remove("image");
        attributs.remove("etat");
        return attributs;
    }


    public ArrayList<String> getListeValeurs(String attribut, ArrayList<String> listePersonnagesElimines) {
        ArrayList<String> valeurs = new ArrayList<>();
        for (JSONObject personnage : listePersonnages) {
            if (!listePersonnagesElimines.contains((String) personnage.get("image"))) {
                String value = (String) personnage.get(attribut);
                if (value != null)
                    if (!valeurs.contains(value))
                        valeurs.add(value);
            }
        }
        return valeurs;
    }

    public ArrayList<String> personnagesAEliminer(ArrayList<String> listePersonnagesElimines,
            ArrayList<String> listeAttribut, ArrayList<String> listeValeurs,
            ArrayList<String> listConnecteurs) {
        ArrayList<String> listePersoAEliminer = new ArrayList<>();
        for (JSONObject personnage : listePersonnages) {
            String nomImage = personnage.get("image").toString();
            if (!listePersonnagesElimines.contains((nomImage))) {
                boolean correspondPersonnage = personnage.get(listeAttribut.get(0)).equals(listeValeurs.get(0));
                for (int i = 1; i < listeAttribut.size(); i++) {
                    if (listConnecteurs.get(i - 1).equals("et")) {
                        correspondPersonnage &= personnage.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
                    } else {
                        correspondPersonnage |= personnage.get(listeAttribut.get(i)).equals(listeValeurs.get(i));
                    }
                }
                if (correspondPersonnage) {
                    listePersoAEliminer.add((nomImage));
                }
            }
        }
        return listePersoAEliminer;
    }

    public boolean estQuestionBinaire(String attribut) {
        String value = (String) listePersonnages.get(0).get(attribut);
        if (value == null) {
            return false;
        }
        return value.equals("oui") || value.equals("non");
    }

    public boolean verifierElimination(ArrayList<String> listePersonnagesElimines) {
        String nomImage = personnageChoisi.get("image").toString();
        return listePersonnagesElimines.contains((nomImage));
    }

    @SuppressWarnings("unchecked")
    public void sauvegarderPartieEnCour(String images, int ligne, int colonne) {
        // sauvegarde de la partie
        JSONObject partieSave = new JSONObject();
        partieSave.put("images", String.valueOf(images));
        partieSave.put("ligne", String.valueOf(ligne));
        partieSave.put("colonne", String.valueOf(colonne));
        partieSave.put("difficulte", difficulte);
        partieSave.put("personnagesChoisi", personnageChoisi);

        JSONObject listePerso = new JSONObject();
        int i = 0;
        for (JSONObject personnage : listePersonnages) {
            listePerso.put(String.valueOf(i), personnage);
            i++;

        }
        partieSave.put("personnages", listePerso);

        try (FileWriter file = new FileWriter(new File("CestQuiGame/bin/save.json"))) {
            file.write(partieSave.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tuerPersonnage(String nomPersonnage) {
        for (JSONObject personnage : listePersonnages) {
            if ((personnage.get("image").toString()).equals(nomPersonnage)) {
                JSONObject personnageATuer = (JSONObject) personnage;
                personnageATuer.replace("etat", "mort");
            }
        }
    }

    public ArrayList<String> getListePersonnageMort() {
        ArrayList<String> listePersoMort = new ArrayList<>();
        for (JSONObject personnage : listePersonnages) {
            if (personnage.get("etat").equals("mort")) {
                listePersoMort.add((personnage.get("image").toString()));
            }
        }
        return listePersoMort;
    }

    public String getPersonnageChoisi() {
        return (String) personnageChoisi.get("prenom");
    }

    public String getImagePersonnageChoisi() {
        return personnageChoisi.get("image").toString();
    }

    public int getNombrePersonnages() {
        return listePersonnages.size();
    }

}