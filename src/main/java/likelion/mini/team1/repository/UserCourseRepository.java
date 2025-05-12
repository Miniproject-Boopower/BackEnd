package likelion.mini.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.domain.entity.UserCourse;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
	Boolean existsByCourseNameAndUser(String courseName, User user);

	Optional<UserCourse> findByCourseNameAndUser(String courseName, User user);

	List<UserCourse> findAllByUser(User user);
}
