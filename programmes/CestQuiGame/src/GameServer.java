import java.io.*;
import java.net.*;

public class GameServer {
    private ServerSocket ss;
    private int nbJoueurs;
    private int port = 51537;

    public GameServer(){
        System.out.println("----Serveur de jeu----");
        nbJoueurs=0;
        try {
            ss = new ServerSocket(port);
        }catch(IOException ex){
            System.out.println("y'a un pb dans le constructeur");
        }
    }

    public void connection(){
        System.out.println("en attente d'une connection");
        try{
            while (nbJoueurs <2){
                Socket s = ss.accept();
                nbJoueurs++;
                System.out.println("Player #"+ nbJoueurs + " has connected");
            }
        }catch(IOException ex){
            System.out.println("probleme dans la connection");
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.connection();
    }
}
