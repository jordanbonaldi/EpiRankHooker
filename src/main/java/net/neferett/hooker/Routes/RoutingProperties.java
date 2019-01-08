package net.neferett.hooker.Routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import lombok.Data;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Data
public abstract class RoutingProperties implements HttpHandler {

    private String name;

    @Override
    @SneakyThrows
    public void handle(HttpExchange _t) {
        HttpsExchange t = (HttpsExchange)_t;
        t.getSSLSession();
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String response = this.commandProcess(t).toString();

        t.sendResponseHeaders(200, 0);

        BufferedOutputStream out = new BufferedOutputStream(t.getResponseBody());
        ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes());

        {
            byte[] buffer = new byte[2048];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
        }
        out.close();
    }

    public abstract JSONObject commandProcess(HttpExchange t);
}

