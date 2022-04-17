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
        } catch (IOException ex) {
            System.err.println("Probleme de construction du serveur");
        }
    }

    public String connexionClient() throws IOException {
        String ip = null;
        System.out.println("Serveur en attente de connexion...");

        clientSocket = serveurSocket.accept();
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Client connecté !");
        while (true) {
            System.out.println("Serveur en attente de l'ip du client");
            ip = in.readUTF();
            System.out.println("Ip du client reçu : " + ip);
            break;
        }

        return ip;
    }

    public String ecouterMessage() throws IOException {
        String msg = null;
        while (true) {
            try {
                System.out.println("Serveur en attente de message");
                msg = in.readUTF();
                System.out.println("Message reçu : " + msg);
                if (msg.equals("close")) {
                    System.out.println("Bouton fermeture pressed");
                    stopSocket();
                    System.out.println("Socket serveur fermée");
                }
            } catch (IOException e) {
                System.err.println("Connexion serveur fermé");
                System.out.println("Fermeture de la socket" + clientSocket.getLocalSocketAddress());
                clientSocket.close();
                return "close";
            }
            break;
        }
        return msg;
    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public void stopSocket() throws IOException {
        if (clientSocket != null) {
            serveurSocket.close();
            System.out.println("Socket Serveir close");
        }
    }
}
