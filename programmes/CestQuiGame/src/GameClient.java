import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class GameClient implements GameSocket {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public GameClient() {
        System.out.println("----Client----");
    }

    public String connectionServeur(String ip) {
        try {
            clientSocket = new Socket(ip, 51537);
            System.out.println("Client créé et connecté");

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            InetAddress inetadr = InetAddress.getLocalHost();
            out.writeUTF(inetadr.toString());

        } catch (IOException ex) {
            System.err.println("Connexion du socket client impossible");
            return "error";
        }
        return "ok";
    }

    public String connexionClient() {
        return null;
    }

    public String ecouterMessage() throws IOException {
        String msg = null;

        try {
            System.out.println("Client en attente de message");
            msg = in.readUTF();
            System.out.println("Message reçu par le client : " + msg);
            if (msg.equals("close")) {
                System.out.println("Bouton fermeture pressed");
                stopSocket();
                System.out.println("Socket client fermée");
            }
        } catch (IOException e) {
            System.err.println("Connexion client fermé");
            stopSocket();
            return "close";
        }
        return msg;
    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
        System.out.println("Message envoyé par le client : " + msg);
    }

    public void stopSocket() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
            System.out.println("Socket Client close");
        }
    }
}