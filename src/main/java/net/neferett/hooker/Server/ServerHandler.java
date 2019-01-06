package net.neferett.hooker.Server;

import lombok.Data;
import lombok.SneakyThrows;
import net.neferett.coreengine.Processors.Config.CoreConfig;
import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Threads.TaskProcessors;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Routes.RoutingProperties;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.List;

@Data
public class ServerHandler {

    private final int port;

    private HttpServer server;

    private TaskProcessors processors;

    @SneakyThrows
    public void build() {
        Logger.logInChannel("Creating new Server On Port : " + this.port, Hooker.getInstance().getFile().getChannel());

        this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
        this.processors = new TaskProcessors(CoreConfig.getConfig().getThreads());
    }

    public void addRoute(RoutingProperties properties) {
        Logger.logInChannel("Creating new route : " + properties.getName(), Hooker.getInstance().getFile().getChannel());
        this.server.createContext(properties.getName(), properties);
    }

    public void addRoutes(List<RoutingProperties> propertiesList) {
        propertiesList.forEach(this::addRoute);
    }

    public void start() {
        this.server.setExecutor(this.processors.getExecutorService());
        this.server.start();
    }

    public void stop() {
        this.server.stop(1);
        this.processors.shutDown();
    }

}

