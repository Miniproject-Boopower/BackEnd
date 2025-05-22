package likelion.mini.team1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import likelion.mini.team1.domain.dto.request.AddNonRegularCourseRequest;
import likelion.mini.team1.domain.dto.request.SignUpRequest;
import likelion.mini.team1.domain.dto.response.AssignmentResponse;
import likelion.mini.team1.domain.dto.response.CourseResponse;
import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.domain.entity.UserCourse;
import likelion.mini.team1.domain.enums.CourseType;
import likelion.mini.team1.repository.AssignmentRepository;
import likelion.mini.team1.repository.UserCourseRepository;
import likelion.mini.team1.repository.UserRepository;
import likelion.mini.team1.util.AESUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final UserCourseRepository userCourseRepository;
	@Autowired
	private final AssignmentRepository assignmentRepository;

	public void test() {
		throw new RuntimeException("sad");
	}

	public void signUp(SignUpRequest signUpRequest) {
		if (userRepository.existsByStudentNumber(signUpRequest.getStudentNumber())) {
			throw new RuntimeException("이미 존재하는 유저 입니다.");
		}
		User newUser = User.builder()
			.studentNumber(signUpRequest.getStudentNumber())
			.name(signUpRequest.getName())
			.major(signUpRequest.getMajor())
			.password(AESUtil.encrypt(signUpRequest.getPassword()))
			.build();
		userRepository.save(newUser);
	}

	public List<CourseResponse> getCourse(String studentNum) {
		User user = userRepository.findByStudentNumber(studentNum).orElseThrow(() -> new RuntimeException("유저가 없습니다."));
		List<UserCourse> courses = userCourseRepository.findAllByUser(user);
		return courses.stream().map(course -> CourseResponse.builder()
			.courseType(course.getCourseType())
			.courseName(course.getCourseName())
			.importanceLevel(course.getImportanceLevel())
			.build()).toList();
	}

	public List<AssignmentResponse> getAssignments(String studentNum) {
		User user = userRepository.findByStudentNumber(studentNum).orElseThrow(() -> new RuntimeException("유저가 없습니다."));
		return assignmentRepository.findAllByUser(user).stream().map(((assignment) -> AssignmentResponse.builder()
			.assignmentName(assignment.getTitle())
			.deadline(assignment.getDeadline())
			.subjectName(assignment.getUserCourse().getCourseName())
			.status(assignment.getStatus()).build()
		)).toList();
	}

	public boolean login(String studentNumber, String password) {
		User user = findUserByStudentNumber(studentNumber);

		String encryptedInputPassword = AESUtil.encrypt(password);

		return user.getPassword().equals(encryptedInputPassword);
	}

	public void addNonRegularCourse(AddNonRegularCourseRequest addNonRegularCourseRequest) {
		User user = findUserByStudentNumber(addNonRegularCourseRequest.getStudentNumber());
		for (String courseName : addNonRegularCourseRequest.getCourseName()) {
			UserCourse newUserCourse = UserCourse.builder()
				.courseType(CourseType.NON_REGULAR)
				.user(user)
				.courseName(courseName)
				.build();
			userCourseRepository.save(newUserCourse);
		}
	}

	public User findUserByStudentNumber(String studentNumber) {
		return userRepository.findByStudentNumber(studentNumber)
			.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));
	}

	public List<FirstSemesterActivitiesResponse> getFirstSemesterActivities(String studentNumber) {
		User user = userRepository.findByStudentNumber(studentNumber)
				.orElseThrow(() -> new RuntimeException("해당 학번의 유저가 존재하지 않습니다."));
		List<FirstSemesterActivity> activities = activityRepository.findAllByUserAndSemester(user, "1학기");
		return activities.stream()
				.map(activity -> FirstSemesterActivitiesResponse.builder()
						.activityName(activity.getName())
						.description(activity.getDescription())
						.date(activity.getDate())
						.club(user.getClubName())
						.build())
				.toList();
	}


}

