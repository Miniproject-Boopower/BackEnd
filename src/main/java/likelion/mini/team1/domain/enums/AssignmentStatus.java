package likelion.mini.team1.domain.enums;

public enum AssignmentStatus {

	SUBMIT("제출"),
	NOT_YET("미제출");

	private final String description;

	AssignmentStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
