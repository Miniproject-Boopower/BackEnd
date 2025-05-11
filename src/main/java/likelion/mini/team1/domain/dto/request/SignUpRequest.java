package likelion.mini.team1.domain.dto.request;

import lombok.Data;

@Data
public class SignUpRequest {
	String studentNumber;
	String password;
	String name;
	String major;
}
