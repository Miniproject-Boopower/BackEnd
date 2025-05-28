package likelion.mini.team1.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import likelion.mini.team1.domain.entity.Schedule;
import likelion.mini.team1.domain.entity.User;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	List<Schedule> findAllByUserAndDate(User user, LocalDate date);
}
