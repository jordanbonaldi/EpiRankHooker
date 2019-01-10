package net.neferett.hooker.Utils;

import lombok.experimental.UtilityClass;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@UtilityClass
public class JSONUtils {

    public static JSONObject studentListToJSON(List<Student> studentList) {
        JSONObject _obj = new JSONObject();
        JSONArray array = new JSONArray();

        studentList.forEach(e ->
            Hooker.getInstance().getManager().getCities().forEach(c -> {
               if (c.getCode().equals(e.getLocation()))
                   e.setCity(c);
            })
        );

        studentList.stream().map(SerializationUtils::serialize).forEach(array::put);

        return  _obj.put("students", array);
    }

}
