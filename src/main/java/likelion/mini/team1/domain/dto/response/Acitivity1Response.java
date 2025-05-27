package likelion.mini.team1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FirstSemesterActivityResponse {
    private String activityName;
    private String description;
    private String date;
}