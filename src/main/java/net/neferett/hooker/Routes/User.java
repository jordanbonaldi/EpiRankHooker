package net.neferett.hooker.Routes;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Manager.CityManager;
import net.neferett.hooker.Utils.JSONObjectCustom;
import net.neferett.hooker.Utils.JSONUtils;
import net.neferett.httpserver.api.Routing.Route;
import net.neferett.httpserver.api.Routing.RoutingProperties;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONObject;

import java.util.stream.Collectors;

@Route(name = "/user")
public class User extends RoutingProperties {

    private String login;
    private String name;
    private int promotion;
    private String city;

    private boolean findStudent(Student student) {
        return null != login && student.getLogin().contains(this.login) ||
                null != name && student.getTitle().contains(this.name) ||
                this.promotion != 0 && student.getPromo() == this.promotion||
                city != null && student.getLocation().contains(this.city);
    }

    @Override
    @SneakyThrows
    public JSONObject routeAction(HttpExchange t) {
        JSONObjectCustom obj = new JSONObjectCustom(new JSONObject(SerializationUtils.convertStreamToString(t.getRequestBody())));

        this.login = obj.getString("login");
        this.name = obj.getString("name");
        this.city = obj.getString("city");
        this.promotion = obj.getInt("promotion");

        if (this.login == null && this.name == null && this.city == null && this.promotion == 0)
            return new JSONObject().put("status", false);

        CityManager cityManager = Hooker.getInstance().getManager();

        return JSONUtils.studentListToJSON(cityManager.getStudents().stream().filter(this::findStudent).collect(Collectors.toList()));
    }
}
