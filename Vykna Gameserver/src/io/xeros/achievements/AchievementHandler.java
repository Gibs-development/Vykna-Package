package io.xeros.achievements;


import io.xeros.content.Announcement;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.items.GameItem;

import static io.xeros.achievements.AchievementList.addReward;

/**
 * Handles the achievements
 * 
 * @author C.T
 * 
 */
public class AchievementHandler {

	/**
	 * Holds the types of achievements
	 * 
	 */
	public enum AchievementDifficulty {
		EASY,
		MEDIUM,
		HARD
	}

	/**
	 * Activates the achievement for the individual player. Increments the
	 * completed amount for the player. If the player has completed the
	 * achievement, they will receive their reward.
	 * 
	 * @param player
	 *            The player activating the achievement.
	 * @param achievement
	 *            The achievement for activation.
	 */
	public static void activate(Player player, AchievementList achievement, int increase) {
		if (increase == -1) {
			return;
		}
		
		if (achievement == null) {
			return;
		}

		if (player.getPlayerAchievements().get(achievement) >= achievement.getCompleteAmount()) {
			return;
		}

		final int current = player.getPlayerAchievements().get(achievement);

		if (current == 0) {
			player.sendMessage("<col=297A29>You have started the achievement: " + achievement.getName() + ".");
		}

		player.getPlayerAchievements().put(achievement, current + increase);

		if (player.getPlayerAchievements().put(achievement, current + increase) >= achievement.getCompleteAmount()) {


			if(player.completedAllAchievements()) {
				activate(player, AchievementList.COMPLETIONIST, 1);
				new Broadcast(""+player.getLoginName()+" has completed the completionist achievement and was rewarded an 50$ donation scroll.").copyMessageToChatbox().submit();
			}

			AchievementInterface.sendCompleteInterface(player, achievement);
			player.addAchievementPoints(player.getAchievementsPoints() + achievement.getReward());
			int points = player.getAchievementsPoints();
			player.sendMessage("<col=297A29>Congratulations! You have completed an achievement. You now have " + points + " point" + (points == 1 ? "" : "s") + ".");
			Announcement.announce("<img=18> <col="+ Color.COOL_BLUE.getColorValue()+">"+player.getLoginName()+"</col> <col="+ Color.RUNITE.getColorValue()+"> has just completed the</col> <col="+ Color.RUNITE.getColorValue()+"> "+achievement.getName()+"</col>.");
			addReward(player, achievement);
		}
	}

	/**
	 * Checks if the reward is completed.
	 * 
	 * @param player
	 *            The player checking the achievement.
	 * @param achievement
	 *            The achievement for checking.
	 */
	public static boolean isCompleted(Player player, AchievementList achievement) {
		return player.getPlayerAchievements().get(achievement) >= achievement.getCompleteAmount();
	}

}