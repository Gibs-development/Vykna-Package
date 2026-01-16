package io.xeros.content;

import java.util.HashMap;

import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

/**
 * Handles the fountain of rune
 * @author C.T for RuneRogue
 *
 */
public class FountainOfRune {
	
	private final static int OBJECT_IDENTIFICATION = 26782;
	public static final int ANIMATION = 881;

	private static final HashMap<Integer, Integer> USABLE = new HashMap<>();
	
	public static void declare() {
			USABLE.put(1704, 11978);//Amulet of Glory to Amulet of Glory (6)
			USABLE.put(1706, 11978);//Amulet of Glory (1) to Amulet of Glory (6)
			USABLE.put(1708, 11978);//Amulet of Glory (2) to Amulet of Glory (6)
			USABLE.put(1710, 11978);//Amulet of Glory (3) to Amulet of Glory (6)
			USABLE.put(1712, 11978);//Amulet of Glory (4) to Amulet of Glory (6)
			USABLE.put(11976, 11978);//Amulet of Glory (5) to Amulet of Glory (6)
			USABLE.put(11118, 11972);//Combat bracelet (4) to Combat bracelet (6)
			USABLE.put(11120, 11972);//Combat bracelet (3) to Combat bracelet (6)
			USABLE.put(11122, 11972);//Combat bracelet (2) to Combat bracelet (6)
			USABLE.put(11124, 11972);//Combat bracelet (1) to Combat bracelet (6)
			USABLE.put(11126, 11972);//Combat bracelet to Combat bracelet (6)
			USABLE.put(11972, 11974);//Combat bracelet (5) to Combat bracelet (6)
			USABLE.put(11105, 11968);//Skills necklace (4) to Skills necklace (6)
			USABLE.put(11107, 11968);//Skills necklace (3) to Skills necklace (6)
			USABLE.put(11109, 11968);//Skills necklace (2) to Skills necklace (6)
			USABLE.put(11111, 11968);//Skills necklace (1) to Skills necklace (6)
			USABLE.put(11113, 11968);//Skills necklace to Skills necklace (6)
			USABLE.put(11970, 11968);//Skills necklace (5) to Skills necklace (6)

		    USABLE.put(1664, 11968);//Dragon necklace to Skills necklace (6)
		    USABLE.put(1702, 11968);//Dragonstone amulet to Skills necklace (6)
		    USABLE.put(11115, 11968);//Dragonstone bracelet to Skills necklace (6)

	}

	public static boolean itemOnObject(Player player, int object, int item) {
		if (object != OBJECT_IDENTIFICATION) {
			return false;
		}
		
		if (USABLE.get(item) != null) {
			player.startAnimation(ANIMATION);
			GameItem replacement = new GameItem(USABLE.get(item));
			int amount = player.getItems().getItemAmount(item);
			if (player.getItems().playerHasItem(item, amount))
				player.getItems().deleteItem2(item, amount);
				player.getItems().addItem(replacement.getId(), amount);
			    player.sendMessage("@gre@You manage to use the fountain for its ability's.");

		if (Misc.hasOneOutOf(1000)) {
				player.getItems().addItemToBankOrDrop(19707, 1);
				player.sendMessage("@red@You have received the amulet of eternal glory.");
				Announcement.announce("@red@"+player.getLoginName()+" @pur@has received the @red@Amulet Of Eternal Glory@pur@ from the @red@Fountain Of Rune.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received an ultra rare: Amulet Of Eternal Glory from the Fountain Of Rune!");
				AchievementHandler.activate(player, AchievementList.ETERNAL_GLORY, 1);
			}
			if (item >= 1704 && item <= 1712 || item == 11976) {//Amulet of glory achievement
				AchievementHandler.activate(player, AchievementList.RECHARGE_GLORYI, amount);
				AchievementHandler.activate(player, AchievementList.RECHARGE_GLORYII, amount);
				AchievementHandler.activate(player, AchievementList.RECHARGE_GLORYIII, amount);
			}
			return true;
		}
		return false;
	}
	
}
