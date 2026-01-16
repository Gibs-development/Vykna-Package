package io.xeros.content;


import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

import java.text.NumberFormat;


/**
 * Price checker
 *
 * @author C.T for RuneRogue.
 */

public class PriceChecker {

	private static int getFramesForSlot[][] = {

	    { 0, 45550 }, { 1, 45551 },	{ 2, 45552 }, { 3, 45553 }, { 4, 45554 }, { 5, 45555 }, { 6, 45556 }, { 7, 45557 },
		{ 8, 45558 }, { 9, 45559 }, { 10, 45560 }, { 11, 45561 }, { 12, 45562 }, { 13, 45563 }, { 14, 45564 },
		{ 15, 45565 }, { 16, 45566 }, { 17, 45567 }, { 18, 45568 }, { 19, 45569 }, { 20, 45570 }, { 21, 45571 },
		{ 22, 45572 }, { 23, 45573 }, { 24, 45574 }, { 25, 45575 }, { 26, 45576 }, { 27, 45577 },
	};

	public static int arraySlot(Player c, int[] array, int target) {
		int spare = -1;
		for (int x = 0; x < array.length; x++) {
			if (array[x] == target && c.getItems().isStackable(target)) {
				return x;
			} else if (spare == -1 && array[x] <= 0) {
				spare = x;
			}
		}
		return spare;
	}

	public static void clearConfig(Player c) {
		for (int x = 0; x < c.priceItem.length; x++) {
			if (c.priceItem[x] > 0)
				withdrawItem(c, c.priceItem[x], x, c.priceItemN[x]);
		}
		c.getItems().updateInventory = true;
		c.getItems().resetItems(5064);
	}

	public static void depositItem(Player c, int id, int amount) {
		int slot = arraySlot(c, c.priceItem, id);
		long price = (long) Math.floor(c.getShops().getItemShopValue(id));

		if (!ItemDef.forId(id).isTradable() || ItemDef.forId(id).getName().contains("Clue scroll")) {
			c.sendMessage("@red@This item is un-tradeable!");
			return;
		}

		if (c.getItems().getItemAmount(id) < amount) {
			amount = c.getItems().getItemAmount(id);
		}
		if (slot == -1) {
			c.sendMessage("The price-checker is currently full.");
			return;
		}
		if (!c.getItems().isStackable(id)) {
			amount = 1;
		}
		if (!c.getItems().playerHasItem(id, amount)) {
			return;
		}
		c.getItems().deleteItem2(id, amount);
		if (c.priceItem[slot] != id) {
			c.priceItem[slot] = id;
			c.priceItemN[slot] = amount;
		} else {
			c.priceItem[slot] = id;
			c.priceItemN[slot] += amount;
		}

		price *= 0.537;
		c.total += price * amount;
		updateChecker(c);
	}

	public static void itemOnInterface(Player c, int frame, int slot, int id,
			int amount) {
		if (c.getOutStream() != null) {
			c.outStream.createFrameVarSizeWord(34);
			c.outStream.writeUnsignedWord(frame);
			c.outStream.writeByte(slot);
			c.outStream.writeUnsignedWord(id + 1);
			c.outStream.writeByte(255);
			c.outStream.writeDWord(amount);
			c.outStream.endFrameVarSizeWord();
		}
	}

	public static void open(Player c) {
		c.isChecking = true;
		c.total = 0;
		c.getPA().sendFrame126(
				"" + NumberFormat.getInstance().format((c.total)) + "",
				45513);
		updateChecker(c);
		resetFrames(c);
		c.getItems().resetItems(5064);
		c.getPA().sendFrame248(45500, 5063);
	}

	public static void resetFrames(Player c) {
		for (int x = 0; x < 27; x++) {
			if (c.priceItem[x] <= 1) {
				setFrame(c, x, getFramesForSlot[x][1], c.priceItem[x], c.priceItemN[x],
						false);
			}
		}
	}

	private static void setFrame(Player player, int slotId, int frameId,
			int itemId, int amount, boolean store) {
		long price = (long) Math.floor(player.getShops().getItemShopValue(itemId));
		price *= 0.537;

		long totalAmount = price * amount;
		String total = NumberFormat.getInstance().format((totalAmount));
		if (!store) {
			player.getPA().sendFrame126("", frameId);
			player.getPA().sendFrame126("", frameId + 1);
			player.getPA().sendFrame126("", frameId + 2);
			return;
		}
		if (player.getItems().isStackable(itemId)) {
			player.getPA().sendFrame126("" + amount + " x", frameId);
			player.getPA().sendFrame126(
					""
							+ NumberFormat.getInstance().format((price))
							+ "     = ", frameId + 1);
			player.getPA().sendFrame126("" + total + "", frameId + 2);
		} else {
			player.getPA()
					.sendFrame126(
							""
									+ NumberFormat.getInstance().format((price))
									+ "", frameId);
			player.getPA().sendFrame126("", frameId + 1);
			player.getPA().sendFrame126("", frameId + 2);
		}
	}

	public static void updateChecker(Player c) {
		c.getItems().resetItems(5064);
		for (int x = 0; x < 27; x++) {
			if (c.priceItemN[x] <= 0) {
				itemOnInterface(c, 45542, x, -1, 0);
			} else {
				itemOnInterface(c, 45542, x, c.priceItem[x], c.priceItemN[x]);

				for (int frames = 0; frames < getFramesForSlot.length; frames++) {
					if (x == getFramesForSlot[frames][0]) {
						setFrame(c, x, getFramesForSlot[frames][1], c.priceItem[x],
								c.priceItemN[x], true);
					}
				}
			}
		}
		c.getPA().sendFrame126(
				""
						+ NumberFormat.getInstance().format((c.total < 0 ? 0 : c.total)) + "",
				45513);
	}



	public static void withdrawItem(Player c, int removeId, int slot, int amount) {
		int price = (int) Math.floor(c.getShops().getItemShopValue(removeId));
		if (!c.isChecking)
			return;
		if (c.priceItem[slot] != removeId) {
			return;
		}
		if (!c.getItems().isStackable(c.priceItem[slot]))
			amount = 1;
		if (amount > c.priceItemN[slot] && c.getItems().isStackable(c.priceItem[slot]))
			amount = c.priceItemN[slot];
		if (c.priceItem[slot] >= 0 && c.getItems().freeSlots() > 0) {
			c.getItems().addItem(c.priceItem[slot], amount);
			if (c.getItems().isStackable(c.priceItem[slot])
					&& c.getItems().playerHasItem(c.priceItem[slot], amount)) {
				c.priceItemN[slot] -= amount;
				c.priceItem[slot] = c.priceItemN[slot] <= 0 ? 0 : c.priceItem[slot];
			} else {
				c.priceItemN[slot] = 0;
				c.priceItem[slot] = 0;
			}
		}
		price *= 0.537;
		c.total -= price * amount;

		for (int frames = 0; frames < getFramesForSlot.length; frames++) {
			if (slot == getFramesForSlot[frames][0]) {
				if (c.priceItemN[slot] >= 1) {
					setFrame(c, slot, getFramesForSlot[frames][1],
							c.priceItem[slot], c.priceItemN[slot], true);
				} else {
					setFrame(c, slot, getFramesForSlot[frames][1],
							c.priceItem[slot], c.priceItemN[slot], false);
				}
			}
		}
		updateChecker(c);
	}


	public static void store(Player c) {
		for (SlottedItem item : c.getItems().getInventoryItems())
		if (item != null) {
			depositItem(c, item.getId(), item.getAmount());
		}
	}


}
