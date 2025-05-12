package likelion.mini.team1.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import likelion.mini.team1.domain.entity.Assignment;
import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.domain.entity.UserCourse;
import likelion.mini.team1.domain.enums.AssignmentStatus;
import likelion.mini.team1.domain.enums.CourseType;
import likelion.mini.team1.repository.AssignmentRepository;
import likelion.mini.team1.repository.UserCourseRepository;
import likelion.mini.team1.repository.UserRepository;
import likelion.mini.team1.util.AESUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrawlingService {

	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final UserCourseRepository userCourseRepository;
	@Autowired
	private final AssignmentRepository assignmentRepository;

	public void saveSubjectAndAssignmentAll(String studentNum) throws Exception {
		User user = userRepository.findByStudentNumber(studentNum)
			.orElseThrow(() -> new RuntimeException("학번을 가진 유저가 존재하지 않습니다."));

		String decryptedPassword = AESUtil.decrypt(user.getPassword());

		String pythonPath = "venv/bin/python";
		String scriptPath = "crawl/crawl_all.py";

		ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, studentNum, decryptedPassword);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder output = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println("[PYTHON] " + line);
			output.append(line);
		}

		int exitCode = process.waitFor();
		System.out.println("Python script exited with code: " + exitCode);

		// JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();

		List<Map<String, String>> assignmentList = objectMapper.readValue(
			output.toString(),
			new TypeReference<>() {
			}
		);

		for (Map<String, String> assignment : assignmentList) {
			String title = assignment.get("과제 제목");
			String subject = assignment.get("과목명");
			String deadline = assignment.get("마감일");
			String status = assignment.get("상태");

			UserCourse userCourse = userCourseRepository.findByCourseNameAndUser(subject.split("\\(")[0], user)
				.orElseGet(() -> {
					UserCourse newUserCourse = UserCourse.builder()
						.courseType(CourseType.NON_REGULAR)
						.user(user)
						.courseName(subject)
						.build();
					return userCourseRepository.save(newUserCourse);  // ⭐ 저장 후 반환
				});

			Assignment newAssignment = Assignment.builder()
				.title(title)
				.status(status.equals("미제출") ? AssignmentStatus.NOT_YET : AssignmentStatus.SUBMIT)
				.deadline(parseDeadline(deadline))
				.user(user)
				.userCourse(userCourse).build();

			assignmentRepository.save(newAssignment);
		}
	}

	public void saveSubjectAndAssignmentFirst(String studentNum) throws Exception {
		User user = userRepository.findByStudentNumber(studentNum)
			.orElseThrow(() -> new RuntimeException("학번을 가진 유저가 존재하지 않습니다."));

		String decryptedPassword = AESUtil.decrypt(user.getPassword());

		// --- Python 실행 부분 ---
		String pythonPath = "venv/bin/python";
		String scriptPath = "crawl/crawl.py";

		ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, studentNum, decryptedPassword);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println("[PYTHON] " + line);
		}

		int exitCode = process.waitFor();
		System.out.println("Python script exited with code: " + exitCode);
	}

	public void crawlCourse(String studentNum) throws Exception {
		User user = userRepository.findByStudentNumber(studentNum)
			.orElseThrow(() -> new RuntimeException("학번을 가진 유저가 존재하지 않습니다."));

		String decryptedPassword = AESUtil.decrypt(user.getPassword());

		String pythonPath = "venv/bin/python";
		String scriptPath = "crawl/crawl_course.py";

		ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, studentNum, decryptedPassword);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder output = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			System.out.println("[PYTHON] " + line);
			output.append(line);
		}

		int exitCode = process.waitFor();
		System.out.println("Python script exited with code: " + exitCode);

		// JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(output.toString());

		List<String> regularCourses = new ArrayList<>();
		List<String> nonRegularCourses = new ArrayList<>();

		rootNode.get("regular").forEach(node -> regularCourses.add(node.asText()));
		rootNode.get("non_regular").forEach(node -> nonRegularCourses.add(node.asText()));

		regularCourses.forEach(regularCourse -> {
			if (!userCourseRepository.existsByCourseNameAndUser(regularCourse, user)) {
				UserCourse userCourse = UserCourse.builder()
					.courseType(CourseType.REGULAR)
					.user(user)
					.courseName(regularCourse)
					.build();
				userCourseRepository.save(userCourse);
			}
		});

		nonRegularCourses.forEach(nonRegularCourse -> {
			if (!userCourseRepository.existsByCourseNameAndUser(nonRegularCourse, user)) {
				UserCourse userCourse = UserCourse.builder()
					.courseType(CourseType.NON_REGULAR)
					.user(user)
					.courseName(nonRegularCourse)
					.build();
				userCourseRepository.save(userCourse);
			}
		});

	}

	public static LocalDateTime parseDeadline(String deadlineStr) {
		if (deadlineStr == null || deadlineStr.isBlank()) return null;

		List<DateTimeFormatter> formatters = List.of(
			// 형식 1: "2025.05.08 PM 5:00" → 영문 AM/PM + dot 구분 + 12시간제
			DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm", Locale.ENGLISH),

			// 형식 2: "2025-05-06 11:30" → 하이픈 구분 + 24시간제
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),

			// 형식 3: 혹시 있을 수도 있는 "오후" → "PM" 미처리된 케이스
			DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.ENGLISH)
		);

		// "오전"/"오후" → "AM"/"PM" 치환
		deadlineStr = deadlineStr.replace("오전", "AM").replace("오후", "PM");

		for (DateTimeFormatter formatter : formatters) {
			try {
				return LocalDateTime.parse(deadlineStr, formatter);
			} catch (DateTimeParseException ignore) {
			}
		}

		System.out.println("⚠️ 마감일 파싱 실패: " + deadlineStr);
		return null;
	}
}
