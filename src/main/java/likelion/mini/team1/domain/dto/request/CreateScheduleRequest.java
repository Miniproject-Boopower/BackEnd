package likelion.mini.team1.domain.dto.request;

import likelion.mini.team1.domain.enums.ScheduleEnums;
import lombok.Data;

@Data
public class CreateScheduleRequest {
	private String name;
	private ScheduleEnums scheduleEnums;
	private String studentNumber;
}
