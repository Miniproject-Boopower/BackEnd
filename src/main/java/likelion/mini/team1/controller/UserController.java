package likelion.mini.team1.controller;

import java.util.List;

import likelion.mini.team1.domain.dto.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import likelion.mini.team1.domain.dto.ApiResponse;
import likelion.mini.team1.domain.dto.response.AssignmentDdayResponse;
import likelion.mini.team1.domain.dto.response.AssignmentResponse;
import likelion.mini.team1.domain.dto.response.CourseResponse;
import likelion.mini.team1.domain.dto.response.FirstSemesterActivityResponse;
import likelion.mini.team1.domain.dto.response.MypageResponse;
import likelion.mini.team1.domain.dto.response.ScheduleResponse;
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
		// userService.test();
		return "test API 입니다12341";
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

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		boolean success = userService.login(loginRequest.getStudentNumber(), loginRequest.getPassword());

		if (success) {
			ApiResponse<String> response = ApiResponse.<String>builder()
				.status(200)
				.message("로그인 성공!")
				.data("유저 정보 or 토큰 자리")
				.build();
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<String> response = ApiResponse.<String>builder()
				.status(401)
				.message("로그인 실패. 학번 또는 비밀번호가 올바르지 않습니다.")
				.data(null)
				.build();
			return ResponseEntity.status(401).body(response);
		}
	}

	@GetMapping("/course")
	public ResponseEntity<?> getCourse(@RequestParam String studentNumber) {
		List<CourseResponse> course = userService.getCourse(studentNumber);
		ApiResponse<List<CourseResponse>> response = ApiResponse.<List<CourseResponse>>builder()
			.status(200)
			.message("과목을 가져오는데 성공하였습니다!")
			.data(course)
			.build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/assignment")
	public ResponseEntity<?> getAssignment(@RequestParam String studentNumber) {
		List<AssignmentResponse> assignments = userService.getAssignments(studentNumber);
		ApiResponse<List<AssignmentResponse>> response = ApiResponse.<List<AssignmentResponse>>builder()
			.status(200)
			.message("과제를 가져오는데 성공하였습니다!")
			.data(assignments)
			.build();
		return ResponseEntity.ok(response);
	}

	@PostMapping("/course/non-regular")
	public ResponseEntity<?> addNonRegularCourse(@RequestBody AddNonRegularCourseRequest addNonRegularCourseRequest) {
		userService.addNonRegularCourse(addNonRegularCourseRequest);
		ApiResponse<Void> response = ApiResponse.<Void>builder()
			.status(200)
			.message("비정규 과목을 모두 저장하였습니다!!")
			.data(null)
			.build();
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/activity1/delete")
	public ResponseEntity<?> deleteActivity(@RequestParam String studentNumber, @RequestParam Long activityId) {
		userService.deleteActivity1(studentNumber, activityId);
		return ResponseEntity.ok("1학기 활동이 삭제되었습니다.");
	}

	@GetMapping("/main/today/assignment")
	public ResponseEntity<?> getTodayAssignment(@RequestParam String studentNumber) {
		List<AssignmentResponse> todayAssignment = userService.getTodayAssignment(studentNumber);
		ApiResponse<List<AssignmentResponse>> response = ApiResponse.<List<AssignmentResponse>>builder()
			.status(200)
			.message("오늘 해야할 과제를 조회완료 하였습니다!!")
			.data(todayAssignment)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/main/today/schedule")
	public ResponseEntity<?> getTodaySchedule(@RequestParam String studentNumber) {
		List<ScheduleResponse> todaySchedule = userService.getTodaySchedule(studentNumber);
		ApiResponse<List<ScheduleResponse>> response = ApiResponse.<List<ScheduleResponse>>builder()
			.status(200)
			.message("오늘 해야할 할일을 조회완료 하였습니다!!")
			.data(todaySchedule)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/main/d-day")
	public ResponseEntity<?> getDday(@RequestParam String studentNumber) {
		List<AssignmentDdayResponse> assignmentDday = userService.getAssignmentDday(studentNumber);
		ApiResponse<List<AssignmentDdayResponse>> response = ApiResponse.<List<AssignmentDdayResponse>>builder()
			.status(200)
			.message("오늘 해야할 과제를 조회완료 하였습니다!!")
			.data(assignmentDday)
			.build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(@RequestParam String studentNumber) {
		MypageResponse profile = userService.getProfile(studentNumber);
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/activity1/share")
	public ResponseEntity<?> shareFirstSemesterActivities(@RequestParam String studentNumber) {
		List<FirstSemesterActivityResponse> activities = userService.getFirstSemesterActivity(studentNumber);
		return ResponseEntity.ok(activities);
	}

	@PutMapping("/fix-activity1")
	public ResponseEntity<?> fixActivity(@RequestBody FixActivityRequest fixActivityRequest) {
		userService.fixActivity(fixActivityRequest);
		return ResponseEntity.ok("수정이 완료되었습니다.");
	}

	@GetMapping("/check-activity1")
	public ResponseEntity<?> checkFirstSemesterActivities(@RequestParam String studentNumber) {
		List<FirstSemesterActivityResponse> activities = userService.getFirstSemesterActivity(studentNumber);
		return ResponseEntity.ok(activities);
	}

	@PostMapping("/createActivity")
	public ResponseEntity<?> createActivity(@RequestBody CreateActivityResponse createActivityResponse) {
		userService.createActivity(createActivityResponse);
		return null;
	}

	@PostMapping("/createSchedule")
	public ResponseEntity<?> createSchedule(@RequestBody CreateScheduleRequest createScheduleRequest) {
		userService.createSchedule(createScheduleRequest);
		ApiResponse<List<AssignmentDdayResponse>> response = ApiResponse.<List<AssignmentDdayResponse>>builder()
			.status(200)
			.message("오늘 해야할 과제를 조회완료1 하였습니다!!")
			.data(null)
			.build();
		return ResponseEntity.ok(response);
	}
}
