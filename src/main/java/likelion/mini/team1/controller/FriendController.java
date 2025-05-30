package likelion.mini.team1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.domain.dto.ApiResponse;
import likelion.mini.team1.domain.dto.request.AddFriendRequest;
import likelion.mini.team1.domain.dto.request.BestFriendRequest;
import likelion.mini.team1.domain.dto.request.FriendDeleteRequest;
import likelion.mini.team1.domain.dto.request.FriendRelationRequest;
import likelion.mini.team1.domain.dto.request.StudentNumberRequest;
import likelion.mini.team1.domain.dto.response.FriendResponse;
import likelion.mini.team1.service.FriendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FriendController {

	private final FriendService friendService;

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<?>> getFriendList(@RequestParam String request) {
		try {
			List<FriendResponse> friends = friendService.getFriendsByStudentNumber(request);
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

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<?>> addFriend(@RequestBody AddFriendRequest request) {
		try {
			friendService.addFriend(request);
			return ResponseEntity.ok(new ApiResponse<>(200, "친구 요청이 전송되었습니다.", null));
		} catch (RuntimeException e) {
			String message = e.getMessage();
			int status = 400;
			if (message.equals("해당 학번의 유저가 존재하지 않습니다.") ||
				message.equals("해당 정보로 가입한 사용자가 존재하지 않습니다.")) {
				status = 404;
			} else if (message.equals("이미 친구로 등록되어 있습니다.")) {
				status = 409;
			}

			return ResponseEntity.status(status).body(new ApiResponse<>(status, message, null));
		}
	}

	@PostMapping("/relation")
	public ResponseEntity<ApiResponse> setFriendRelation(@RequestBody FriendRelationRequest request) {
		try {
			friendService.setFriendRelation(request);
			return ResponseEntity.ok(new ApiResponse(200, "친구 구분이 등록되었습니다.", null));
		} catch (RuntimeException e) {
			return ResponseEntity.status(404)
				.body(new ApiResponse(404, e.getMessage(), null));
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteFriend(@RequestBody FriendDeleteRequest request) {
		try {
			friendService.deleteFriend(request);
			return ResponseEntity.ok(new ApiResponse(200, "친구 삭제가 완료되었습니다.", null));
		} catch (RuntimeException e) {
			String message = e.getMessage();
			int status = 400;
			if (message.equals("삭제할 친구가 존재하지 않습니다.") || message.contains("가입한 사용자가")) {
				status = 404;
			}
			return ResponseEntity.status(status).body(new ApiResponse(status, message, null));
		}
	}

}
