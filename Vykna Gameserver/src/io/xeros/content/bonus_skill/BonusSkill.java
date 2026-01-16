package io.xeros.content.bonus_skill;

import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;


/**
 * Author C.T for koranes
 *
 */

public class BonusSkill {


public static boolean bonusskill = false;

public static String getSkillName() {
	switch (Configuration.BONUS_SKILL) {
	case 6:
		return "Magic";
	case 5:
		return "Prayer";
	case 4:
		return "Ranged";
	case 3:
		return "Hitpoints";
	case 2:
		return "Strength";
	case 1:
		return "Defence";
	case 0:
		return "Attack";
		case 7:
			return "Cooking";
		case 8:
			return "Woodcutting";
		case 9:
			return "Fletching";
		case 10:
			return "Fishing";
		case 11:
			return "Firemaking";
		case 12:
			return "Crafting";
		case 13:
			return "Smithing";
		case 14:
			return "Mining";
		case 15:
			return "Herblore";
		case 16:
			return "Agility";
		case 17:
			return "Thieving";
		case 18:
			return "Slayer";
		case 19:
			return "Farming";
		case 20:
			return "Runecrafting";
		case 21:
			return "Hunter";
	default:
		return "none";
	}
}


public static void setBonusSkill() {
	CycleEventHandler.getSingleton().addEvent(bonusskill, new CycleEvent() {

		@Override
		public void execute(CycleEventContainer container) {
			if(bonusskill) {
				bonusskill = false;
				Configuration.BONUS_SKILL = -1;
				Configuration.BONUS_SKILL_TIMER = 3000;
				return;
			}
			Configuration.BONUS_SKILL = Misc.random(5, 21);
			if (Configuration.BONUS_SKILL == 6)
				Configuration.BONUS_SKILL = 7;

			new Broadcast("Bonus skill: 50% XP multiplier for "+getSkillName()+"!").copyMessageToChatbox().submit();
			Configuration.BONUS_SKILL_TIMER = 3000;
			bonusskill = true;
		}
	}, 3000);
}

}

