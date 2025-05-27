package likelion.mini.team1.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FirstSemesterActivityResponse {
    private Long id;
    private String activityName;
    private String description;
    private LocalDateTime date;
}