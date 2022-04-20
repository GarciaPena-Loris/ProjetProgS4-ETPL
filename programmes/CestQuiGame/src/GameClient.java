import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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

    public static byte[] decodeImage(String imageDataString) {
        return Base64.getDecoder().decode(imageDataString);
    }

    public void enregistrerImage() throws IOException {
        System.out.println("Recuperation du l'image");
        String fileReceived = "";
        while (true) {
            String partOfFile = in.readUTF();
            if (!partOfFile.equals("end")) {
                fileReceived += partOfFile;
            } else
                break;
        }
        // convert received data
        System.out.println("File Received!");
        JSONObject obj1 = (JSONObject) JSONValue.parse(fileReceived);
        String name = obj1.get("filename").toString();
        String image = obj1.get("image").toString();

        // convert from base64 to byte array
        byte[] imageByteArray = decodeImage(image);

        // convert byte array to a file image
        FileOutputStream imageOutFile = new FileOutputStream("CestQuiGame/bin/gameTamp/" + name);
        imageOutFile.write(imageByteArray);
        imageOutFile.close();
        System.out.println("Image Successfully Manipulated!");
        System.out.println("Image récupéré " + name + " récupéré");
    }

    public void stopSocket() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
            System.out.println("Socket Client close");
        }
    }
}