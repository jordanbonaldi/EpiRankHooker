package net.neferett.hooker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.neferett.hooker.Manager.StudentsManager;
import net.neferett.redisapi.Annotations.Redis;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Redis(db = 8, folder = true)
public class City {
    private @NonNull String code;
    private @NonNull String title;
    private @NonNull int students;

    @JsonIgnore
    private StudentsManager manager = new StudentsManager(this);
}
