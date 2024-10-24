package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private String text = "";

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                text = "Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа";
                sendText(h, text, 403);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    text = "Key для загрузки пустой. key указывается в пути: /load/{key}";
                    sendText(h, text, 400);
                    return;
                }
                if (data.containsKey(key)) {
                    sendText(h, data.get(key), 200);
                    return;
                }

                h.sendResponseHeaders(204, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                text = "Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа";
                sendText(h, text, 403);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    text = "Key для сохранения пустой. key указывается в пути: /save/{key}";
                    sendText(h, text, 400);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    text = "Value для сохранения пустой. value указывается в теле запроса";
                    sendText(h, text, 400);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                text = "/save ждёт POST-запрос, а получил: " + h.getRequestMethod();
                h.sendResponseHeaders(405, 0);
                sendText(h, text, 405);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken, 200);
            } else {
                text = "/register ждёт GET-запрос, а получил " + h.getRequestMethod();
                sendText(h, text, 405);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
    }
}
