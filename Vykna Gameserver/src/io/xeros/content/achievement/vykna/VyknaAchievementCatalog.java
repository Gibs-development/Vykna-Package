package io.xeros.content.achievement.vykna;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class VyknaAchievementCatalog {

	private VyknaAchievementCatalog() {
	}

	public static List<AchievementDefinition> getAchievements(AchievementListType listType,
			AchievementSubcategory subcategory) {
		return Stream.of(VyknaAchievement.values())
				.filter(achievement -> achievement.getListType() == listType)
				.filter(achievement -> achievement.getSubcategory().equals(subcategory))
				.collect(Collectors.toList());
	}
}
