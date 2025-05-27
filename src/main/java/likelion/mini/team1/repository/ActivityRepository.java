package likelion.mini.team1.repository;

import likelion.mini.team1.domain.entity.Activity;
import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.domain.enums.Semester;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByUserAndSemester(User user, Semester semester);
    Optional<Activity> findByIdAndUser(Long id, User user);
}
