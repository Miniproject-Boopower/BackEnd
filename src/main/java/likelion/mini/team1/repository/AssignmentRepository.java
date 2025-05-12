package likelion.mini.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import likelion.mini.team1.domain.entity.Assignment;
import likelion.mini.team1.domain.entity.User;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	List<Assignment> findAllByUser(User user);
}
