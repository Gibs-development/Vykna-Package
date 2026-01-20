package io.xeros.content.achievement.vykna;

import io.xeros.model.entity.player.Player;

public interface AchievementDefinition {
	int getUid();

	String getTag();

	int getPoints();

	AchievementListType getListType();

	AchievementSubcategory getSubcategory();

	String getName();

	String getDescription();

	int getTarget();

	default int getProgress(Player player) {
		return 0;
	}

	default boolean isComplete(Player player) {
		return getTarget() > 0 && getProgress(player) >= getTarget();
	}

	default String getProgressText(Player player) {
		if (getTarget() <= 0) {
			return "";
		}
		return getProgress(player) + "/" + getTarget();
	}
}
