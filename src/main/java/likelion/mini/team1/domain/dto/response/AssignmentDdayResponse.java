package likelion.mini.team1.domain.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentDdayResponse {
	private String assignmentName;
	private String leftDay;

	@Builder
	public AssignmentDdayResponse(String assignmentName, String leftDay) {
		this.assignmentName = assignmentName;
		this.leftDay = leftDay;
	}
}
