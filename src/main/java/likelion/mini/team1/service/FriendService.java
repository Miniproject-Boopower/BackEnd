package likelion.mini.team1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import likelion.mini.team1.domain.dto.response.FriendResponse;
import likelion.mini.team1.domain.entity.Friend;
import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.repository.FriendRepository;
import likelion.mini.team1.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final FriendRepository friendRepository;
	private final UserRepository userRepository;

	public List<FriendResponse> getFriendsByStudentNumber(String studentNumber) {
		User user = userRepository.findByStudentNumber(studentNumber)
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));

		List<Friend> friends = friendRepository.findAllByUser(user);

		if (friends.isEmpty()) {
			throw new RuntimeException("조회 할 친구가 존재하지 않습니다.");
		}

		return friends.stream()
			.map(friend -> FriendResponse.builder()
				.studentNumber(friend.getFriend().getStudentNumber())
				.name(friend.getFriend().getName())
				.major(friend.getFriend().getMajor())
				.favorite(friend.isFavorite())
				.build())
			.collect(Collectors.toList());
	}
}
