package net.neferett.hooker.Routes;

import com.sun.net.httpserver.HttpExchange;
import lombok.Data;
import lombok.SneakyThrows;
import net.neferett.coreengine.Utils.ClassSerializer;
import net.neferett.httpserver.api.Routing.Route;
import net.neferett.httpserver.api.Routing.RoutingProperties;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONObject;

@Route(name = "/test")
public class Test extends RoutingProperties {

    @Data
    class TotoData {
        private final String test1;

        private final int x;
        private final int y;
    }

    @Override
    @SneakyThrows
    public JSONObject routeAction(HttpExchange t) {
        System.out.println(SerializationUtils.convertStreamToString(t.getRequestBody()));

        return ClassSerializer.serialize(new TotoData("toto", 1, 2));
    }
}
