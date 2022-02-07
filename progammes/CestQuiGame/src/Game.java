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

    public void verifierElimination() {
        // verifier qu'il a pas eliminer la mauvaise personne

        // sauvegarder
        // double boucle dans laquelle tu ajoute chaque element de "listePersonnages"
        // dans notre Json de sauvegarde.
        // enregistrer quelquepart

    }

}