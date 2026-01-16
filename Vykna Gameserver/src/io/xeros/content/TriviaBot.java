package io.xeros.content;


import io.xeros.Configuration;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/*
 * @author C.T for Koranes
 */
public class TriviaBot {
	
	
	public static final int TIMER = 20000; //10000
	public static int botTimer = TIMER;
	
	public static int answerCount;
	public static String firstPlace;
	public static String secondPlace;
	public static String thirdPlace;
	
	private static List<String> winners = new ArrayList<String>(3);

	public static void sequence() {
		
		if(botTimer > 0)
			botTimer--;
		if(botTimer <= 0) {
			botTimer = TIMER;
			didSend = false;
			askQuestion();
		}
	}

	private static boolean hasAnswered(Player p) {
		for (int i = 0; i < winners.size(); i++) {
			if (winners.get(i).equalsIgnoreCase(p.getLoginName())) {
				return true;
			}
		}
		return false;
	}


	public static void attemptAnswer(Player p, String attempt) {

		boolean accountInLobby = winners
				.stream()
				.anyMatch(winners -> p.getMacAddress().equalsIgnoreCase(p.getMacAddress()));
		if (accountInLobby) {
			p.sendMessage("@red@You have already entered the trivia on your alt!");
			p.getPA().removeAllWindows();
			return;
		}

		if (hasAnswered(p)) {
			p.sendMessage("@red@You have already answered the trivia question!");
			return;
		}
		
		if (!currentQuestion.equals("") && attempt.replaceAll("_", " ").equalsIgnoreCase(currentAnswer)) {
			
			if (answerCount == 0) {
				answerCount++;
				if(p.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
					p.setTriviaPoints(p.getTriviaPoints() + 20);
					p.sendMessage("<img=24> You Received 20 trivia points for 1st Place.");
				} else {
					p.setTriviaPoints(p.getTriviaPoints() + 10);
					p.sendMessage("<img=16> You Received 10 trivia points for 1st Place.");
				}
				firstPlace = p.getLoginName();
				AchievementHandler.activate(p, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				AchievementHandler.activate(p, AchievementList.ANSWER_80_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				return;
			}
			if (answerCount == 1) {
				if (p.getLoginName() == firstPlace) {
					p.sendMessage("Already answered");
					return;
				}
				answerCount++;
				if(p.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
					p.setTriviaPoints(p.getTriviaPoints() + 12);
					p.sendMessage("<img=24> You Received 12 trivia points for 2nd Place.");
				} else {
					p.setTriviaPoints(p.getTriviaPoints() + 6);
					p.sendMessage("<img=16> You Received 6 trivia points for 2nd Place.");
				}
				p.getPA().updateQuestTab();
				secondPlace = p.getLoginName();
				AchievementHandler.activate(p, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				AchievementHandler.activate(p, AchievementList.ANSWER_80_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				return;
			
			}
			if (answerCount == 2) {
				if (p.getLoginName() == firstPlace || p.getLoginName() == secondPlace) {
					p.sendMessage("Already answered");
					return;
				}
				if(p.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
					p.setTriviaPoints(p.getTriviaPoints() + 8);
					p.sendMessage("<img=24> You Received 8 trivia points for 3rd Place.");
				} else {	
					p.setTriviaPoints(p.getTriviaPoints() + 4);
					p.sendMessage("<img=16> You Received 4 trivia points for 3rd Place.");
				}
				thirdPlace = p.getLoginName();
				new Broadcast("<img=10><col=6666FF> Trivia: [1st:" +firstPlace+" (10 pts)] [2nd:" +secondPlace+" (6 pts)] [3rd:" +thirdPlace+"  (4 pts)]").copyMessageToChatbox().submit();
				AchievementHandler.activate(p, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				AchievementHandler.activate(p, AchievementList.ANSWER_80_TRIVIABOTS_CORRECTLY, 1);//NEW ACHIEVEMENTS
				resetForNextQuestion();
				currentQuestion = "";
				didSend = false;
				botTimer = TIMER;
				answerCount = 0;
				p.getPA().updateQuestTab();
				return;
			}
			
			
		} else {
			if(attempt.contains("question") || attempt.contains("repeat")){
				p.sendMessage("<col=800000>"+(currentQuestion));
				return;
			}
			p.sendMessage("<img=10> Trivia: Sorry! Wrong answer! "+(currentQuestion));
			return;
		}
		
	}
	
	
	public static void resetForNextQuestion() {
		if (!winners.isEmpty()) {
		    winners.clear();
		}
		answerCount = 0;
	}
	
	public static boolean acceptingQuestion() {
		return !currentQuestion.equals("");
	}
	
	private static void askQuestion() {
		for (int i = 0; i < TRIVIA_DATA.length; i++) {
			if (Misc.random(TRIVIA_DATA.length - 1) == i) {
				if(!didSend) {
					didSend = true;
				currentQuestion = TRIVIA_DATA[i][0];
				currentAnswer = TRIVIA_DATA[i][1];
				resetForNextQuestion();
				new Broadcast(currentQuestion).copyMessageToChatbox().submit();
				
				
				}
			}
		}
	}
	
	public static boolean didSend = false;

	private static final String[][] TRIVIA_DATA = {

			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "1"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "2"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "3"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "4"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "5"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "6"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "7"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "8"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "9"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>Guess a number 1-10?", "10"},

			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "a"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "b"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "c"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "d"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "e"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "f"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "g"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "h"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "i"},
			{"@red@ Trivia-Guess:</col> <col=6666FF>A random letter between A - J", "j"},

			{"@red@ Trivia:</col> <col=6666FF>How much xp do you need to reach 99?", "13034431"},
			{"@red@ Trivia:</col> <col=6666FF>What is the maximum amount of cash you can hold in your inventory?", "2147483647"},
			{"@red@ Trivia:</col> <col=6666FF>At what level prayer can you use Hawk Eye?", "26"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 12th item in the vote store?", "music cape"},
			{"@red@ Trivia:</col> <col=6666FF>What tab will be able to give you a title?", "coin"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 5th item in fat tony's store?", "magic potion"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 11th item in boss store?", "crystal key"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 43rd item in the Range shop?", "rune dart"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 1st item in the Pk store?", "pkp ticket"},
			{"@red@ Trivia:</col> <col=6666FF>What was Herblore's name originally called?", "Herblaw"},
			{"@red@ Trivia:</col> <col=6666FF>What is the slayer level to enter Alchemical hydra's instance?", "95"},
			{"@red@ Trivia:</col> <col=6666FF>In what month was "+ Configuration.SERVER_NAME+" released?", "march"},

			{"@red@ Trivia:</col> <col=6666FF>What Smithing level is required to smith a Steel Plate-Skirt?", "46"},
			{"@red@ Trivia:</col> <col=6666FF>What is the second boss to kill in the Recipe for Disaster Mini-game?", "flambeed"},
			{"@red@ Trivia:</col> <col=6666FF>How much does a dragon scimitar cost?", "100000"},
			{"@red@ Trivia:</col> <col=6666FF>How much do rune boots cost?", "12500"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 40th spell on the regular spell book?", "charge earth orb"},
			{"@red@ Trivia:</col> <col=6666FF>Which skill shows a weapon in a skull?", "slayer"},
			{"@red@ Trivia:</col> <col=6666FF>Which skill shows an image of a Fist", "strength"},

			{"@red@ Trivia:</col> <col=6666FF>What is the capital of Spain ?", "madrid"},
			{"@red@ Trivia:</col> <col=6666FF>Which key unlocks the Hunllef teleport ?", "crystalline"},
			{"@red@ Trivia:</col> <col=6666FF>How much boss points do you get from killing Vorkath ?", "10"},
			{"@red@ Trivia:</col> <col=6666FF>What combat level is Scorpia ?", "225"},
			{"@red@ Trivia:</col> <col=6666FF>What is the first herb you can plant?", "guam"},
			{"@red@ Trivia:</col> <col=6666FF>Where is our Home located?", "edgeville"},
			{"@red@ Trivia:</col> <col=6666FF>What combat level is the Chaos Elemental ?", "305"},
			{"@red@ Trivia:</col> <col=6666FF>What is the name of the Thieving pet?", "rocky"},
			{"@red@ Trivia:</col> <col=6666FF>What item gives the ability to dig ?", "spade"},

			{"@red@ Trivia:</col> <col=6666FF>Which magic spell turns items to cash ?", "alchemy"},
			{"@red@ Trivia:</col> <col=6666FF>Copy the following ::answer 5g8lks39ta4ss6ehp26gr.", "5g8lks39ta4ss6ehp26gr"},
			{"@red@ Trivia:</col> <col=6666FF>What is the tab with a bag called ?", "inventory"},
			{"@red@ Trivia:</col> <col=6666FF>What cape gives you Wings ?", "death cape"},
			{"@red@ Trivia:</col> <col=6666FF>What is the NPC called that sells Mithril Seeds?", "gamble shop"},
			{"@red@ Trivia:</col> <col=6666FF>FAST ! What is 2 + 2 ?", "4"},
			{"@red@ Trivia:</col> <col=6666FF>DO YOU MIND TO TELL ME I AM WRONG !", "wrong"},
			{"@red@ Trivia:</col> <col=6666FF>What mobs give extra drop-rate bonus while being skulled ?", "revenants"},
			{"@red@ Trivia:</col> <col=6666FF>How much bolts is the minimum amount to make a set ?", "15"},

			{"@red@ Trivia:</col> <col=6666FF>What is one commitment that will help RuneRogue grow ?", "vote"},
			{"@red@ Trivia:</col> <col=6666FF>How many starter achievements are there ?", "9"},
			{"@red@ Trivia:</col> <col=6666FF>How much do you get if you high alch a shortbow (u) ?", "17"},
			{"@red@ Trivia:</col> <col=6666FF>Write the following word backwards sanguinesti", "itseniugnas"},
			{"@red@ Trivia:</col> <col=6666FF>Type the following ysps96z3320xmsq25i4kjq", "ysps96z3320xmsq25i4kjq"},
			{"@red@ Trivia:</col> <col=6666FF>Which pet can you get from mining ?", "rock golem"},
			{"@red@ Trivia:</col> <col=6666FF>What number comes after 569152647942653 ?", "569152647942654"},
			{"@red@ Trivia:</col> <col=6666FF>Who is the owner of RuneRogue ?", "rogue"},
			{"@red@ Trivia:</col> <col=6666FF>What NPC decants my potions ?", "zahur"},

			{"@red@ Trivia:</col> <col=6666FF>TELL ME I AM RIGHT !", "no"},
			{"@red@ Trivia:</col> <col=6666FF>What Farming level do you need to plant Torstol seed ?", "85"},
			{"@red@ Trivia:</col> <col=6666FF>Finish the sentence veni vidi?", "vici"},
			{"@red@ Trivia:</col> <col=6666FF>What is the capital of Italy ?", "Rome"},
			{"@red@ Trivia:</col> <col=6666FF>What is the most consumed beverages around the world ?", "water"},
			{"@red@ Trivia:</col> <col=6666FF>Where can I add a player if we don't seem to get along ?", "Ignore list"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 8th achievement on Legendary ?", "fire of exchange"},
			{"@red@ Trivia:</col> <col=6666FF>What was Rune-scape's original name ?", "Deviousmud"},

			{"@red@ Trivia:</col> <col=6666FF>What level do you need to go to the Woodcutting guild ?", "90"},
			{"@red@ Trivia:</col> <col=6666FF>Which NPC drops an abyssal whip ?", "abyssal demon"},
			{"@red@ Trivia:</col> <col=6666FF>I found a bug ! Who do I contact ? ", "staff"},
			{"@red@ Trivia:</col> <col=6666FF>Six multiplied by 204 equals", "1224"},
			{"@red@ Trivia:</col> <col=6666FF>Copy the following number 34976128465534168", "34976128465534168"},
			{"@red@ Trivia:</col> <col=6666FF>What is the Capital of the USA ?", "Washington"},
			{"@red@ Trivia:</col> <col=6666FF>What boss drops inquisitor's mace ?", "the nightmare"},
			{"@red@ Trivia:</col> <col=6666FF>How many country's are there currently in the world ?", "195"},
			{"@red@ Trivia:</col> <col=6666FF>Which pet can you get from Vorkath ?", "vorki"},

			{"@red@ Trivia:</col> <col=6666FF>What is the tab called with the image of a star ?", "prayer"},
			{"@red@ Trivia:</col> <col=6666FF>What is the capital of Denmark ?", "copenhagen"},
			{"@red@ Trivia:</col> <col=6666FF>What is the official birth year of the internet ?", "1983"},
			{"@red@ Trivia:</col> <col=6666FF>What NPC drops Bandos hilt ?", "general graardor"},
			{"@red@ Trivia:</col> <col=6666FF>What skill was released on 21 November 2006 ?", "Hunter"},
			{"@red@ Trivia:</col> <col=6666FF>What baby pet can you get from hunter ?", "chinchompa"},
			{"@red@ Trivia:</col> <col=6666FF>How many thieving stalls are there at home ?", "5"},
			{"@red@ Trivia:</col> <col=6666FF>How many cows are there at the cow teleport ?", "14"},
			{"@red@ Trivia:</col> <col=6666FF>What age was the Wizard guild build ?", "5th"},

			{"@red@ Trivia:</col> <col=6666FF>What age did Guthix came to Rune-scape ?", "1st"},
			{"@red@ Trivia:</col> <col=6666FF>What food has a weapon in its name ?", "swordfish"},
			{"@red@ Trivia:</col> <col=6666FF>Where is the only place I can lose my items ?", "wilderness"},

			{"@red@ Trivia:</col> <col=6666FF>How much coins are added to your bank after a correct random ?", "100000"},
			{"@red@ Trivia:</col> <col=6666FF>Where can i sell/buy items for any price ?", "tradingpost"},
			{"@red@ Trivia:</col> <col=6666FF>What is the world of Runescape called ? ", "gielinor"},
			{"@red@ Trivia:</col> <col=6666FF>What is the capital of India ? ", "new delhi"},
			{"@red@ Trivia:</col> <col=6666FF>At what level can I fletch a Magic longbow (u) ?", "85"},
			{"@red@ Trivia:</col> <col=6666FF>What is Zamorak the God of ? ", "power and chaos"},

			{"@red@ Trivia:</col> <col=6666FF>What total level is the minimum to enter COX ?", "1200"},
			{"@red@ Trivia:</col> <col=6666FF>If I subtract 13 inv spots. How many do I have left ?", "15"},
			{"@red@ Trivia:</col> <col=6666FF>What fishing level can I fish sharks ?", "76"},
			{"@red@ Trivia:</col> <col=6666FF>What boss drops an blowpipe ?", "zulrah"},

			{"@red@ Trivia:</col> <col=6666FF>Where can I check on the update list ?", "discord"},
			{"@red@ Trivia:</col> <col=6666FF>What is the capital of France ?", "Paris"},
			{"@red@ Trivia:</col> <col=6666FF>How many different bank tabs can I use ?", "9"},

			{"@red@ Trivia:</col> <col=6666FF>How many NPC's are there in Edgeville bank ?", "14"},
			{"@red@ Trivia:</col> <col=6666FF>Where does the evil tree spawn ?", "edge"},
			{"@red@ Trivia:</col> <col=6666FF>What year did OSRS come back as an option to log in to ?", "2013"},
			{"@red@ Trivia:</col> <col=6666FF>What skill caused the Falador massacre ?", "construction"},
			{"@red@ Trivia:</col> <col=6666FF>What color does zulrah take on melee attack ? ", "red"},
			{"@red@ Trivia:</col> <col=6666FF>How much gp is given to pass trough the Al-kharid gate at Lumbridge ?", "10"},
			{"@red@ Trivia:</col> <col=6666FF>How many Goblins are there south of the cows ? ", "8"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 4th Legendary achievement ?", "the mimic"},
			{"@red@ Trivia:</col> <col=6666FF>What chest can I open every hour ?", "Loyalty"},

			{"@red@ Trivia:</col> <col=6666FF>What is currently the max combat level on RuneRogue ?", "126"},
			{"@red@ Trivia:</col> <col=6666FF>What's currently the max total level on RuneRogue ?", "2178"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 21st ?", "farming"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 4th ?", "strength"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 16th ?", "magic"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 2nd ?", "hitpoints"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 14th ?", "crafting"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 9th ?", "fishing"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 3rd ?", "mining"},
			{"@red@ Trivia:</col> <col=6666FF>If skills are numbered, what skill is 22nd ?", "hunter"},

			{"@red@ Trivia:</col> <col=6666FF>How many foe points are needed for K'Klik?", "150000"},
			{"@red@ Trivia:</col> <col=6666FF>What is the 12th item in the donator shop?", "ultra mystery box"},

	};
	
	public static String currentQuestion;
	private static String currentAnswer;
}