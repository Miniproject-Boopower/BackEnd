package likelion.mini.team1.domain.entity;

import jakarta.persistence.*;
import likelion.mini.team1.domain.enums.Semester;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String activityName;
    private String activityDescription;
    private LocalDateTime activityDate;
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
