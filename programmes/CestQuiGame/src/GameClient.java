import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public String ecouterMessage() throws IOException {
        String msg = null;

        System.out.println("Client en attente de message");
        msg = in.readUTF();
        System.out.println("Message reçu par le client : " + msg);
        if (msg.equals("close")) {
            System.out.println("Bouton fermeture pressed");
            stopSocket();
            System.out.println("Socket client fermée");
        }
        return msg;

    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
        System.out.println("Message envoyé par le client : " + msg);
    }

    public void enregistrerJson() throws IOException, ParseException {
        System.out.println("Recuperation du JSON");
        byte[] mybytearray = new byte[1000000];
        FileOutputStream fos = new FileOutputStream("CestQuiGame/bin/gameTamp/game.json");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = in.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
    }

    public void enregistrerImage(String nomImage) throws IOException {
        System.out.println("Recuperation du l'image " + nomImage);
        byte[] mybytearray = new byte[10000000];
        FileOutputStream fos = new FileOutputStream("CestQuiGame/bin/gameTamp/" + nomImage);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = in.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        System.out.println("Image récupéré");
    }

    public void stopSocket() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
            System.out.println("Socket Client close");
        }
    }
}