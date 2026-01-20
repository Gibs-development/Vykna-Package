package io.xeros.content.achievement.vykna;

public enum CombatSubcategory implements AchievementSubcategory {
	SLAYER("Slayer"),
	BOSSES("Bosses"),
	WILDERNESS("Wilderness");

	private final String displayName;

	CombatSubcategory(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
