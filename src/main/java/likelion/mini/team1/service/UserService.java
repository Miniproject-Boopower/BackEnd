package likelion.mini.team1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import likelion.mini.team1.domain.dto.request.SignUpRequest;
import likelion.mini.team1.domain.entity.User;
import likelion.mini.team1.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;

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
			.password(passwordEncoder.encode(signUpRequest.getPassword()))
			.build();
		userRepository.save(newUser);
	}
}
