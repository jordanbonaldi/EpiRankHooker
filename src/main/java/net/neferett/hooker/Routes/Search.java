package net.neferett.hooker.Routes;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.SearchEngine.Engine;
import net.neferett.hooker.Utils.JSONObjectCustom;
import net.neferett.hooker.Utils.JSONUtils;
import net.neferett.httpserver.api.Routing.Route;
import net.neferett.httpserver.api.Routing.RoutingProperties;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONObject;

import java.util.List;

@Route(name = "/search")
public class Search extends RoutingProperties {

    @Override
    @SneakyThrows
    public JSONObject routeAction(HttpExchange t) {
        JSONObjectCustom obj = new JSONObjectCustom(new JSONObject(SerializationUtils.convertStreamToString(t.getRequestBody())));

        String sentence = obj.getString("value");

        if (sentence == null)
            return new JSONObject().put("status", false);

        Engine engine = new Engine();

        engine.loadStudents(Hooker.getInstance().getManager().getStudents());

        List<Student> students = engine.searchAll(sentence);

        return JSONUtils.studentListToJSON(students);
    }
}
