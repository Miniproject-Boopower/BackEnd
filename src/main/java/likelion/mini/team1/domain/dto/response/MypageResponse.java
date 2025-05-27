package likelion.mini.team1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String name;
    private String studentNumber;
    private String major;
    private String minor;
}