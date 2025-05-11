package likelion.mini.team1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;

	@GetMapping("/test")
	public String testAPI() {
		userService.test();
		return "test API 입니다";
	}
}
