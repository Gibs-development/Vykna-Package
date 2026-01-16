package io.xeros.content.wogw;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

public class Wogw {

	public static String[] lastDonators = new String[5];
	//private static int slot = 0;

	private static final int LEAST_ACCEPTED_AMOUNT = 1000000; //1m

	public static long EXPERIENCE_TIMER = 0, PC_POINTS_TIMER = 0, DOUBLE_DROPS_TIMER = 0, BARROWS_BONUS_TIMER = 0;
	public static int MONEY_TOWARDS_EXPERIENCE = 0, MONEY_TOWARDS_MINIGAME_BONUS = 0, MONEY_TOWARDS_DOUBLE_DROPS = 0, MONEY_TOWARDS_BARROWS_BONUS = 0;

	@SuppressWarnings("resource")
	public static void init() {
		try {
			File f = new File("./Data/wogw.txt");
			Scanner sc = new Scanner(f);
			int i = 0;
			while(sc.hasNextLine()){
				i++;
				String line = sc.nextLine();
				String[] details = line.split("=");
				String amount = details[1];

				switch (i) {
					case 1:
						MONEY_TOWARDS_EXPERIENCE = (int) Long.parseLong(amount);
						break;
					case 2:
						EXPERIENCE_TIMER = (int) Long.parseLong(amount);
						break;
					case 3:
						MONEY_TOWARDS_MINIGAME_BONUS = (int) Long.parseLong(amount);
						break;
					case 4:
						PC_POINTS_TIMER = (int) Long.parseLong(amount);
						break;
					case 5:
						MONEY_TOWARDS_DOUBLE_DROPS = (int) Long.parseLong(amount);
						break;
					case 6:
						DOUBLE_DROPS_TIMER = (int) Long.parseLong(amount);
						break;
					case 7:
						MONEY_TOWARDS_BARROWS_BONUS = (int) Long.parseLong(amount);
						break;
					case 8:
						BARROWS_BONUS_TIMER = (int) Long.parseLong(amount);
						break;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("./Data/wogw.txt"));
			out.write("experience-amount=" + MONEY_TOWARDS_EXPERIENCE);
			out.newLine();
			out.write("experience-timer=" + EXPERIENCE_TIMER);
			out.newLine();
			out.write("minigame-amount=" + MONEY_TOWARDS_MINIGAME_BONUS);
			out.newLine();
			out.write("minigame-timer=" + PC_POINTS_TIMER);
			out.newLine();
			out.write("drops-amount=" + MONEY_TOWARDS_DOUBLE_DROPS);
			out.newLine();
			out.write("drops-timer=" + DOUBLE_DROPS_TIMER);
			out.newLine();
			out.write("barrows-amount=" + MONEY_TOWARDS_BARROWS_BONUS);
			out.newLine();
			out.write("barrows-timer=" + BARROWS_BONUS_TIMER);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void donate(Player player, int amount) {
		if (player.wogwOption.isEmpty()) {
			player.sendMessage("You must choose something to donate towards.");
			return;
		}
		if (amount < LEAST_ACCEPTED_AMOUNT) {
			player.sendMessage("You must donate at least 1m coins.");
			return;
		}
		if (!player.getItems().playerHasItem(995, amount)) {
			player.sendMessage("@cr10@You do not have " + Misc.getValueWithoutRepresentation(amount) + ".");
			return;
		}
		player.getItems().deleteItem(995, amount);
		//player.getPA().sendFrame171(1, 38020);

		/**
		 * Updating latest donators
		 */
		String towards = Objects.equals(player.wogwOption, "minigame") ? "+ bonus PC points!" : Objects.equals(player.wogwOption, "experience") ? "bonus experience!" : Objects.equals(player.wogwOption, "drops") ? "double drop rate!" : Objects.equals(player.wogwOption, "barrows") ? "double barrows drop rate!" : "";
		player.sendMessage("You successfully donated " + Misc.format((int) player.wogwDonationAmount) + "gp to the well of goodwill towards");
		player.sendMessage(towards);
		Discord.writeWellMessage(""+player.getLoginName()+" as donated " + Misc.format((int) player.wogwDonationAmount) + "gp to the well of goodwill towards");
		Discord.writeWellMessage(towards);
		AchievementHandler.activate(player, AchievementList.DONATE_WELL, 1);//NEW ACHIEVEMNTS
		//Wogw.lastDonators[Wogw.slot] = "" + Misc.formatPlayerName(player.playerName) + " donated " + Misc.getValueWithoutRepresentation(player.wogwDonationAmount) + " towards " + towards;
		//player.getPA().sendFrame126(Wogw.lastDonators[Wogw.slot], 8145 + Wogw.slot);

		/**
		 * Setting sprites back to unticked
		 */
		player.getPA().sendChangeSprite(38006, (byte) 1);
		player.getPA().sendChangeSprite(38007, (byte) 1);
		player.getPA().sendChangeSprite(38008, (byte) 1);
		player.getPA().sendChangeSprite(38013, (byte) 1);
		player.getPA().sendFrame126("", 38019);
		/**
		 * Announcing donations over 10m
		 */
		if (player.wogwDonationAmount >= 10_000_000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] @blu@" + Misc.formatPlayerName(player.getLoginName()) + "@bla@ donated @blu@" + Misc.getValueWithoutRepresentation(player.wogwDonationAmount) + "@bla@ to the well of goodwill!");
			player.donationWell+=1;
		}
		/**
		 * Setting the amounts and enabling bonus if the amount reaches above required.
		 */
		switch (player.wogwOption) {
			case "experience":
				handleMoneyToExperience(amount);
				break;

			case "pc":
				if (MONEY_TOWARDS_MINIGAME_BONUS + amount >= 30000000 && PC_POINTS_TIMER == 0) {
					MONEY_TOWARDS_MINIGAME_BONUS = MONEY_TOWARDS_MINIGAME_BONUS + amount - 30000000;
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill has been filled!");
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 hour of +5 bonus pc points.");
					PC_POINTS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
					Configuration.BONUS_PC_WOGW = true;
				} else {
					MONEY_TOWARDS_MINIGAME_BONUS += amount;
				}
				break;

			case "drops":
				if (MONEY_TOWARDS_DOUBLE_DROPS + amount >= 50000000 && DOUBLE_DROPS_TIMER == 0) {
					MONEY_TOWARDS_DOUBLE_DROPS = MONEY_TOWARDS_DOUBLE_DROPS + amount - 50000000;
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill has been filled!");
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 hour of 20% drop rate bonus.");
					DOUBLE_DROPS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
					Configuration.DOUBLE_DROPS = true;
				} else {
					MONEY_TOWARDS_DOUBLE_DROPS += amount;
				}
				break;
			case "barrows":
				if (MONEY_TOWARDS_BARROWS_BONUS + amount >= 50000000 && BARROWS_BONUS_TIMER == 0) {
					MONEY_TOWARDS_BARROWS_BONUS = MONEY_TOWARDS_BARROWS_BONUS + amount - 50000000;
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill has been filled!");
					PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 hour of 20% drop rate bonus.");
					BARROWS_BONUS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
					Configuration.DOUBLE_BARROWS = true;
				} else {
					MONEY_TOWARDS_BARROWS_BONUS += amount;
				}
				break;
		}
		player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
		player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_MINIGAME_BONUS) + "/30M", 38016);
		player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_DOUBLE_DROPS) + "/50M", 38017);
		//player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_BARROWS_BONUS) + "/50M", 38018);

		/*Wogw.slot++;
		if (Wogw.slot == 5) {
			Wogw.slot = 0;
		}*/
		player.wogwOption = "";
		player.wogwDonationAmount = 0;
	}

	public static void donateItems(Player player, int amount) {
		//if (amount < LEAST_ACCEPTED_AMOUNT) {
		//	player.sendMessage("You must donate at least 1 million worth of items.");
		//	return;
		//}
		//for (final Wogwitems.itemsOnWell t : Wogwitems.itemsOnWell.values()) {
		if (!player.getItems().playerHasItem(player.wellItem, 1)) {
			player.sendMessage("You do not have any items to donate.");
			return;
			//}
		}
		//player.getItems().deleteItem(995, amount);
		//player.getPA().sendFrame171(1, 38020);

		/**
		 * Updating latest donators
		 */
		//String towards = player.wogwOption == "pc" ? "+5 bonus PC Points!" : player.wogwOption == "experience" ? "double experience!" : player.wogwOption == "drops" ? "double drops!" : "";
		//player.sendMessage("You successfully donated " + Misc.format((int) player.wogwDonationAmount) + "gp to the well of goodwill towards");
		//player.sendMessage(towards);
		//Wogw.lastDonators[Wogw.slot] = "" + Misc.formatPlayerName(player.playerName) + " donated " + Misc.getValueWithoutRepresentation(player.wogwDonationAmount) + " towards " + towards;
		//player.getPA().sendFrame126(Wogw.lastDonators[Wogw.slot], 38033 + Wogw.slot);

		/**
		 * Setting sprites back to unticked
		 */
		player.getPA().sendChangeSprite(38006, (byte) 1);
		player.getPA().sendChangeSprite(38007, (byte) 1);
		player.getPA().sendChangeSprite(38008, (byte) 1);
		player.getPA().sendChangeSprite(38019, (byte) 1);
		player.getPA().sendChangeSprite(38013, (byte) 1);
		player.getPA().sendFrame126("", 38019);
		/**
		 * Announcing donations over 5m
		 */
		String name = String.valueOf(ItemAssistant.getItemName(player.wellItem));
		String determine = "a";
		if (name.startsWith("A") || name.startsWith("E") || name.startsWith("I") || name.startsWith("O") || name.startsWith("U"))
			determine = "an";
		if (player.wogwDonationAmount >= 5_000_000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] @blu@" + Misc.formatPlayerName(player.getLoginName()) + "@bla@ donated "+ determine +" @blu@" + ItemAssistant.getItemName(player.wellItem) + " @bla@worth @blu@" + Misc.getValueWithoutRepresentation(player.wogwDonationAmount) + "@bla@ to the well of goodwill!");
		}
		/**
		 * Setting the amounts and enabling bonus if the amount reaches above required.
		 */
		switch (player.wogwOption) {
			case "experience":
				handleMoneyToExperience(amount);
				break;
		}
		player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
		/*Wogw.slot++;
		if (Wogw.slot == 5) {
			Wogw.slot = 0;
		}*/
		player.wogwOption = "";
		player.wogwDonationAmount = 0;
	}

	private static void handleMoneyToExperience(int amount) {

		if (MONEY_TOWARDS_EXPERIENCE + amount >= 30000000 && EXPERIENCE_TIMER == 0) {
			MONEY_TOWARDS_EXPERIENCE = MONEY_TOWARDS_EXPERIENCE + amount - 30000000;
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill has been filled!");
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 hour of bonus experience.");
			EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;

			Configuration.BONUS_XP_WOGW = true;
		} else {
			MONEY_TOWARDS_EXPERIENCE += amount;
		}
	}

	public static void appendBonus() {
		if (MONEY_TOWARDS_EXPERIENCE >= 30000000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill was filled above the top!");
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 more hour of bonus experience.");
			EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
			MONEY_TOWARDS_EXPERIENCE -= 30000000;
			PlayerHandler.nonNullStream().forEach(player -> {
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_MINIGAME_BONUS) + "/30M", 38016);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_DOUBLE_DROPS) + "/50M", 38017);
			//	player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_BARROWS_BONUS) + "/50M", 38018);
			});
			Configuration.BONUS_XP_WOGW = true;
			return;
		}
		if (MONEY_TOWARDS_MINIGAME_BONUS >= 30000000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill was filled above the top!");
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 more hour of +5 bonus pc points.");
			PC_POINTS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
			MONEY_TOWARDS_MINIGAME_BONUS -= 30000000;
			PlayerHandler.nonNullStream().forEach(player -> {
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_MINIGAME_BONUS) + "/30M", 38016);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_DOUBLE_DROPS) + "/50M", 38017);
			//	player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_BARROWS_BONUS) + "/50M", 38018);
			});
			Configuration.BONUS_PC_WOGW = true;
			return;
		}
		if (MONEY_TOWARDS_DOUBLE_DROPS >= 50000000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill was filled above the top!");
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 more hour of double drop rate!");
			DOUBLE_DROPS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
			MONEY_TOWARDS_DOUBLE_DROPS -= 50000000;
			PlayerHandler.nonNullStream().forEach(player -> {
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_MINIGAME_BONUS) + "/30M", 38016);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_DOUBLE_DROPS) + "/50M", 38017);
			//	player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_BARROWS_BONUS) + "/50M", 38018);
			});
			Configuration.DOUBLE_DROPS = true;
		}
		if (MONEY_TOWARDS_BARROWS_BONUS >= 50000000) {
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>The Well of Goodwill was filled above the top!");
			PlayerHandler.executeGlobalMessage("[@blu@WOGW@bla@] <col=6666FF>It is now granting everyone 1 more hour of double barrows drop rate!");
			BARROWS_BONUS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
			MONEY_TOWARDS_BARROWS_BONUS -= 50000000;
			PlayerHandler.nonNullStream().forEach(player -> {
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_EXPERIENCE) + "/30M", 38015);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_MINIGAME_BONUS) + "/30M", 38016);
				player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_DOUBLE_DROPS) + "/50M", 38017);
				//player.getPA().sendFrame126(Misc.getValueWithoutRepresentation(Wogw.MONEY_TOWARDS_BARROWS_BONUS) + "/50M", 38018);
			});
			Configuration.DOUBLE_BARROWS = true;
		}
	}
}
