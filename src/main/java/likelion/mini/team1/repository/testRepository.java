package likelion.mini.team1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import likelion.mini.team1.domain.entity.User;

public interface testRepository extends JpaRepository<User, Long> {
}
