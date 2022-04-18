import java.io.IOException;

public interface GameSocket {
    String ecouterMessage() throws IOException;

    void envoyerMessage(String msg) throws IOException;

    void stopSocket() throws IOException;

    String connexionClient() throws IOException;

    String connectionServeur(String ip);
}
