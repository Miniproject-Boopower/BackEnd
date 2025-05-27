package likelion.mini.team1.domain.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import likelion.mini.team1.domain.enums.ScheduleEnums;
import lombok.Data;

@Data
public class CreateScheduleRequest {
	private String name;
	private ScheduleEnums scheduleEnums;
	private String studentNumber;
	private LocalDate date;
}
