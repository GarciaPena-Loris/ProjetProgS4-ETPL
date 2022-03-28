INSTRUCTIONS :

/!\ version de java : java 17 /!\
__
Pour le jeu :


Ourvir le cmd dans le répertoire où se trouve 'programmes.jar'. (./programmes/...)
Copier la commande : java -jar --enable-preview --module-path "../librairies/javafx-sdk-17.0.2/lib" --add-modules javafx.controls,javafx.fxml .\programmes.jar
Exécuter la commande dans le susdit cmd.
Sélectionner avec le bouton 'choix json' le json. ( une grille de test est disponible : "test.JSON" dans "./programmes/...".)
Sélectionner la difficulté de votre choix.
Appuyer sur 'nouvelle partie'.

//Pour reprendre une partie la dernière partie là ou vous l'avez laisser il vous suffit d'appuyer sur le bouton "charger partie".

A vous de jouer !

__
Pour le générateur :


Ourvir le cmd dans le répertoire où se trouve 'generateur.jar'. (./generateur/...)
Copier la commande : java -jar --enable-preview --module-path "../librairies/javafx-sdk-17.0.2/lib" --add-modules javafx.controls,javafx.fxml .\generateur.jar
Exécuter la commande dans le susdit cmd.
Choisir un dossier contenant les images qui constiturons la future grille pour jouer au "Qui est-ce ?". (un dossier d'image est disponible : "personnage" dans "./generateur/images/personnages")
Suivre les étapes. 
Vous pouvez récuperer votre grille fonctionnelle (__.json) dans le dossier que vous avez choisi.




// Des changements ont été apportés au programmes principale du jeu depuis le dernier rendu pour régler des problemes. 
// Il est conseillé d'utiliser la dernière version disponible.