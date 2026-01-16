package io.xeros.content.dailytasks;


import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 * @author C.T for runerogue
 * Handles the daily tasks system
 *
 */

public class DailyTasks {

	public final static double MYSTERY_BOX_CHANCE = 25;//The mystery box chance
	
	public enum PossibleTasks {

		//PVM TASKS

		//SLAYER_TASK(TaskTypes.PVM, 5, "Slayer tasks"),
		BARROWS_CHESTS(TaskTypes.PVM, 10, "Barrows chests"),
		SKOTIZO(TaskTypes.PVM, 3, "Kill Skotizos"),
		BLACK_DRAGONS(TaskTypes.PVM, 50, "Kill black dragons"),
		BLUE_DRAGONS(TaskTypes.PVM, 50, "Kill blue dragons"),
		ABYSSAL_DEMONS(TaskTypes.PVM, 50, "Kill abyssal demons"),
		DARK_BEASTS(TaskTypes.PVM, 50, "Kill dark beasts"),
		GENERAL_GRAARDOR(TaskTypes.PVM, 5, "Kill General Graardors"),
		KREE_ARRA(TaskTypes.PVM, 5, "Kill Kree'Arras"),
		TSUTSAROTH(TaskTypes.PVM, 5, "Kill K'ril Tsutsaroths"),
		ZILYANA(TaskTypes.PVM, 5, "Kill Commander Zilyanas"),
		BLACK_DEMONS(TaskTypes.PVM, 50, "Kill black demons"),
		IRON_DRAGONS(TaskTypes.PVM, 50, "Kill iron dragons"),
		STEEL_DRAGONS(TaskTypes.PVM, 50, "Kill steel dragons"),
		MITHRIL_DRAGONS(TaskTypes.PVM, 50, "Kill mithril dragons"),


		//SKILLING TASKS
		LAW_RUNES(TaskTypes.SKILLING, 20, "Craft law runes"),
		RED_CHINCHOMPAS(TaskTypes.SKILLING, 30, "Catch red chinchompas"),
		SEERS_COURSE(TaskTypes.SKILLING, 10, "Run Seers' course"),
		PRAYER_POTIONS(TaskTypes.SKILLING, 80, "Make prayer potions"),
		MASTER_FARMERS(TaskTypes.SKILLING, 75, "Pickpocket master farmers"),
		DRAGONSTONES(TaskTypes.SKILLING, 50, "Cut dragonstones"),
		YEW_SHORTBOWS(TaskTypes.SKILLING, 100, "Fletch yew shortbows"),
		MITHRIL_ORES(TaskTypes.SKILLING, 200, "Mine mithril ores"),
		IRON_PLATEBODYS(TaskTypes.SKILLING, 35, "Make iron platebodies"),
		SHARKS(TaskTypes.SKILLING, 100, "Catch sharks"),
		LOBSTERS(TaskTypes.SKILLING, 150, "Cook lobsters"),
		LIGHT_YEW_LOGS(TaskTypes.SKILLING, 100, "Light yew logs"),
		YEW_LOGS(TaskTypes.SKILLING, 100, "Cut yew logs"),
		FUR(TaskTypes.SKILLING, 150, "Steal furs"),
		MAGIC_LOGS(TaskTypes.SKILLING, 60, "Cut magic logs"),
		LIGHT_MAGIC_LOGS(TaskTypes.SKILLING, 70, "Light magic logs"),
		GEM_ROCK(TaskTypes.SKILLING, 25, "Mine gem rocks"),
		NATURE_RUNES(TaskTypes.SKILLING, 25, "Craft nature runes"),
		;
		;
		
		public TaskTypes type;
		public int amount;
		String message;
		
		private PossibleTasks(TaskTypes type, int amount, String message) {
			
			this.type = type;
			this.amount = amount;
			this.message = message;
			
		}
		
	}
	
	/**
	 * Gets today's date
	 * @return
	 */
	
	public static int getTodayDate() {
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		return (month * 100 + day);
	}
	
	/**
	 * Assigns a task to a player when it has daily's enabled and has no current task.
	 * @param player
	 */
	
	public static void assignTask(Player player) {
		if (player.dailyEnabled) {

			if(player.currentDailyTask != null) {
				if (!player.completedDailyTask)
					player.sendMessage("@red@Daily Task@bla@: " + player.currentDailyTask.message +" ("+(player.currentDailyTask.amount - player.totalDailyDone)+").");
				else
					player.sendMessage("<col=6666FF>You have already completed your daily task today!");
				return;
			}
			
			if (player.dailyTaskDate == getTodayDate() && player.currentDailyTask == null && player.completedDailyTask) {
				player.sendMessage("<col=6666FF>You have already completed your daily task today!");
				return;
			} else if (player.dailyTaskDate != getTodayDate() && player.currentDailyTask == null) {
					player.currentDailyTask = getRandomTask(player.playerChoice);
					player.sendMessage("@red@New Daily Task@bla@: " + player.currentDailyTask.message+" "+(player.currentDailyTask.amount - player.totalDailyDone)+".");
					player.completedDailyTask = false;
			}
			
		} else
			player.sendMessage("@red@Daily Task@bla@: Daily tasks are not enabled, speak to the task manager to receive them.");
	}
	/**
	 * Assigns a task to a player when it has daily's enabled and has no current task.
	 * @param player
	 */
	
	public static void checkTask(Player player) {
		if (player.dailyEnabled) {

			if(player.currentDailyTask != null) {
				if (!player.completedDailyTask)
					player.sendMessage("@red@Daily Task@bla@: " + player.currentDailyTask.message +" ("+(player.currentDailyTask.amount - player.totalDailyDone)+").");
				else
					player.sendMessage("@red@You have already completed your daily task!");				
				return;
			}
			
			if (player.dailyTaskDate == getTodayDate() && player.currentDailyTask == null && player.completedDailyTask) {
				player.sendMessage("@red@You already completed your daily task today.");
				return;
			}
			
		} else
			player.sendMessage("@red@Speak to the daily task manager to enable daily tasks.");
	}
	public static void complete(Player player) {
		if(player.currentDailyTask == null)
			return;
		
		if(player.totalDailyDone >= player.currentDailyTask.amount) {
			player.totalDailyDone = 0;
			player.dailyTaskDate = getTodayDate();
			player.completedDailyTask = true;
			player.sendMessage("@red@You have completed your daily task: "+ player.currentDailyTask.message + ", take these rewards!");
			AchievementHandler.activate(player, AchievementList.DAILY_TASK, 1);//NEW ACHIEVEMNTS
			if (Misc.hasOneOutOf(MYSTERY_BOX_CHANCE)) {
				player.getItems().addItem(6199, 1);
				PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(player.getLoginName()) + " @pur@has received an @red@Mystery Box @pur@from an @red@Daily Task.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: Mystery Box from an Daily Task.");
			}
			if (player.dailyTaskAmount == 10) {
				player.getItems().addItem(995, 1000000);
				PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(player.getLoginName()) + " @blu@has received @red@1m @blu@for completing @red@10 Daily Tasks.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: 1m for completing 10 Daily Tasks.");
			}
			if (player.dailyTaskAmount == 20) {
				player.getItems().addItem(995, 5000000);
				PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(player.getLoginName()) + " @blu@has received @red@5m @blu@for completing @red@20 Daily Tasks.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: 1m for completing 20 Daily Tasks.");
			}
			if (player.dailyTaskAmount == 33) {
				player.getItems().addItem(995, 20000000);
				player.getItems().addItem(6199, 1);
				PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(player.getLoginName()) + " @blu@has received @red@20m & mbox @blu@for completing @red@all the Daily Tasks.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: 20m & mbox for completing all the Daily Tasks.");
			}
			player.getItems().addItemUnderAnyCircumstance(995, Misc.random(400000, 650000));
			player.getItems().addItemUnderAnyCircumstance(20791, 1);
			player.dailyTaskPoints += 10;
			player.dailyTaskAmount += 1;
			player.dailyTaskMultiplier += 1;
			if (player.dailyTaskMultiplier == 5) {
				player.dailyTaskPoints += 10;
				player.sendMessage("@blu@You receive twice the task points as you have completed another 5 tasks in a row.");
				player.dailyTaskMultiplier = 0;
			} else {
				player.sendMessage("@blu@You receive 10 task points as you have completed your daily task.");

			}
			player.sendMessage("@blu@You are rewarded with some coins and an Extra supply crate!");
			player.currentDailyTask = null;
		}
	}
	
	public static void increase(Player player, PossibleTasks task) {
		if(player.currentDailyTask == null)
			return;
		
		if (player.currentDailyTask == task) {
			player.totalDailyDone++;
			player.sendMessageSkill("@red@"+(player.currentDailyTask.amount - player.totalDailyDone)+" remaining of your daily task.");
			complete(player);
		}
	}
	
	/**
	 * Gets a random task on the choice of what the player has selected
	 * @param type
	 * @return
	 */
	//private static PossibleTasks getRandomTask(TaskTypes type) {
	//	ArrayList<PossibleTasks> possibleTasks = new ArrayList<>();
	//	for(PossibleTasks tasks : PossibleTasks.values())
	//		if(tasks.type == type)
	//			possibleTasks.add(tasks);
	//	return possibleTasks.get(Misc.random(possibleTasks.size()));
	//}

	private static PossibleTasks getRandomTask(TaskTypes type) {
		ArrayList<PossibleTasks> possibleTasks = new ArrayList<>();
		for(PossibleTasks tasks : PossibleTasks.values())
			if(tasks.type.equals(type))
				possibleTasks.add(tasks);
		return Misc.randomTypeOfList(possibleTasks);
	}
	
}