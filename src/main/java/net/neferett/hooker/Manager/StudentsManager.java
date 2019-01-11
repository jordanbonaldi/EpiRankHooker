package net.neferett.hooker.Manager;

import lombok.Data;
import lombok.experimental.Delegate;
import net.neferett.hooker.API.HookerAPI;
import net.neferett.hooker.Entity.City;
import net.neferett.hooker.Entity.Student;
import net.neferett.hooker.Hooker;
import net.neferett.redisapi.RedisAPI;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Data
public class StudentsManager {

    private final City city;

    private boolean done = false;

    private HookerAPI api;

    @Delegate
    private List<Student> students = new ArrayList<>();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(7);

    private String getRequest(int year, int promotion, int offset)
    {
        return "/user/filter/user?format=json&location=" + this.city.getCode() + "&year=" + year + "&course=bachelor/classic&promo=tek" + promotion + "&offset=" + offset;
    }

    private Student getStudent(String login) {
        return (Student) SerializationUtils.deSerialize(Student.class, this.api.call("/user/" + login));
    }

    private void loadStudentsByPromotion(int year, int promotion, int offset) {
        int total;
        JSONObject students = new JSONObject(this.api.call(this.getRequest(year, promotion, offset)));

        System.out.println(this.getRequest(year, promotion, offset));

        total = students.getInt("total");
        if (0 == total)
            return;

        JSONArray array = students.getJSONArray("items");

        executor.submit(() -> {
            RedisAPI api = Hooker.getInstance().getNewRedisInstance();

            array.forEach(e -> {
                Student student = this.getStudent(new JSONObject(e.toString()).getString("login"));

                System.out.println(student.getLocation() + " - " + student.getLogin());
                api.serialize(student, student.getLogin());
            });

            api.close();
        });

        offset += array.length();

        if (offset < total)
            this.loadStudentsByPromotion(year, promotion, offset);
    }

    public void loadAllStudents() {
        if (null == this.api)
            return;
        for (int j = 0; j < 4; j++) {
            for (int i = 1; i < 4; i++) {
                int finalI = i;
                int finalJ = j;
                executor.submit(() ->
                        this.loadStudentsByPromotion(2018 - finalJ, finalI, 0)
                );
            }
        }
    }
}
