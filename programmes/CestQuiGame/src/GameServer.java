import java.io.*;
import java.net.*;

public class GameServer {
    private ServerSocket serveurSocket;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private int port = 51537;

    public GameServer() {
        System.out.println("----Serveur de jeu----");
        try {
            serveurSocket = new ServerSocket(port);

            System.out.println("Serveur en attente de connexion...");

            clientSocket = serveurSocket.accept();
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            System.out.println("Client connecté !");

            while(true) {
                System.out.println("Serveur en attente de l'ip du client");
                String ip = in.readUTF();
                System.out.println("Ip du client reçu : " + ip);
                stopSocket();
                break;
            }

        } catch (IOException ex) {
            System.out.println("Probleme de construction du serveur");
        }
    }

    public void ecouterMessage() throws IOException {
        while(true) {
            System.out.println("Serveur en attente de message");
            String msg = in.readUTF();
            System.out.println("Message reçu : " + msg);
            if (msg.equals("close")) {
                System.out.println("Bouton fermeture pressed");
                stopSocket();
                System.out.println("Socket serveur fermée");
                break;
            }
        }
    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public void stopSocket() throws IOException {
        serveurSocket.close();
    }
}
