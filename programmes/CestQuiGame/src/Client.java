import java.net.*;
import java.io.*;

public class Client {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public Client(){
        System.out.println("--- Client ---");
        try {
            socket = new Socket("localhost", 51734);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Probleme du constructeur client");
        }
    }

}
