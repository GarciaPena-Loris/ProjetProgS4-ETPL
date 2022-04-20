import java.io.*;
import java.net.*;
import java.util.Base64;

import org.json.simple.JSONObject;

public class GameServer implements GameSocket {
    private ServerSocket serveurSocket;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private int port = 51537;

    public GameServer() {
        // System.out.println("----Serveur de jeu----");
        try {
            serveurSocket = new ServerSocket(port);
        } catch (IOException ex) {
            // System.err.println("Probleme de construction du serveur");
        }
    }

    public String connexionClient() throws IOException {
        String ip = null;
        // System.out.println("Serveur en attente de connexion...");

        clientSocket = serveurSocket.accept();
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        // System.out.println("Client connecté !");
        while (true) {
            // System.out.println("Serveur en attente de l'ip du client");
            ip = in.readUTF();
            // System.out.println("Ip du client reçu : " + ip);
            break;
        }

        return ip;
    }

    public String ecouterMessage() throws IOException {
        String msg = null;
        // System.out.println("Serveur en attente de message");
        msg = in.readUTF();
        // System.out.println("Message reçu par le serveur : " + msg);
        if (msg.equals("close")) {
            // System.out.println("Bouton fermeture pressed");
            stopSocket();
            // System.out.println("Socket serveur fermée");
        }
        return msg;
    }

    public void envoyerMessage(String msg) throws IOException {
        out.writeUTF(msg);
        // System.out.println("Message envoyé par le serveur : " + msg);
    }

    public void envoyerJson(File file) throws IOException {
        // System.out.println("Envois du fichier : " + file.getName());
        byte[] mybytearray = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = clientSocket.getOutputStream();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
        bis.close();
    }

    public static String encodeImage(byte[] imageByteArray) {
        return Base64.getEncoder().encodeToString(imageByteArray);
    }

    public void envoyerImage(File image) throws IOException {
        // System.out.println("Envois de l'image " + image.getName());

        FileInputStream imageInFile = new FileInputStream(image);
        byte imageData[] = new byte[(int) image.length()];
        imageInFile.read(imageData);

        String imageDataString = encodeImage(imageData);
        imageInFile.close();
        // System.out.println("Image correctement encodé !");

        JSONObject obj = new JSONObject();

        obj.put("nomFichier", image.getName());
        obj.put("image", imageDataString);

        String jsonEncode = obj.toJSONString();
        int tailleMax = 64000;
 
        String[] chunks = jsonEncode.split("(?<=\\G.{" + tailleMax + "})");
        for (String string : chunks) {
            out.writeUTF(string);     
        }
        out.writeUTF("endImage");

        // System.out.println("Envoie de l'image " + image.getName() + " terminé");
    }

    public void stopSocket() throws IOException {
        if (serveurSocket != null && clientSocket != null) {
            clientSocket.close();
            // System.out.println("Socket Serveur close");
        }
    }
}
