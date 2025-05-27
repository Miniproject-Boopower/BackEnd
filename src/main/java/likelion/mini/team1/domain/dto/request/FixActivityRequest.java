package likelion.mini.team1.domain.dto.request;

import likelion.mini.team1.domain.enums.Semester;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FixActivityRequest {
    private String studentNumber;
    private Long activityId;
    private String activityName;
    private String activityDescription;
    private LocalDateTime activityDate;
    private Semester semester;
}
