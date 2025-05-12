package likelion.mini.team1.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import likelion.mini.team1.domain.enums.CourseType;
import likelion.mini.team1.domain.enums.ImportanceLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserCourse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String courseName;  // 예: "자료구조", "인공지능"

	@Enumerated(EnumType.STRING)
	private ImportanceLevel importanceLevel;

	@Enumerated(EnumType.STRING)
	private CourseType courseType;

	private LocalDateTime createdAt;

	@Builder
	public UserCourse(User user, String courseName, CourseType courseType) {
		this.user = user;
		this.courseName = courseName;
		this.courseType = courseType;
		this.createdAt = LocalDateTime.now();
	}
}

