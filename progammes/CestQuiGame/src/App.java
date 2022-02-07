public final class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        // Creer et afficher le menu
        // en fonction des choix créer une interface de QuiEstCe
        // (difficulte / sauvegarde)
        //creer une "Game" avec les différents parametres et le lien du JSON
        Game partieEnCour = new Game(Difficulte.normal, "c:/documents/listePersonnages.json");

        
    }
}