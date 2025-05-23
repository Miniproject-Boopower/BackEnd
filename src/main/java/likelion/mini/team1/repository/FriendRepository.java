package likelion.mini.team1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import likelion.mini.team1.domain.entity.Friend;
import likelion.mini.team1.domain.entity.User;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findAllByUser(User user);
	Optional<Friend> findByUserAndFriend(User user, User friend);
}
