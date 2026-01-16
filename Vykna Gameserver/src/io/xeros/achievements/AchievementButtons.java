package io.xeros.achievements;


import io.xeros.model.entity.player.Player;

import java.util.HashMap;



/**
 * Handles the achievement buttons
 * 
 * @author C.T
 *
 */
public class AchievementButtons {

	private static final HashMap<Integer, AchievementList> BUTTONS_1 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_2 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_3 = new HashMap<Integer, AchievementList>();

	static {
		int button = 136215;
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementHandler.AchievementDifficulty.EASY)) {
			BUTTONS_1.put(button++, achievement);
		}
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementHandler.AchievementDifficulty.MEDIUM)) {
			BUTTONS_2.put(button++, achievement);
		}
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementHandler.AchievementDifficulty.HARD)) {
			BUTTONS_3.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Player player, int buttonId) {
		if (player.getAchievement() == AchievementHandler.AchievementDifficulty.EASY && BUTTONS_1.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_1.get(buttonId));
			return true;
		}

		if (player.getAchievement() == AchievementHandler.AchievementDifficulty.MEDIUM && BUTTONS_2.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_2.get(buttonId));
			return true;
		}

		if (player.getAchievement() == AchievementHandler.AchievementDifficulty.HARD && BUTTONS_3.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_3.get(buttonId));
			return true;
		}
		return false;
	}

}