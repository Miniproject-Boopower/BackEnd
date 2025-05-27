package likelion.mini.team1.domain.dto.request;

import likelion.mini.team1.domain.enums.Semester;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateActivityResponse {
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private Semester semester;
    private String studentNumber;
}
