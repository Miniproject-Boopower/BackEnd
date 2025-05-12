package likelion.mini.team1.domain.enums;

public enum CourseType {
	REGULAR("정규과목"),
	NON_REGULAR("비정규과목");

	private final String description;

	CourseType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}