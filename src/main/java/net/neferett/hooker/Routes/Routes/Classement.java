package net.neferett.hooker.Routes.Routes;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Manager.CityManager;
import net.neferett.hooker.Routes.Route;
import net.neferett.hooker.Routes.RoutingProperties;
import net.neferett.hooker.Utils.JSONObjectCustom;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(name = "/classement")
public class Classement extends RoutingProperties {

    private int promotion;
    private String city;

    private boolean findStudent(Student student) {
        return this.promotion != 0 && student.getPromo() == this.promotion ||
                this.city != null && student.getLocation().contains(this.city);
    }

    @Override
    @SneakyThrows
    public JSONObject commandProcess(HttpExchange t) {
        JSONObjectCustom obj = new JSONObjectCustom(new JSONObject(SerializationUtils.convertStreamToString(t.getRequestBody())));

        int amount = obj.getInt("amount");
        this.city = obj.getString("city");
        this.promotion = obj.getInt("promotion");

        if (amount == 0 && this.promotion == 0 && this.city == null)
            return new JSONObject().put("status", false);

        CityManager cityManager = Hooker.getInstance().getManager();

        JSONObject _obj = new JSONObject();
        JSONArray array = new JSONArray();

        List<Student> student = new ArrayList<>();

        cityManager.getCities().stream().map(e -> e.getManager().getStudents().stream().filter(this::findStudent).collect(Collectors.toList())).filter(e -> !e.isEmpty()).forEach(student::addAll);

        student = student.stream().sorted((a, b) -> Double.compare(Double.valueOf(b.getGpa().get(0).getGpa()), Double.valueOf(a.getGpa().get(0).getGpa()))).collect(Collectors.toList());

        if (amount != 0)
            student = student.stream().limit(amount).collect(Collectors.toList());

        student.stream().map(SerializationUtils::serialize).forEach(array::put);

        return  _obj.put("students", array);
    }
}
