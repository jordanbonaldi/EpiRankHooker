package net.neferett.hooker.API;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.apache.http.protocol.HTTP.USER_AGENT;

@Data
public class HookerAPI {

    private final String auth;

    @SneakyThrows
    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        is.close();

        return sb.toString();
    }

    @SneakyThrows
    private String getFileContent(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        return convertStreamToString(response.getEntity().getContent());
    }

    public String call(String path) {
        return this.getFileContent(this.auth + path + "/?format=json");
    }
}
