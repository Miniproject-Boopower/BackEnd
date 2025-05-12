package likelion.mini.team1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import likelion.mini.team1.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsByStudentNumber(String studentNumber);

	Optional<User> findByStudentNumber(String studentNumber);
}
