import server.KVServer;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
    }
}