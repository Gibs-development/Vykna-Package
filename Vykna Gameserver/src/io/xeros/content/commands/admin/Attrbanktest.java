package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAttributes;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.items.bank.BankTab;

public class Attrbanktest extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		GameItem whipA = new GameItem(Items.ABYSSAL_WHIP, 1);
		ItemAttributes attrsA = new ItemAttributes();
		attrsA.rarityId = 1;
		attrsA.perk1 = 100;
		attrsA.perk1Rank = 1;
		whipA.setAttrs(attrsA);

		GameItem whipB = new GameItem(Items.ABYSSAL_WHIP, 1);
		ItemAttributes attrsB = new ItemAttributes();
		attrsB.rarityId = 2;
		attrsB.perk1 = 101;
		attrsB.perk1Rank = 2;
		whipB.setAttrs(attrsB);

		c.getItems().addItem(whipA, true);
		c.getItems().addItem(whipB, true);

		depositFromInventory(c, whipA);
		depositFromInventory(c, whipB);

		BankTab tab = c.getBank().getBankTab(0);
		if (tab == null) {
			c.sendMessage("Attr test: bank tab missing.");
			return;
		}

		boolean wasBanking = c.isBanking;
		c.isBanking = true;
		withdrawFromBank(c, tab, whipA);
		withdrawFromBank(c, tab, whipB);
		c.isBanking = wasBanking;

		c.getItems().queueBankContainerUpdate();
		c.getItems().addContainerUpdate(io.xeros.model.items.ContainerUpdate.INVENTORY);
		c.getItems().processContainerUpdates();
		PlayerSave.saveGame(c);

		c.sendMessage("Attr test complete: two whips with different attrs banked/withdrawn.");
		c.sendMessage("Relog to confirm attrs persist.");
	}

	private void depositFromInventory(Player player, GameItem item) {
		int slot = findInventorySlotByHash(player, item.getAttrsHash());
		if (slot == -1) {
			player.sendMessage("Attr test: could not find inventory slot for hash=" + item.getAttrsHash());
			return;
		}
		player.getItems().addToBankFromSlot(slot, item.getId(), item.getAmount(), true, true);
	}

	private void withdrawFromBank(Player player, BankTab tab, GameItem item) {
		int slot = findBankSlotByHash(tab, item.getAttrsHash());
		if (slot == -1) {
			player.sendMessage("Attr test: could not find bank slot for hash=" + item.getAttrsHash());
			return;
		}
		player.getItems().removeFromBankSlot(tab, slot, item.getAmount(), true);
	}

	private int findInventorySlotByHash(Player player, int attrsHash) {
		for (int slot = 0; slot < player.playerItems.length; slot++) {
			if (player.playerItems[slot] <= 0) {
				continue;
			}
			if (player.playerItemAttrHash[slot] == attrsHash) {
				return slot;
			}
		}
		return -1;
	}

	private int findBankSlotByHash(BankTab tab, int attrsHash) {
		for (int slot = 0; slot < tab.size(); slot++) {
			BankItem bankItem = tab.getItem(slot);
			if (bankItem != null && bankItem.getAttrsHash() == attrsHash) {
				return slot;
			}
		}
		return -1;
	}
}
