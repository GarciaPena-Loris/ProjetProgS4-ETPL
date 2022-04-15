import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class GameClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public GameClient(String ip) {
        System.out.println("----Client----");
        try {
            System.out.println("Debug : creation du client");
            clientSocket = new Socket(ip, 51537);
            System.out.println("Debug : client créé");

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            InetAddress inetadr = InetAddress.getLocalHost();
            out.writeUTF(inetadr.toString());

        } catch (IOException ex) {
            System.out.println("Probleme de construction du client");
        }
    }

    public void ecouterMessage() throws IOException {
        while(true) {
            System.out.println("Client en attente de message");
            String msg = in.readUTF();
            System.out.println("Message reçu : " + msg);
            if (msg.equals("close")) {
                System.out.println("Bouton fermeture pressed");
                stopSocket();
                System.out.println("Socket client fermée");
                break;
            }
        }
    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
    } 

    public void stopSocket() throws IOException {
        clientSocket.close();
    }
}