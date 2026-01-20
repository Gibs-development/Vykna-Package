package io.xeros.content.achievement.vykna;

public enum SkillingSubcategory implements AchievementSubcategory {
	WOODCUTTING("Woodcutting"),
	FISHING("Fishing"),
	MINING("Mining");

	private final String displayName;

	SkillingSubcategory(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
