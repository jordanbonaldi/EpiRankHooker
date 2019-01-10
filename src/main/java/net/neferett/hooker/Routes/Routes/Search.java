package net.neferett.hooker.Routes.Routes;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Manager.CityManager;
import net.neferett.hooker.Routes.Route;
import net.neferett.hooker.Routes.RoutingProperties;
import net.neferett.hooker.SearchEngine.Engine;
import net.neferett.hooker.Utils.JSONObjectCustom;
import net.neferett.hooker.Utils.JSONUtils;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@Route(name = "/search")
public class Search extends RoutingProperties {

    @Override
    @SneakyThrows
    public JSONObject commandProcess(HttpExchange t) {
        JSONObjectCustom obj = new JSONObjectCustom(new JSONObject(SerializationUtils.convertStreamToString(t.getRequestBody())));

        String sentence = obj.getString("value");

        if (sentence == null)
            return new JSONObject().put("status", false);

        Engine engine = new Engine();

        engine.loadStudents(Hooker.getInstance().getManager().getStudents());

        List<Student> students = engine.searchAll(sentence);

        System.out.println("toto2");

        return JSONUtils.studentListToJSON(students);
    }
}
