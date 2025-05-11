package likelion.mini.team1.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class testController {

	@GetMapping("/test")
	public String testAPI() {
		return "test API 입니다";
	}
}
