package likelion.mini.team1.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import likelion.mini.team1.domain.enums.ScheduleEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class Schedule {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private ScheduleEnums scheduleEnums;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public Schedule(String name, ScheduleEnums scheduleEnums, User user) {
		this.name = name;
		this.scheduleEnums = scheduleEnums;
		this.user = user;
	}
}
