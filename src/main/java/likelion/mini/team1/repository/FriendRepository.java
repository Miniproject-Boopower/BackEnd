package likelion.mini.team1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import likelion.mini.team1.domain.entity.Friend;
import likelion.mini.team1.domain.entity.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findAllByUser(User user);
}
