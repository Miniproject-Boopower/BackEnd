package likelion.mini.team1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


import org.springframework.stereotype.Service;

import likelion.mini.team1.domain.dto.request.AddFriendRequest;
import likelion.mini.team1.domain.dto.request.FriendRelationRequest;
import lombok.RequiredArgsConstructor;
import likelion.mini.team1.domain.dto.request.BestFriendRequest;
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

	@Transactional
	public void registerBestFriend(BestFriendRequest request) {
		String studentNumber = request.getStudentNumber();
		String friendStudentNumber = request.getFriendStudentNumber();
		boolean isBestFriend = request.isBestFriend();
		User user = userRepository.findByStudentNumber(request.getStudentNumber())
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));

		User friendUser = userRepository.findByStudentNumber(request.getFriendStudentNumber())
			.orElseThrow(() -> new RuntimeException("친구 정보가 존재하지 않습니다."));

		Friend existingFriend = friendRepository.findByUserAndFriend(user, friendUser).orElse(null);

		if (existingFriend != null && existingFriend.isFavorite()) {
			throw new RuntimeException("이미 짱친으로 등록되어 있는 친구입니다.");
		}

		// 친구 저장 or 수정
		if (existingFriend == null) {
			Friend newFriend = Friend.builder()
				.user(user)
				.friend(friendUser)
				.favorite(request.isBestFriend())
				.build();
			friendRepository.save(newFriend);
		} else {
			existingFriend.setFavorite(request.isBestFriend());
			friendRepository.save(existingFriend);
		}
	}

	@Transactional
	public void unregisterBestFriend(BestFriendRequest request) {
		User user = userRepository.findByStudentNumber(request.getStudentNumber())
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));

		User friendUser = userRepository.findByStudentNumber(request.getFriendStudentNumber())
			.orElseThrow(() -> new RuntimeException("친구 정보가 존재하지 않습니다."));

		Friend existingFriend = friendRepository.findByUserAndFriend(user, friendUser)
			.orElseThrow(() -> new RuntimeException("짱친 정보가 존재하지 않습니다."));

		if (!existingFriend.isFavorite()) {
			throw new RuntimeException("짱친으로 등록된 친구가 아닙니다.");
		}

		existingFriend.setFavorite(false);
		friendRepository.save(existingFriend);
	}

	@Transactional
	public void addFriend(AddFriendRequest request) {
		User user = userRepository.findByStudentNumber(request.getStudentNumber())
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));

		User friendUser = userRepository.findByStudentNumber(request.getFriendStudentNumber())
			.orElseThrow(() -> new RuntimeException("해당 정보로 가입한 사용자가 존재하지 않습니다."));

		if (friendRepository.findByUserAndFriend(user, friendUser).isPresent()) {
			throw new RuntimeException("이미 친구로 등록되어 있습니다.");
		}

		Friend newFriend = Friend.builder()
			.user(user)
			.friend(friendUser)
			.favorite(false)  // 기본값 false
			.build();

		friendRepository.save(newFriend);
	}

	@Transactional
	public void setFriendRelation(FriendRelationRequest request) {
		User user = userRepository.findByStudentNumber(request.getStudentNumber())
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));

		User friendUser = userRepository.findByStudentNumber(request.getFriendStudentNumber())
			.orElseThrow(() -> new RuntimeException("친구 정보가 존재하지 않습니다."));

		Friend friend = friendRepository.findByUserAndFriend(user, friendUser)
			.orElseThrow(() -> new RuntimeException("친구 관계가 존재하지 않습니다."));

		friend.setRelationLevel(request.getRelationLevel());
		friendRepository.save(friend);
	}
}
