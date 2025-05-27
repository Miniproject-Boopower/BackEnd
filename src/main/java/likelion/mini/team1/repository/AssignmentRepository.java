package likelion.mini.team1.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import likelion.mini.team1.domain.entity.Assignment;
import likelion.mini.team1.domain.entity.User;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	List<Assignment> findAllByUser(User user);

	List<Assignment> findAllByUserAndDeadlineAfterAndDeadlineBefore(User user, LocalDateTime startDate,
		LocalDateTime endDate);

	List<Assignment> findAllByUserAndDeadlineAfter(User user, LocalDateTime dateTime);
}
