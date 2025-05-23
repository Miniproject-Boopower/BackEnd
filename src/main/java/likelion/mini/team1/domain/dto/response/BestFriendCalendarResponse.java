package likelion.mini.team1.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BestFriendCalendarResponse {
	private Long id;
	private String title;
	private String type;
	private String memo;
}
