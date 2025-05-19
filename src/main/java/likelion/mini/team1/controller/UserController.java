package likelion.mini.team1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.domain.dto.ApiResponse;
import likelion.mini.team1.domain.dto.request.SignUpRequest;
import likelion.mini.team1.domain.dto.response.CourseResponse;
import likelion.mini.team1.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

	@Autowired
	private final UserService userService;

	@GetMapping("/test")
	public String testAPI() {
		userService.test();
		return "test API 입니다1234";
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws Exception {
		userService.signUp(signUpRequest);
		ApiResponse<Void> response = ApiResponse.<Void>builder()
			.status(200)
			.message("회원가입이 성공하였습니다!")
			.data(null)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/course")
	public ResponseEntity<?> getCourse(@RequestParam String studentNumber) {
		List<CourseResponse> course = userService.getCourse(studentNumber);
		return ResponseEntity.ok(course);
	}

	@GetMapping("/assignment")
	public ResponseEntity<?> getAssignment(@RequestParam String studentNumber) {
		return ResponseEntity.ok(userService.getAssignments(studentNumber));
	}

}
