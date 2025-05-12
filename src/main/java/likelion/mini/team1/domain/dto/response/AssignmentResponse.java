package likelion.mini.team1.domain.dto.response;

import java.time.LocalDateTime;

import likelion.mini.team1.domain.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentResponse {
	private String assignmentName;
	private String subjectName;
	private LocalDateTime deadline;
	private AssignmentStatus status;

	@Builder
	public AssignmentResponse(String assignmentName, String subjectName, LocalDateTime deadline,
		AssignmentStatus status) {
		this.assignmentName = assignmentName;
		this.subjectName = subjectName;
		this.deadline = deadline;
		this.status = status;
	}
}
