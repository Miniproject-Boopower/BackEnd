package likelion.mini.team1.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import likelion.mini.team1.domain.enums.AssignmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_course_id")
	private UserCourse userCourse;

	private String title;  // 과제 제목

	private LocalDateTime deadline;  // 마감일 (ex: 2025-05-11 11:30)

	@Enumerated(EnumType.STRING)
	private AssignmentStatus status;  // ex: 미제출, 제출 등

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public Assignment(UserCourse userCourse, String title, LocalDateTime deadline, AssignmentStatus status, User user) {
		this.userCourse = userCourse;
		this.title = title;
		this.deadline = deadline;
		this.status = status;
		this.user = user;
		this.createdAt = LocalDateTime.now();
	}
}

