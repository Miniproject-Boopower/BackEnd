package likelion.mini.team1.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendDeleteRequest {
	private String studentNumber;         // 본인 학번
	private String friendStudentNumber;   // 삭제할 친구 학번
}
