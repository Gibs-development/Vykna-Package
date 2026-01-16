package io.xeros.content;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;


/*
 * @author C.T
 * Simple interface for top 15 main rules
 */

public class RulesInterface {

	private final static String[] rules =
			{
					"1. Common Courtesy (Spamming)\n" +
							"The in-game server, forums and discord requires all guests, members and staff to show a standard of Common Courtesy to others. This includes but not limited to spamming, purposeful crashing, or intentionally baiting someone on any platform.\n" +
							"1st Offense: Warning  | 2nd Offense: 24H Mute  | 3rd Offense: Permanent Mute or Ban",
					"",
					"2. Inappropriate Language, Inappropriate Content or Player Harassment\n" +
							"There is a zero tolerance for harassment toward other individuals especially based upon sexual preferences, personal or religious beliefs, gender or race. Language that may be considered offensive, racist or otherwise inappropriate should not be used in chats or usernames.\n" +
							"1st Offense: Warning  | 2nd Offense: 24H Mute  | 3rd Offense: 72H Mute, Permanent Mute or Ban",
					"",
					"3. Advertising or Discussion of Other Servers\n" +
							"There is a zero tolerance for advertisement or encouragement to join other servers on any platform owned and operated by RuneRogue.  This includes but not limited to In-game, Forums or Discord. \n" +
							"1st Offense: 24H Mute  | 2nd Offense: 72H Mute, Permanent Mute or Ban",
					"",
					"4. Threats \n" +
							"All guests, players and staff should feel safe and welcome at all times. We will hold a zero tolerance for any threats made against another individual including those without physical harm for example \"DDOS\" threats. \n" +
							" 1st Offense: Permanent IP Ban",
					"",
					" 5. Encouraging others to break rules \n" +
							"Anyone who encourages someone to break a rule or rules will be treated as if they committed the offense themselves and will be punished as if they are the one breaking the rule. \n" +
							"Any Offense: Punishment will be issued depending on severity of the situation",
					"",
					"6. Backseat Moderation\n" +
							"Members of the Staff Team have been hand selected by Server Management to handle certain duties inside of their role assignment. This means they are provided with the tools to complete and diffuse situations accordingly.  We respect and appreciate individuals willingness to help however a better alternative would be to help new, current, or returning players inside of the \"Help\" Clan Chat \n" +
							"1st Offense: Warning  | 2nd Offense: 24H Mute  | 3rd Offense: Permanent Mute or Ban",
					"",
					"7. Clan Etiqutte\n" +
							"Clans are a great way to play with friends, and make new relationships, but it's important to note that invading clans, or bashing them will not be tolerated. \n" +
							"The list below is a few examples of things that you may be punished for if seen/reported.\n" +
							"-Joining another Clan/CC for intent of advertising your/someones Clan.\n" +
							"-Taking Clan issues out of the CC, and bringing them into Public Chat, yell, Help CC, etc.\n" +
							"-Any type of Clan on Clan public drama is not tolerated.\n" +
							"-Advertising a Clan, or CC must be in a 30 minute interval for advertising over yell, Clan chat, or using an auto typer. \n" +
							"-Joining any type of CC via an alternative account with the sole agenda of becoming a disruption or annoyance will no longer be tolerated. \n" +
							"Any accounts that are caught doing so will be punished accordingly on both ends, thus meaning alternative accounts that relate to a main account will now be qualified to receive a punishment.\n" +
							"1st Offense: Warning  | 2nd Offense: 24H Mute  | 3rd Offense: Permanent Mute or Ban (edited)\n" +
							"[15:33]\n",
					"",
					"8. In game '::yell' Command\n" +
							"The yell system RuneRogue has to offer provides a fantastic opportunity or social platform for players wishing to interact with one another. Due to the yell system being in the noticeable eyes of the public the list below administers a few examples of punishable offences if seen/reported.\n" +
							"Staff members are here to protect the goodwill & longevity of RuneRogue. Failing to oblige by a staff members request (Server Support & higher) on yell will be warned and furthermore muted if the request to stop has been ignored.\n" +
							"-Any bitter or aggressive intensified PvP banter related discussions that occur on yell, as per discretion by staff members, will now be considered as a punishable offence. Please keep PvP banter inside of Clan-chats or Private messaging.\n" +
							"-To maintain a clean, toxic free, enjoyable yell environment, any form of excessive verbal griefing or false advertising will now be considered and viewed as a punishable offense. \n" +
							"-Griefing refers to the act of one player intentionally disrupting another player's game experience for personal pleasure and possibly potential gain. Players of RuneRogue should not attempt to grief another player by either trolling or provoking one another via global yell.\n" +
							"-RuneRogue player base is largely from the United Kingdom & the United States, therefore (due to moderation purposes) we ask that all language over yell is to be written in English ONLY. In most cases, a warning will be issued first, failing to oblige by the given warning will result in a yell-mute.\n" +
							"1st Offense: Warning  | 2nd Offense: 24H Yell Mute  | 3rd Offense: Permanent Yell Mute or Ban ",
					"",
					"9. Third Party Software\n" +
							"Any software that is used to allow the member to have an unfair advantage in any way will result in punishment. We want to create a fair environment where all players have an equal chance of all of our features. Software includes automation tools, macros, bots, and auto-clickers. This includes the development of scripts for said bots.\n" +
							"Macro Farms (3+ accounts macroing at same time) will result in an automatic IP Ban for all accounts involved. \n" +
							"We reserve the right to reset your stats if we deem it appropriate.\n" +
							"Anyone found creating/using/distributing macros will be IP banned.\n" +
							"1st Offense: 72H Ban [Skill Reset to Level 1] | 2nd Offense: Permanent Ban  | 3rd Offense: IP Ban",
					"",
					"10. Real world trading (Account & items selling/buying)\n" +
							"The act of trading RuneRogue currency, items, services, etc for outside currency is NOT allowed. This includes but is not limited to; RSGP, fiat currencies, IRL goods/services, online currencies, etc. Both parties will be punished if found to be doing so. \n" +
							"- The act of trading/selling, or the attempt of trading/selling, any account for other goods will not be allowed on the server or forums, a ban will be issued for the account being traded off.\n" +
							"- As per Old School Runescape, RuneRogue has decided to follow route & enforce the rule of Infernal cape services. Inferno services are at your own risk, account sharing is not advised, however if you do and you get hacked or scammed it is at your own risk and will not be refunded. \n" +
							" 1st Offense: Permanent Ban (Bank reset)  | 2nd Offense:  IP Ban (Bank reset) (edited)\n" +
							"[15:34]\n",
					"",
			};

	public static void displayRulesInterface(Player player) {
		int frameIndex = 0;

		player.getPA().sendFrame126("RuneRogue's MAIN RULES!", 8144);

		for (int index = 0; index < rules.length; index++) {
			player.getPA().sendFrame126(rules[index], 8145 + index);
			frameIndex++;
		}
		player.getPA().setScrollableMaxHeight(8140, 429 + (rules.length > 30 ? (rules.length - 30) * 20 : 0));
		player.getPA().resetScrollBar(8140);
		player.getPA().showInterface(8134);
	}
}