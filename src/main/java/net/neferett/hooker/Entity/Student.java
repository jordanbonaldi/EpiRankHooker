package net.neferett.hooker.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.neferett.hooker.Entity.StudentInfos.StudentGPA;
import net.neferett.redisapi.Annotations.Redis;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Redis(db = 8, folder = true)
public class Student {

    private @NonNull String login;
    private @NonNull String title;

    private @NonNull String firstname;
    private @NonNull String lastname;

    private @NonNull String picture;
    private @NonNull int scolaryear;
    private @NonNull int promo;
    private @NonNull String semester;
    private @NonNull String location;

    private @NonNull String course_code;
    private @NonNull int studentyear;
    private @NonNull int credits;
    private @NonNull List<StudentGPA> gpa;
}
