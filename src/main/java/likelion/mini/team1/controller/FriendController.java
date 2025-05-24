package likelion.mini.team1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.domain.dto.ApiResponse;
import likelion.mini.team1.domain.dto.request.BestFriendRequest;
import likelion.mini.team1.domain.dto.request.StudentNumberRequest;
import likelion.mini.team1.domain.dto.response.FriendResponse;
import likelion.mini.team1.service.FriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;

	@PostMapping("/list")
	public ResponseEntity<ApiResponse<?>> getFriendList(@RequestBody StudentNumberRequest request) {
		try {
			List<FriendResponse> friends = friendService.getFriendsByStudentNumber(request.getStudentNumber());
			return ResponseEntity.ok(
				ApiResponse.<List<FriendResponse>>builder()
					.status(200)
					.message("성공")
					.data(friends)
					.build()
			);
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(
				ApiResponse.<String>builder()
					.status(404)
					.message(e.getMessage())
					.data(null)
					.build()
			);
		}
	}

	@PostMapping("/best")
	public ResponseEntity<ApiResponse> registerBestFriend(@RequestBody BestFriendRequest request) {
		try {
			friendService.registerBestFriend(request);
			return ResponseEntity.ok(new ApiResponse(200, "짱친이 등록되었습니다.", "짱친 등록 완료"));
		} catch (RuntimeException e) {
			String message = e.getMessage();
			int status = 400;
			if (message.equals("친구 정보가 존재하지 않습니다.")) status = 404;
			else if (message.equals("이미 짱친으로 등록되어 있는 친구입니다.")) status = 409;

			return ResponseEntity.status(status)
				.body(new ApiResponse(status, message, null));
		}
	}

	@DeleteMapping("/unbest")
	public ResponseEntity<ApiResponse> unregisterBestFriend(@RequestBody BestFriendRequest request) {
		try {
			friendService.unregisterBestFriend(request);
			return ResponseEntity.ok(new ApiResponse(200, "짱친 관계가 해제되었습니다.", null));
		} catch (RuntimeException e) {
			String message = e.getMessage();
			int status = 400;
			if (message.equals("짱친 정보가 존재하지 않습니다.")) status = 404;
			else if (message.equals("짱친으로 등록된 친구가 아닙니다.")) status = 409;

			return ResponseEntity.status(status).body(new ApiResponse(status, message, null));
		}
	}

}
