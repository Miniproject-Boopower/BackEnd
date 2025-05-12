package likelion.mini.team1.domain.dto.response;

import java.time.LocalDateTime;

import likelion.mini.team1.domain.enums.CourseType;
import likelion.mini.team1.domain.enums.ImportanceLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseResponse {
	private String courseName;
	private ImportanceLevel importanceLevel;
	private CourseType courseType;

	@Builder
	public CourseResponse(String courseName, ImportanceLevel importanceLevel, CourseType courseType) {
		this.courseName = courseName;
		this.importanceLevel = importanceLevel;
		this.courseType = courseType;
	}
}
