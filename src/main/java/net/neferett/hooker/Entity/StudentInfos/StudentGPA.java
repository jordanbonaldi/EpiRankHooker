package net.neferett.hooker.Entity.StudentInfos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentGPA {
    private @NonNull String gpa;
    private @NonNull String cycle;
}
