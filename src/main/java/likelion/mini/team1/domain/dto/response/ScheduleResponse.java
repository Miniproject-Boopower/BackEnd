package likelion.mini.team1.domain.dto.response;

import java.time.LocalDateTime;

import likelion.mini.team1.domain.enums.ScheduleEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduleResponse {
	private Long id;
	private String name;
	private ScheduleEnums scheduleEnums;
	private LocalDateTime date;
	private String userName;

	@Builder
	public ScheduleResponse(Long id, String name, ScheduleEnums scheduleEnums, LocalDateTime date, String userName) {
		this.id = id;
		this.name = name;
		this.scheduleEnums = scheduleEnums;
		this.date = date;
		this.userName = userName;
	}
}
