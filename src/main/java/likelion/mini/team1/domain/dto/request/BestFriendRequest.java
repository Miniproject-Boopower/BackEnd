package likelion.mini.team1.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BestFriendRequest {
	private String studentNumber;
	private String friendStudentNumber;
	private boolean isBestFriend;
}
