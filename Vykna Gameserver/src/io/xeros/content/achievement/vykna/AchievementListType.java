package io.xeros.content.achievement.vykna;

import java.util.Arrays;
import java.util.List;

public enum AchievementListType {
	TASKS("Tasks", TaskSubcategory.values()),
	SKILLING("Skilling", SkillingSubcategory.values()),
	COMBAT("Combat", CombatSubcategory.values());

	private final String displayName;
	private final List<? extends AchievementSubcategory> subcategories;

	AchievementListType(String displayName, AchievementSubcategory[] subcategories) {
		this.displayName = displayName;
		this.subcategories = Arrays.asList(subcategories);
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<? extends AchievementSubcategory> getSubcategories() {
		return subcategories;
	}
}
