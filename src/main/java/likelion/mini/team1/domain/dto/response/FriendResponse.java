package likelion.mini.team1.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendResponse {
	private String studentNumber;
	private String name;
	private String major;
	private boolean favorite;  // 하트 여부
}
