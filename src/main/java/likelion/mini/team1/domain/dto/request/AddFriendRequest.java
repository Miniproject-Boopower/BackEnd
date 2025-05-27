package likelion.mini.team1.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddFriendRequest {
	private String studentNumber;  // 내 학번 (로그인 정보로 받아도 됨)
	private String friendStudentNumber; // 추가할 친구 학번
}
