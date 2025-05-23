package likelion.mini.team1.domain.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
	private String studentNumber;
	private String password;
}