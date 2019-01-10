package net.neferett.hooker.Server;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import lombok.Data;
import lombok.SneakyThrows;
import net.neferett.coreengine.Processors.Config.CoreConfig;
import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Threads.TaskProcessors;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Routes.RoutingProperties;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.List;

@Data
public class ServerHandler {

    private final int port;

    private HttpsServer server;

    private TaskProcessors processors;

    private HttpsConfigurator configurator;

    @SneakyThrows
    public void build() {
        Logger.logInChannel("Creating new Server On Port : " + this.port, Hooker.getInstance().getFile().getChannel());

        this.server = HttpsServer.create(new InetSocketAddress(this.port), 0);
        this.processors = new TaskProcessors(CoreConfig.getConfig().getThreads());
        this.keyStore();
    }

    private void keyStore() {
        char[] password = "password".toCharArray();

        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("testkey.jks");
            ks.load(fis, password);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            this.configurator = new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters httpsParameters) {
                    SSLContext sslContext = getSSLContext();
                    SSLParameters defaultSSLParameters = sslContext.getDefaultSSLParameters();
                    httpsParameters.setSSLParameters(defaultSSLParameters);
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRoute(RoutingProperties properties) {
        Logger.logInChannel("Creating new route : " + properties.getName(), Hooker.getInstance().getFile().getChannel());
        this.server.createContext(properties.getName(), properties);
    }

    public void addRoutes(List<RoutingProperties> propertiesList) {
        propertiesList.forEach(this::addRoute);
    }

    public void start() {
        this.server.setExecutor(this.processors.getExecutorService());
        this.server.setHttpsConfigurator(this.configurator);
        this.server.start();
    }

    public void stop() {
        this.server.stop(1);
        this.processors.shutDown();
    }

}

