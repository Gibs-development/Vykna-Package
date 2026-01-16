package io.xeros.content;


import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

/**
 * Handles the Player Profiler
 * 
 * @author C.T For RuneRogue
 *
 */
public class PlayerProfiler {

	/**
	 * Display player's own profile
	 * 
	 * @param player
	 */

	public static void myProfile(Player player) {

		// Time played
		long milliseconds = (long) player.playTime * 600;
		long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
		String time = days + " days, " + hours + " hrs";

		player.sendMessage("@dre@You are now viewing your own profile.");

		player.getPA().sendString("My Profile", 41602);

		for (int i = 0; i < 20; i++) {
			player.getPA().sendString(Misc.capitalizeFirstLetter(Skill.SKILL_NAMES[i]) + " level: @dre@" + player.getLevel(Skill.forId(i)) +"\\nPrestige level: @dre@" + player.prestigeNumber, 41632 + i);
		}

		player.getPA().sendString("</col>Name: @gre@" + Misc.capitalizeFirstLetter(player.getLoginName()), 41607);
		player.getPA().sendString("</col>Rank: @gre@" + player.getRights().buildCrownString() + " " + player.getRights().getPrimary().toString(), 41608);
		player.getPA().sendString("</col>Combat: @gre@" + player.combatLevel, 41609);


		player.getPA().sendString("</col>Time Played: @gre@" +time, 41681);
		player.getPA().sendString("</col>Likes: @whi@" + player.getLikes(), 41682);
		player.getPA().sendString("</col>Dislikes @whi@" + player.getDislikes(), 41683);
		player.getPA().sendString("</col>Views @whi@" + player.getProfileViews(), 41684);
		player.getPA().sendString("</col>Drop Rate Bonus: %@whi@" + DropManager.getModifier1(player), 41685);
		player.getPA().sendString("</col>Total Donated: $@whi@" + player.amDonated, 41686);
		player.getPA().sendString("</col>Kills: @whi@" + player.killcount, 41687);
		player.getPA().sendString("</col>Deaths: @whi@" + player.deathcount, 41688);
		player.getPA().sendString("</col>Kill Streak: @whi@" + player.killStreak, 41689);


		if (player.getSlayer().getTask().isEmpty()) {
			player.getPA().sendString("</col>Slayer Task: @red@ None", 41690);
		} else {
			player.getPA().sendString("</col>Slayer Task: @whi@" +player.getSlayer().getTask().get().getFormattedName()+ "</col>(" + player.getSlayer().getTaskAmount() + "</col>)", 41690);
		}

		player.getPA().sendString("</col>Slayer Points: @whi@" + player.getSlayer().getPoints(), 41691);
		player.getPA().sendString("</col>PC Points: @whi@" + player.pcPoints, 41692);
		player.getPA().sendString("</col>Boss Points: @whi@"+ player.bossPoints, 41693);
		player.getPA().sendString("</col>Trivia Points: @whi@"+ player.triviaPoints, 41694);
		player.getPA().sendString("</col>Afk Points: @whi@"+ player.afkPoints, 41695);
		player.getPA().sendString("</col>Daily Task Points: @whi@"+ player.dailyTaskPoints, 41696);
		player.getPA().sendString("", 41697);

		player.getPA().showInterface(41600);
	}


	public static void displayProfile(Player player, Player viewing) {

		// Time played
		long milliseconds = (long) viewing.playTime * 600;
		long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
		String time = days + " days, " + hours + " hrs";

		viewing.sendMessage("@dre@" + Misc.capitalizeFirstLetter(player.getLoginName()) + " is viewing your profile!");

		viewing.setProfileViews(viewing.getProfileViews() + 1);

		player.getPA().sendString("Player Profiler", 41802);

		for (int i = 0; i < 20; i++) {
			player.getPA().sendString(Misc.capitalizeFirstLetter(Skill.SKILL_NAMES[i]) + " level: @dre@" + viewing.getLevel(Skill.forId(i)) +"\\nPrestige level: @dre@" + viewing.prestigeNumber, 41832 + i);
		}

		player.getPA().sendString("</col>Name: @gre@" + Misc.capitalizeFirstLetter(viewing.getLoginName()), 41807);
		player.getPA().sendString("</col>Rank: @gre@" + viewing.getRights().buildCrownString() + " " + viewing.getRights().getPrimary().toString(), 41808);
		player.getPA().sendString("</col>Combat: @gre@" + viewing.combatLevel, 41809);


		player.getPA().sendString("</col>Time Played: @gre@" +time, 41881);
		player.getPA().sendString("</col>Likes: @whi@" + viewing.getLikes(), 41882);
		player.getPA().sendString("</col>Dislikes @whi@" + viewing.getDislikes(), 41883);
		player.getPA().sendString("</col>Views @whi@" + viewing.getProfileViews(), 41884);
		player.getPA().sendString("</col>Drop Rate Bonus: %@whi@" + DropManager.getModifier1(viewing), 41885);
		player.getPA().sendString("</col>Total Donated: $@whi@" + viewing.amDonated, 41886);
		player.getPA().sendString("</col>Kills: @whi@" + viewing.killcount, 41887);
		player.getPA().sendString("</col>Deaths: @whi@" + viewing.deathcount, 41888);
		player.getPA().sendString("</col>Kill Streak: @whi@" + viewing.killStreak, 41889);


		if (viewing.getSlayer().getTask().isEmpty()) {
			player.getPA().sendString("</col>Slayer Task: @red@ None", 41890);
		} else {
			player.getPA().sendString("</col>Slayer Task: @whi@" +viewing.getSlayer().getTask().get().getFormattedName()+ "</col>(" + viewing.getSlayer().getTaskAmount() + "</col>)", 41890);
		}

		player.getPA().sendString("</col>Slayer Points: @whi@" + viewing.getSlayer().getPoints(), 41891);
		player.getPA().sendString("</col>PC Points: @whi@" + viewing.pcPoints, 41892);
		player.getPA().sendString("</col>Boss Points: @whi@"+ viewing.bossPoints, 41893);
		player.getPA().sendString("</col>Trivia Points: @whi@"+ viewing.triviaPoints, 41894);
		player.getPA().sendString("</col>Afk Points: @whi@"+ viewing.afkPoints, 41895);
		player.getPA().sendString("</col>Daily Task Points: @whi@"+ viewing.dailyTaskPoints, 41896);
		player.getPA().sendString("", 41897);

		player.getPA().showInterface(41800);
	}


	public static void search(Player player, String string) {

		player.sendMessage("@dre@Searching account '" + Misc.capitalizeFirstLetter(string) + "' for profile...");

		Player viewing = Player.getPlayerByName(string);

		player.viewing = string;

		if (player == viewing && !Configuration.isOwner(player)) {
			player.sendMessage("@dre@Please click on the 'My Profile' button.");
			return;
		}

		if (viewing == null) {
			player.sendMessage("@dre@" + Misc.capitalizeFirstLetter(string) + " either does not exist or is not online!");
			return;
		}

		int deltaX = viewing.getLocation().getX() - (player.getMapRegionX() << 3);
		int deltaY = viewing.getLocation().getY() - (player.getMapRegionY() << 3);

		if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
			player.sendMessage("@dre@You can only view profiles of players that are in your region.");
			return;
		}
		if (viewing.getProfilePrivacy()) {
			player.sendMessage("@dre@" + Misc.capitalizeFirstLetter(viewing.getLoginName()) + " has disabled profile viewing.");
			return;
		}
		if (player.getProfilePrivacy()) {
			player.sendMessage("@dre@You cannot view profiles while your profile privacy is off!");
			return;
		}
		displayProfile(player, viewing);
	}



	public static void manageReputation(Player player, String name, int button) {


		Player viewing = Player.getPlayerByName(name);

		//player.viewing = name;

		if (player == viewing) {
			return;
		}


		if (!player.canLike()) {
			player.sendMessage("@dre@You may only give out 3 reputations per day.");
			return;
		}

		if (name == null) {
			player.sendMessage("@red@You can only use those buttons from searching an player on the tab first.");
			return;
		}

		switch (button) {

			case 164006:
				player.addLike();
				player.sendMessage("@dre@You have liked " + Misc.capitalizeFirstLetter(viewing.getLoginName()) + "'s profile!");
				if (player.getLikesGiven() == 3) {
					player.setLastLike(System.currentTimeMillis());
					player.sendMessage("You have given your last reputation; please wait another 24 hours to give more.");
				}
				viewing.setLikes(viewing.getLikes() + 1);
				viewing.sendMessage("@dre@" + Misc.capitalizeFirstLetter(player.getLoginName()) + " has liked your profile.");
				break;

			case 164009:
				player.addLike();
				player.sendMessage("@dre@You have disliked " + Misc.capitalizeFirstLetter(viewing.getLoginName()) + "'s profile!");
				if (player.getLikesGiven() == 3) {
					player.setLastLike(System.currentTimeMillis());
					player.sendMessage("You have given your last reputation; please wait another 24 hours to give more.");
				}
				viewing.setDislikes(viewing.getDislikes() + 1);
				viewing.sendMessage("@dre@" + Misc.capitalizeFirstLetter(player.getLoginName()) + " has disliked your profile.");
				break;

		}
	}


}
