package net.neferett.hooker.Manager;

import lombok.Data;
import net.neferett.hooker.API.HookerAPI;
import net.neferett.hooker.Entity.City;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Utils.Constants;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CityManager {

    private final HookerAPI api;

    List<City> cities = new ArrayList<>();

    List<Student> students = new ArrayList<>();

    public void loadCities() {
        JSONArray array = new JSONArray(this.api.call(Constants.cities));
        array.forEach(e -> {
            City city = (City) SerializationUtils.deSerialize(City.class, e.toString());

                {
                    city.getManager().setApi(this.api);
                    city.getManager().loadAllStudents();
                }

            Hooker.getInstance().getRedisAPI().serialize(city, city.getCode());
        });
    }

    public void setStudentByCity() {

        List<City> city = Hooker.getInstance().getRedisAPI().contains(City.class).values().stream().map(e -> ((City)e)).collect(Collectors.toList());
        List<Student> students = Hooker.getInstance().getRedisAPI().contains(Student.class).values().stream().map(e -> ((Student)e)).collect(Collectors.toList());

        students.forEach(e ->
            city.forEach(c -> {
                if (e.getLocation().contains(c.getCode())) {
                    c.getManager().add(e);
                }
            })
        );

        this.students = students;
        this.cities = city;
    }
}
