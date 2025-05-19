package likelion.mini.team1.domain.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddNonRegularCourseRequest {
	private String studentNumber;
	private List<String> courseName;
}
