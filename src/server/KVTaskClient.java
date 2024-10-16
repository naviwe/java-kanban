package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;
    private final HttpClient client;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        client = HttpClient.newHttpClient();
        URI registerUrl = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(registerUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apiToken = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI putUrl = URI.create(String.format("%s/save/%s?API_TOKEN=%s", url, key, apiToken));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(putUrl)
                .POST(body)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    public String load(String key) throws IOException, InterruptedException {
        URI loadUrl = URI.create(String.format("%s/load/%s?API_TOKEN=%s", url, key, apiToken));
        HttpRequest request = HttpRequest.newBuilder().uri(loadUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
