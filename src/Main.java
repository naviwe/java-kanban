import server.HttpTaskServer;
import server.KVServer;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
    }
}