package io.xeros.content.achievement.vykna;

public enum TaskSubcategory implements AchievementSubcategory {
	LUMBRIDGE("Lumbridge"),
	VARROCK("Varrock"),
	FALADOR("Falador"),
	ARDOUGNE("Ardougne"),
	WILDERNESS("Wilderness"),
	MISC("Misc");

	private final String displayName;

	TaskSubcategory(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
