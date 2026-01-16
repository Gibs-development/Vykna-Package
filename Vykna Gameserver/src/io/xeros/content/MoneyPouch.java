package io.xeros.content;


import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Handles Money Pouch
 * @author C.T for runerogue
 *
 */
public class MoneyPouch {

	int maxCashStored = 2147483647;
	
	/**
	 * Player
	 */
	private final Player player;
	
	/**
	 * Money Pouch
	 * @param player
	 */
	public MoneyPouch(Player player) {
		this.player = player;
	}
	
	/**
	 * Format coins
	 * @param amount
	 * @return
	 */
	public String formatCoins(long amount) {
		if (amount >= 1_000 && amount < 1_000_000) {
			return "" + (amount / 1_000) + "K";
		}
		
		if (amount >= 1_000_000 && amount < 1_000_000_000) {
			return "" + (amount / 1_000_000) + "M";
		}
		
		if (amount >= 1_000_000_000) {
			return "" + (amount / 1_000_000_000) + "B";
		}
		return "" + amount;
	}
	
	/**
	 * Adds coins to Money Pouch
	 */
	public void addPouch() {

		//Check if player is in a position to add coins
		if (player.getPosition().inDuelArena() || player.getPosition().inPcGame() || player.getPosition().inPcGame() ||  player.getPosition().inWild() || player.teleporting || player.inTrade || player.inDuel || player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank || player.isDead || player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen()) {
			player.sendMessage("You can't do this right now!");
			return;
		}
	//	int amount1 = player.getItems().getItemAmount(995);
	//	if (player.getMoneyPouch() + amount1 >= maxCashStored) {
	//		player.sendMessage("@red@You cant put anymore cash in your pouch!");
	//		return;
	//	}

		//Check if money pouch is filled
		if (player.getMoneyPouch() == Long.MAX_VALUE) {
			player.sendMessage("Your pouch is already full!");
			return;
		}
		
		//Grabs amount of coins to store
		int amount = player.getItems().getItemAmount(995);
		
		//Checks if current stored coins + new coins to store exceed the max value
		if (player.getMoneyPouch() + amount >= Long.MAX_VALUE) {
			player.sendMessage("You can't fit all that into your pouch!");
			return;
		}
	
		//Removes coins from inventory
		player.getItems().deleteItem(995, amount);
		
		//Adds coins to Money Pouch
		player.setMoneyPouch(player.getMoneyPouch() + amount);
		
		//Sends confirmation message
		player.sendMessage("@dre@You have added " + Misc.format(amount) + " coins into your pouch. Total: " + formatCoins(player.getMoneyPouch()) + ".");
		
		//Updates string
		player.getPA().sendFrame126(player.getMoneyPouch() + "", 8135);
		AchievementHandler.activate(player, AchievementList.MONEY_POUCH, 1);//NEW ACHIEVEMNTS
	}
	
	/**
	 * Withdraw coins from Money Pouch
	 * @param amount
	 */
	public void withdrawPouch(long amount) {

		// Check if player is in a position to withdraw coins
		if (player.getPosition().inDuelArena() || player.getPosition().inPcGame() || player.getPosition().inWild()  || player.inTrade || player.inDuel || player.teleporting  || player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank || player.isDead || player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen()) {
			player.sendMessage("You can't do this right now!");
			return;
		}

		// Checks if player is withdrawing a negative amount
		if (amount >=  0 && amount <= 0) {
			player.getPA().closeAllWindows();
			player.sendMessage("You can't withdraw a negative amount!");
			return;
		}

		// Checks if player has the amount to withdraw stored
		if (player.getMoneyPouch() < amount) {
			amount = (int) player.getMoneyPouch();
		}

		// Checks if coins in inventory + amount to withdraw passes max value
		if ((long) (player.getItems().getItemAmount(995) + amount) > Integer.MAX_VALUE) {
			player.sendMessage("You don't have enough space to withdraw that many coins!");
			amount = Integer.MAX_VALUE - player.getItems().getItemAmount(995);
		}

		// Check to see if player is withdrawing more than max value
		if (amount > Integer.MAX_VALUE) {
			player.sendMessage("You can't withdraw more than 2B coins at a time!");
			return;
		}

		// Checks if player has max value of coins in inventory
		if (player.getItems().getItemAmount(995) == Integer.MAX_VALUE) {
			player.sendMessage("You can't withdraw any more coins!");
			return;
		}

		// Checks if player has space to withdraw the coins
		if (!player.getItems().playerHasItem(995) && player.getItems().freeSlots() == 0) {
			player.sendMessage("You do not have enough inventory spaces to withdraw coins.");
			return;
		}



		// Removes coins from pouch
		player.setMoneyPouch(player.getMoneyPouch() - amount);

		// Adds coins to inventory
		//player.getInventory().add(995, (int) amount);
		player.getItems().addItem(995, (int) amount);

		// Sends confirmation dialogue
        player.getDH().sendItemStatement("You have withdrawn <col=255>" + Misc.format(amount) + " </col>coins.", 995);
		// Updates string
		player.getPA().sendFrame126(player.getMoneyPouch() + "", 8135);
	}











	public void withdrawTickets(long amount) {

		// Check if player is in a position to withdraw tokens
		if (player.getPosition().inDuelArena() || player.getPosition().inPcGame() || player.getPosition().inWild()  || player.inTrade || player.inDuel || player.teleporting  || player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank || player.isDead || player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen()) {
			player.sendMessage("@red@You can't do this right now!");
			return;
		}

		// Checks if player is withdrawing a negative amount
		if (amount >=  0 && amount <= 0) {
			player.getPA().closeAllWindows();
			player.sendMessage("@red@You can't withdraw a negative amount!");
			return;
		}

	//	if (player.getMoneyPouch() < 1000000001) {
		if (player.getMoneyPouch() >= 0 && player.getMoneyPouch() <= 1000000001) {
			player.sendMessage("@red@You will need 1b or more to withdraw an platinum token!");
			return;
		}

		// Checks if player has the amount to withdraw stored
		if (player.getMoneyPouch() < amount) {
			amount = (int) player.getMoneyPouch();
		}

		// Checks if tokens in inventory + amount to withdraw passes max value
		if ((long) (player.getItems().getItemAmount(13204) + amount) > Integer.MAX_VALUE) {
			player.sendMessage("@red@You don't have enough space to withdraw that many platinum tokens!");
			amount = Integer.MAX_VALUE - player.getItems().getItemAmount(13204);
		}

		// Check to see if player is withdrawing more than max value
		if (amount >= 2 && amount <= Integer.MAX_VALUE) {
			player.sendMessage("@red@You can't withdraw more than 1 platinum token at a time!");
			return;
		}

		// Checks if player has max value of tokens in inventory
		if (player.getItems().getItemAmount(13204) == Integer.MAX_VALUE) {
			player.sendMessage("@red@You can't withdraw any more platinum tokens!");
			return;
		}

		// Checks if player has space to withdraw the tokens
		if (!player.getItems().playerHasItem(13204) && player.getItems().freeSlots() == 0) {
			player.sendMessage("@red@You do not have enough inventory spaces to withdraw platinum tokens.");
			return;
		}

		// Removes coins from pouch
		player.setMoneyPouch(player.getMoneyPouch() - 1000000000);

		// Adds tokens to inventory
		player.getItems().addItem(13204, (int) 1);

		// Sends confirmation dialogue
		player.getDH().sendItemStatement("You have withdrawn <col=255>" + Misc.format(amount) + " </col>platinum token.", 13204);
		// Updates string
		player.getPA().sendFrame126(player.getMoneyPouch() + "", 8135);
	}



}
