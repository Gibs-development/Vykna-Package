package io.xeros.achievements;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;



/**
 * Handles the achievement interfaces
 * 
 * @author C.T
 *
 */
public class AchievementInterface extends InterfaceHandler {



	/**
	 * Sends the achievement completion interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendCompleteInterface(final Player player, final AchievementList achievement) {
		int color = 0;

		switch (achievement.getDifficulty()) {
		case EASY:
			color = 0x1C889E;
			break;
		case MEDIUM:
			color = 0xD9750B;
			break;
		case HARD:
			color = 0xC41414;
			break;
		}
		player.sendMessage("You've completed the achievement: " + achievement.getName() + "!", color);
	}

	/**
	 * Sends the achievement information interface
	 * 
	 * @param player
	 * @param achievement
	 */
	public static void sendInterfaceForAchievement(final Player player, AchievementList achievement) {

		final String difficulty = StringUtils.capitalize(achievement.getDifficulty().name().toLowerCase());
		final int completed = player.getPlayerAchievements().get(achievement);
		final int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
		player.getPA().sendString("<col=ff9040>" + achievement.getName(), 35006);
		player.getPA().sendString("<col=ff7000>" + achievement.getDescription(), 35008);
		player.getPA().sendString("<col=ff7000>" + difficulty, 35010);
		player.getPA().sendString("<col=ff7000>" + Misc.format(completed) + " / " + Misc.format(achievement.getCompleteAmount()) + " ( " + progress + "% )", 35012);
		player.getPA().sendString("<col=ff7000>" + achievement.getReward() + " achievement point" + (achievement.getReward() == 1 ? "" : "s") + ".", 35014);

		for (GameItem item : achievement.getRewards()) {
			player.getPA().itemOnInterface(item.getId(), item.getAmount(), 36002, 0);
		}
		boolean isCompleted = completed >= achievement.getCompleteAmount();
		player.getPA().sendConfig(694, isCompleted ? 1 : 0);
		
	}

	private final String[] text;

	public AchievementInterface(Player player, AchievementHandler.AchievementDifficulty difficulty) {
		super(player);
		int shift = 0;
		final int total = AchievementList.values().length;
		
		switch (difficulty) {
		case EASY:
			player.getPA().resetScrollBar(35030);
			break;
		case MEDIUM:
			player.getPA().resetScrollBar(35030);
			break;
		case HARD:
			player.getPA().resetScrollBar(35030);
			break;
		}

		player.getPA().sendString("</col>Completed: <col=65280>" + player.getPA().achievementCompleted() + "</col>/" + total, 35015);
		player.getPA().sendString("</col>Points: <col=65280>" + player.getAchievementsPoints(), 35016);
		final List<AchievementList> list = AchievementList.asList(difficulty);

		text = new String[total];

		Arrays.fill(text, "");

		for (final AchievementList achievement : list) {
			int completed = player.getPlayerAchievements().get(achievement);
			if (completed > achievement.getCompleteAmount()) {
				completed = achievement.getCompleteAmount();
			}


			text[shift++] = ""+getColor(player.getPlayerAchievements().get(achievement),achievement.getCompleteAmount())+"" + achievement.getName();
		}
	}

	public String getColor(int amount, int max) {
		if (amount == 0) {
			return "<col=FF0000>";
		}
		if (amount >= max) {
			return "<col=00FF00>";
		}
		return "<col=FFFF00>";
	}

	@Override
	protected int startingLine() {
		return 35031;
	}

	@Override
	protected String[] text() {
		return text;
	}

}