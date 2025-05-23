package likelion.mini.team1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.domain.dto.ApiResponse;
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
}
