package likelion.mini.team1.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String studentNumber;
	String password;
	String name;
	String major;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
}
