package io.xeros.model.items;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.Sounds;
import io.xeros.content.UimStorageChest;
import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.VeracsEffect;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.combat.specials.Specials;
import io.xeros.content.combat.weapon.WeaponData;
import io.xeros.content.combat.weapon.WeaponDataConstants;
import io.xeros.content.items.Degrade.DegradableItem;
import io.xeros.content.lootbag.LootingBagItem;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.sound.Sfx;
import io.xeros.model.Bonus;
import io.xeros.model.Items;
import io.xeros.model.SkillLevel;
import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.group.GroupIronmanBank;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.bank.Bank;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.items.bank.BankTab;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * Indicates Several Usage Of Items
 *
 * @author Sanity Revised by Shawn Notes by Shawn
 */
public class ItemAssistant {
	private static final int IF_INV = 3214;
	private static final int IF_INV_SHOP = 3823;
	private static final int IF_EQUIP = 1688;

	public static final int FIGHT_MODE_CONFIG = 43;

	/**
	 * Item bonuses.
	 **/
	public final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range",
			"Stab", "Slash", "Crush", "Magic", "Range", "Melee Strength", "Ranged Strength", "Magic Damage", "Prayer"
	};

	public static final int[] BONUS_STRING_IDS = {
			1675, 1676, 1677, 1678, 1679, 1680, 1681, 1682, 1683, 1684, 1686, 21451, 21452, 1687
	};


	private final Player player;

	public ItemAssistant(Player client) {
		this.player = client;
	}


	private final Queue<ContainerUpdate> containerUpdates = new ArrayDeque<ContainerUpdate>();

	private void sendInventoryAttrs() {
		// attrs for main inv + shop inv (same slots)
		player.getPA().sendInventoryAttrDbg(player, 3214);
		player.getPA().sendInventoryAttrDbg(player, 3823);
	}

	private void sendEquipmentAttrs() {
		player.getPA().sendEquipmentAttrDbg(player);
	}

	private void sendBankAttrs() {
		// Bank tab containers: attrs live on BankItem (GameItem) instances in each tab.
		for (int i = 0; i < io.xeros.model.items.bank.Bank.ITEM_CONTAINERS.length; i++) {
			int interfaceId = io.xeros.model.items.bank.Bank.ITEM_CONTAINERS[i];
			io.xeros.model.items.bank.BankTab tab = player.getBank().getBankTab()[i];
			player.getPA().sendBankTabAttrDbg(player, interfaceId, tab);
		}

		// Bank inventory container (the inv shown while banking): it's still player inventory attrs, but keyed to interface 5064.
		player.getPA().sendInventoryAttrDbg(player, 5064);
	}


	public void addContainerUpdate(ContainerUpdate containerUpdate) {
		if (!containerUpdates.contains(containerUpdate)) {
			containerUpdates.add(containerUpdate);
		}
	}

	public void processContainerUpdates() {
		while (!containerUpdates.isEmpty()) {
			switch (containerUpdates.poll()) {
				case INVENTORY:
					player.getRunePouch().sendPouchRuneInventory();
					sendInventoryInterface(3214);
					sendInventoryInterface(3823);

					// Vykna attrs: after inv container(s) are sent
					sendInventoryAttrs();
					break;

				case EQUIPMENT:
					updateEquipmentBonusInterface();

					// Vykna attrs: keep your existing packet 34 single-slot updates,
					// but ALWAYS follow with a full equip attrs container push
					sendEquipmentAttrs();
					break;

				case BANK:
					updateBankContainer();
					// Vykna attrs: after bank containers are sent
					sendBankAttrs();
					break;
			}
		}
	}


	public SlottedItem getInventoryItem(int itemId) {
		return getInventoryItems().stream().filter(it -> it.getId() == itemId).findFirst().orElse(null);
	}

	public List<SlottedItem> getInventoryItems() {
		List<SlottedItem> items = Lists.newArrayList();
		for (int index = 0; index < player.playerItems.length; index++) {
			if (player.playerItems[index] > 0) {
				items.add(new SlottedItem(player.playerItems[index] - 1, player.playerItemsN[index], index));
			}
		}
		return items;
	}

	public List<SlottedItem> getEquipmentItems() {
		List<SlottedItem> items = Lists.newArrayList();
		for (int index = 0; index < player.playerEquipment.length; index++) {
			if (player.playerEquipment[index] > 0) {
				items.add(new SlottedItem(player.playerEquipment[index], player.playerEquipmentN[index], index));
			}
		}
		return items;
	}

	/**
	 * Get the inventory and equipment items. Will not stack items of the same id, simply combines them.
	 * Use {@link GameItem#normalize(List, boolean)}} to normalize the items.
	 */
	public List<GameItem> getHeldItems() {
		return Stream.concat(getInventoryItems().stream(), getEquipmentItems().stream())
				.map(SlottedItem::toGameItem)
				.collect(Collectors.toList());
	}

	public List<GameItem> getBankItems() {
		List<GameItem> bankItems = new ArrayList<>();
		for (BankTab tab : player.getBank().getBankTab())
			for (BankItem bankItem : tab.getItems())
				bankItems.add(new GameItem(bankItem.getId() - 1, bankItem.getAmount())); // :(
		return bankItems;
	}

	public List<GameItem> getHeldAndBankedItems() {
		return Stream.concat(getHeldItems().stream(), getBankItems().stream()).collect(Collectors.toList());
	}

	public int getWeapon() {
		List<SlottedItem> equipment = getEquipmentItems();
		Optional<SlottedItem> weapon = equipment.stream().filter(it -> it.getSlot() == Player.playerWeapon).findFirst();
		return weapon.map(SlottedItem::getId).orElse(-1);
	}

	public void setEquipmentUpdateTypes() {
		player.isFullHelm = ItemDef.forId(player.playerEquipment[Player.playerHat]).getEquipmentModelType() == EquipmentModelType.FULL_HELMET;
		player.isFullMask = ItemDef.forId(player.playerEquipment[Player.playerHat]).getEquipmentModelType() == EquipmentModelType.FULL_MASK;
		player.isFullBody = ItemDef.forId(player.playerEquipment[Player.playerChest]).getEquipmentModelType() == EquipmentModelType.FULL_BODY;
	}

	public int[] Nests = { 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304 };

	public int getWornItemSlot(int itemId) {
		for (int i = 0; i < player.playerEquipment.length; i++)
			if (player.playerEquipment[i] == itemId)
				return i;
		return -1;
	}

	/**
	 * Sends an item to the bank in any tab possible.
	 *
	 * @param itemId the item id
	 * @param amount the item amount
	 */
	public void sendItemToAnyTab(int itemId, int amount) {
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.freeSlots() > 0 || tab.contains(item)) {
				player.getBank().setCurrentBankTab(tab);
				addItemToBankOrDrop(itemId, amount);
				return;
			}
		}
		addItemToBankOrDrop(itemId, amount);
	}

	public boolean sendToTab(int tabId, int itemId, int amount) {
		BankTab tab = player.getBank().getBankTab(tabId);
		if (tab.freeSlots() > 0 || tab.contains(new BankItem(itemId))) {
			player.getBank().setCurrentBankTab(tab);
			addItemToBankOrDrop(itemId, amount);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds an item to the players inventory, bank, or drops it. It will do this under any circumstance so if it cannot be added to the inventory it will next try to send it to the
	 * bank and if it cannot, it will drop it.
	 *
	 * @param itemId the item
	 * @param amount the amount of said item
	 */
	public void addItemUnderAnyCircumstance(int itemId, int amount) {
		if (!addItem(itemId, amount)) {
			sendItemToAnyTabOrDrop(new BankItem(itemId, amount), player.getX(), player.getY());
			player.sendMessage("@red@Your item was sent to the bank!");
		}
	}

	/**
	 * The x and y represents the possible x and y location of the dropped item if in fact it cannot be added to the bank.
	 */
	public void sendItemToAnyTabOrDrop(BankItem item, int x, int y) {
		item = new BankItem(item.getId() + 1, item.getAmount());
		if (item.getDef().isNoted() && bankContains(item.getId() - 2)) {
			if (isBankSpaceAvailable(item)) {
				sendItemToAnyTab(item.getId() - 1, item.getAmount());
			} else {
				Server.itemHandler.createGroundItem(player, item.getId() - 1, x, y, player.heightLevel, item.getAmount());
			}
		} else {
			sendItemToAnyTab(item.getId() - 1, item.getAmount());
		}
	}

	/**
	 * Determines if the player is wearing a specific item at a particular slot
	 *
	 * @param itemId the item we're checking to see the player is wearing
	 * @param slot the slot the item should be detected in
	 * @return true if the item is being word
	 */
	public boolean isWearingItem(int itemId, int slot) {

		return slot>=0&&slot<= player.playerEquipment.length-1&& player.playerEquipment[slot]==itemId;
	}

	/**
	 * Check all slots and determine whether or not a slot is accompanied by that item
	 */
	public boolean hasAnyItem(int slot, int... ids) {
		for (int id : ids) {
			if (isWearingItem(id, slot)) return true;
		}
		return false;
	}

	/**
	 * Check all slots and determine whether or not a slot is accompanied by that item
	 */
	public boolean isWearingItem(int itemID) {
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the player is wearing any of the given items.
	 *
	 * @param items the array of item id values.
	 * @return true if the player is wearing any of the optional items.
	 */
	public boolean isWearingAnyItem(int... items) {
		for (int equipmentId : player.playerEquipment) {
			for (int item : items) {
				if (equipmentId == item) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Trimmed and untrimmed skillcapes.
	 */
	public int[][] skillcapes = { { 9747, 9748, 3033 }, // Attack
			{ 9753, 9754, 3034 }, // Defence
			{ 9750, 9751, 3035 }, // Strength
			{ 9768, 9769, 3036 }, // Hitpoints
			{ 9756, 9757, 3037 }, // Range
			{ 9759, 9760, 3038 }, // Prayer
			{ 9762, 9763, 3039 }, // Magic
			{ 9801, 9802, 3040 }, // Cooking
			{ 9807, 9808, 3041 }, // Woodcutting
			{ 9783, 9784, 3042 }, // Fletching
			{ 9798, 9799, 3043 }, // Fishing
			{ 9804, 9805, 3044 }, // Firemaking
			{ 9780, 9781, 3045 }, // Crafting
			{ 9795, 9796, 3046 }, // Smithing
			{ 9792, 9793, 3047 }, // Mining
			{ 9774, 9775, 3048 }, // Herblore
			{ 9771, 9772, 3049 }, // Agility
			{ 9777, 9778, 3050 }, // Thieving
			{ 9786, 9787, 3051 }, // Slayer
			{ 9810, 9811, 3052 }, // Farming
			{ 9765, 9766, 3053 } // Runecraft
	};

	/**
	 * Empties all of (a) player's items.
	 */
	public void sendInventoryInterface(int WriteFrame) {
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeUnsignedWord(WriteFrame);
			player.getOutStream().writeUnsignedWord(player.playerItems.length);
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItemsN[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.playerItemsN[i] == -1 ? 0 : player.playerItemsN[i]);
				} else {
					player.getOutStream().writeByte(player.playerItemsN[i] == -1 ? 0 : player.playerItemsN[i]);
				}

				player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	public void sendImmutableItemContainer(int interfaceId, List<? extends ImmutableItem> container) {
		sendItemContainer(interfaceId, container.stream().map(item -> new GameItem(item.getId(), item.getAmount())).collect(Collectors.toList())
				, true);
	}

	public void sendItemContainer(int interfaceId, List<? extends GameItem> container) {
		sendItemContainer(interfaceId, container, true);
	}

	public void sendItemContainer(int interfaceId, List<? extends GameItem> container, boolean addToItemId) {
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeUnsignedWord(interfaceId);
			player.getOutStream().writeUnsignedWord(container.size());
			for (int i = 0; i < container.size(); i++) {
				GameItem item = container.get(i);
				if (item == null) {
					player.getOutStream().writeByte(0);
					player.getOutStream().writeWordBigEndianA(0);
				} else {
					if (container.get(i).getAmount() > 254) {
						player.getOutStream().writeByte(255);
						player.getOutStream().writeDWord_v2(container.get(i).getAmount() == -1 ? 0 : container.get(i).getAmount());
					} else {
						player.getOutStream().writeByte(container.get(i).getAmount() == -1 ? 0 : container.get(i).getAmount());
					}

					player.getOutStream().writeWordBigEndianA(container.get(i).getId() + (addToItemId ? 1 : 0));
				}
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	public void sendEquipmentContainer() {
		List<GameItem> items = Lists.newArrayList();
		for (int index = 0; index < player.playerEquipment.length; index++) {
			items.add(new GameItem(player.playerEquipment[index], player.playerEquipmentN[index]));
		}
		// equipment should NOT be +1 encoded here
		sendItemContainer(IF_EQUIP, items, false);
	}


	/**
	 * Check to see if an item is noted.
	 *
	 * @param itemId The item ID of the item which is to be checked.
	 * @return True in case the item is noted, False otherwise.
	 */
	public boolean isNoted(int itemId) {
		return ItemDef.forId(itemId).isNoted();
	}

	public void addItemToBankOrDrop(int itemId, int amount) {
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		if (ItemDef.forId(itemId).isNoted()) {
			item = new BankItem(Server.itemHandler.getCounterpart(itemId) + 1, amount);
		}
		Iterator<BankTab> iterator = Arrays.asList(player.getBank().getBankTab()).iterator();
		outer: while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				for (BankItem i : t.getItems()) {
					if (i.stacksWith(item)) {
						if (t.getTabId()!=tab.getTabId()) {
							tab=t;
							break outer;
						}
					}
				}
			}
		}

		if (isNoted(itemId)) {
			item = new BankItem(ItemDef.forId(itemId).getNoteId() + 1, amount);
		}

		if (player.getMode().isUltimateIronman()) {
			if (!player.getItems().hasRoomInInventory(itemId, amount)) {
				Server.itemHandler.createGroundItem(player, itemId, player.absX, player.absY, player.heightLevel, amount, player.getIndex());
				player.sendMessage("Your items was dropped on the floor due to your mode and no inventory room.");
				return;
			}
			player.getItems().addItem(itemId, amount);
			player.sendMessage("Because you are ultimate ironman, your items went into your inventory.");
			return;
		}
		if (tab.freeSlots() == 0) {
			player.sendMessage("The item has been dropped on the floor.");
			Server.itemHandler.createGroundItem(player, itemId, player.absX, player.absY, player.heightLevel, amount,
					player.getIndex());
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.sendMessage("The item has been dropped on the floor.");
			Server.itemHandler.createGroundItem(player, itemId, player.absX, player.absY, player.heightLevel, amount, player.getIndex());
			return;
		}
		tab.add(item);
		resetTempItems();
		if (player.isBanking) {
			queueBankContainerUpdate();
		}
		player.sendMessage(getItemName(itemId) + " x" + item.getAmount() + " has been added to your bank.");
	}

	public void replaceItem(Player c, int i, int l) {
		for (int playerItem : c.playerItems) {
			if (playerHasItem(i, 1)) {
				deleteItem(i, getInventoryItemSlot(i), 1);
				addItem(l, 1);
			}
		}
	}

	public int getBonus(Bonus bonus) {
		if (bonus == Bonus.RANGED_STRENGTH) {
			int weapon = player.getItems().getWeapon();
			int ammoId = player.playerEquipment[Player.playerArrows];
			// Disable attacks for these bows
			if (ammoId != -1) {
				boolean weaponIsAmmo = Arrays.stream(RangeData.OTHER_RANGE_WEAPONS).anyMatch(i -> i == weapon);
				boolean ammoIsRangeAmmo = Arrays.stream(RangeData.ARROWS).anyMatch(i -> i == ammoId)
						|| Arrays.stream(RangeData.OTHER_RANGE_WEAPONS).anyMatch(i -> i == ammoId)
						|| Arrays.stream(RangeData.JAVELINS).anyMatch(i -> i == ammoId);

				if (ammoIsRangeAmmo) {
					if (player.getCombatItems().usingCrystalBow()
							|| player.getCombatItems().usingCrawsBow()
							|| player.getCombatItems().usingBlowPipe()
							|| weaponIsAmmo) {

						return RangeData.getRangeStr(player, Player.playerArrows);
					}
				}
			}

		}

		return player.playerBonus[bonus.ordinal()];
	}

	public void calculateBonuses() {
		Arrays.fill(player.playerBonus, 0);

		for (int playerEquipment : player.playerEquipment) {
			if (playerEquipment > -1) {
				for (int bonusSlot = 0; bonusSlot < player.playerBonus.length; bonusSlot++) {
					ItemStats itemStats = ItemStats.forId(playerEquipment);
					if (itemStats != null && itemStats.getEquipment() != null) {
						player.playerBonus[bonusSlot] += itemStats.getEquipment().getBonus(Bonus.values()[bonusSlot]);
					}
				}
			}
		}

		if (VeracsEffect.INSTANCE.canUseEffect(player)) {
			player.playerBonus[Bonus.PRAYER.ordinal()] += 7;
		}

		addContainerUpdate(ContainerUpdate.EQUIPMENT);
	}

	/**
	 * Gets the bonus' of an item.
	 */
	private void updateEquipmentBonusInterface() {
		for (int i = 0; i < Bonus.values().length; i++) {
			Bonus bonus = Bonus.values()[i];
			int bonusValue = getBonus(bonus);
			String bonusString;
			if (bonus == Bonus.MAGIC_DMG) {
				bonusString = BONUS_NAMES[i] + ": " + bonusValue + "%";
			} else if (bonusValue >= 0) {
				bonusString = BONUS_NAMES[i] + ": +" + bonusValue;
			} else {
				bonusString = BONUS_NAMES[i] + ": -" + Math.abs(bonusValue);
			}

			player.getPA().sendFrame126(bonusString, BONUS_STRING_IDS[i]);
		}
	}

	/**
	 * Get a count of all items with {@param itemId} inside the player's inventory, equipment, and bank.
	 */
	public long getTotalCount(int itemId) {
		List<List<GameItem>> items = List.of(
				getInventoryItems().stream().map(SlottedItem::toGameItem).collect(Collectors.toList()),
				getEquipmentItems().stream().map(SlottedItem::toGameItem).collect(Collectors.toList()),
				getBankItems()
		);

		return items.stream().mapToLong(list -> list.stream().filter(it -> it.getId() == itemId).mapToLong(GameItem::getAmount).sum()).sum();
	}

	/**
	 * Deletes all of a player's items.
	 **/
	public void deleteAllItems() {
		for (int i1 = 0; i1 < player.playerEquipment.length; i1++) {
			deleteEquipment(i1);
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			deleteItem(player.playerItems[i] - 1, getInventoryItemSlot(player.playerItems[i] - 1), player.playerItemsN[i]);
		}
	}

	/**
	 * Counts the number of items a player possesses with given item ID.
	 *
	 * @param itemId The ID of the item we're looking for.
	 * @param includeCounterpart True in case the counterpart (noted/unnoted) should also be included in the count, false otherwise.
	 * @return The amount of items the player possesses.
	 */
	public int getItemCount(int itemId, boolean includeCounterpart) {
		int counter = 0;
		int counterpart = -1;
		if (includeCounterpart) {
			counterpart = Server.itemHandler.getCounterpart(itemId);
		}
		for (LootingBagItem item : player.getLootingBag().getLootingBagContainer().items) {
			if (item.getId() == itemId || counterpart > 0 && item.getId() == counterpart) {
				counter += item.getAmount();
			}
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId + 1 || counterpart > 0 && player.playerItems[i] == counterpart + 1) {
				counter += player.playerItemsN[i];
				if (ItemDef.forId(player.playerItems[i] - 1).isStackable()) {
					break;
				}
			}
		}
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] == itemId || counterpart > 0 && player.playerEquipment[i] == counterpart) {
				counter += player.playerEquipmentN[i];
				if (ItemDef.forId(player.playerEquipment[i] - 1).isStackable()) {
					break;
				}
			}
		}
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab == null) {
				continue;
			}
			for (BankItem item : tab.getItems()) {
				if (item.getId() == itemId + 1 || counterpart > 0 && item.getId() == counterpart + 1) {
					counter += item.getAmount();
					break;
				}
			}
		}
		return counter;
	}

	/**
	 * Handles tradable items.
	 */
	public boolean isTradable(int itemId) {
		if (itemId == 12899 && player.getToxicTridentCharge() > 0 || itemId == 11907 && player.getTridentCharge() > 0) {
			return false;
		}
		if (ItemDef.forId(itemId).getName().contains("graceful")) {
			return false;
		}
		return ItemDef.forId(itemId).isTradable() || itemId == 13307;
	}

	public boolean hasRoomInInventory(int item, int amount) {
		boolean stackable = ItemDef.forId(item).isStackable();

		// If stackable first check for the item and verify the amount will be valid after adding
		if (stackable && this.getItemAmount(item) <= 0 && freeSlots() == 0) {
			return false;
		}
		if (stackable) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] - 1 == item) {
					return (long) amount + (long) player.playerItemsN[i] <= Integer.MAX_VALUE;
				}
			}
			return amount >= 1;
		}

		int freeSlots = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == 0) {
				freeSlots++;
			}
		}

		return freeSlots >= amount;
	}

	public boolean isItemInInventorySlot(int itemId, int slot) {
		GameItem itemInInventorySlot = getItemInInventorySlot(slot);
		return itemInInventorySlot != null && itemInInventorySlot.getId() == itemId;
	}

	public GameItem getItemInInventorySlot(int slot) {
		if (slot < 0 || slot >= player.playerItems.length || player.playerItems[slot] <= 0)
			return null;

		GameItem item = new GameItem(player.playerItems[slot] - 1, player.playerItemsN[slot]);

		// attach per-slot attributes (if any)
		ItemAttributes attrs = player.playerItemAttrs[slot];
		if (attrs != null && player.playerItemAttrHash[slot] != 0) {
			item.setAttrs(attrs.copy());
		}

		return item;
	}


	private boolean updateStackableInventoryItemAmount(GameItem incoming) {
		for (int i = 0; i < player.playerItems.length; i++) {
			GameItem existing = getItemInInventorySlot(i);
			if (existing != null && existing.stacksWith(incoming)) {
				player.playerItemsN[i] += incoming.getAmount();
				addContainerUpdate(ContainerUpdate.INVENTORY);
				return true;
			}
		}
		return false;
	}
	// Legacy overload used all over the file. Keeps old calls compiling.
// NOTE: This stacks only by ID (no attrs) because callers don't pass attrs.
	private boolean updateStackableInventoryItemAmount(int itemId, int amountToAdd) {
		GameItem incoming = new GameItem(itemId, amountToAdd);
		return updateStackableInventoryItemAmount(incoming);
	}

	private void clearInventorySlot(int slot) {
		player.playerItems[slot] = 0;
		player.playerItemsN[slot] = 0;
		player.playerItemAttrs[slot] = null;
		player.playerItemAttrHash[slot] = 0;
	}

	private void setInventorySlot(int slot, int itemIdPlus1, int amount, ItemAttributes attrs) {
		player.playerItems[slot] = itemIdPlus1;
		player.playerItemsN[slot] = amount;
		if (attrs != null) {
			player.playerItemAttrs[slot] = attrs.copy();
			player.playerItemAttrHash[slot] = attrs.computeHash();
		} else {
			player.playerItemAttrs[slot] = null;
			player.playerItemAttrHash[slot] = 0;
		}
	}

	private boolean addInventoryItemToFreeSlot(GameItem gameItem) {
		for (int i = 0; i < player.playerItems.length; i++) {
			GameItem slottedItem = getItemInInventorySlot(i);
			if (slottedItem == null) {
				player.playerItems[i] = gameItem.getId() + 1;
				player.playerItemsN[i] = gameItem.getAmount();
				// write attributes into the parallel arrays
				if (gameItem.hasAttrs()) {
					player.playerItemAttrs[i] = gameItem.getAttrs().copy();
					player.playerItemAttrHash[i] = gameItem.getAttrsHash();
				} else {
					player.playerItemAttrs[i] = null;
					player.playerItemAttrHash[i] = 0;
				}

				addContainerUpdate(ContainerUpdate.INVENTORY);
				return true;
			}
		}

		return false;
	}

	public boolean addItem(int item, int amount) {
		return addItem(item, amount, true);
	}

	/**
	 * Adds an item to a player's inventory. Will not add a partial amount of the item,
	 * it always adds the full item or nothing.
	 * @return true if the item was added in it's entirety
	 */
	public boolean addItem(int item, int amount, boolean sendMessage) {
		if (amount <= 0)
			return false;
		GameItem gameItem = new GameItem(item, amount);
		int currentAmount = getItemAmount(item);

		if (gameItem.getDef().isStackable() && currentAmount > 0) {
			if ((long) currentAmount + (long) amount > Integer.MAX_VALUE) {
				player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
				return false;
			}

			if (!updateStackableInventoryItemAmount(gameItem))
				throw new IllegalStateException("Impossible condition, reinstall existence.");
			return true;
		} else {
			if (gameItem.isStackable()) {
				return addInventoryItemToFreeSlot(gameItem);
			} else {
				if (freeSlots() < gameItem.getAmount()) {
					player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
					return false;
				}
				for (int i = 0; i < gameItem.getAmount(); i++)
					if (!addInventoryItemToFreeSlot(new GameItem(gameItem.getId(), 1)))
						throw new IllegalStateException();
				return true;
			}
		}
	}

	public boolean addItem(GameItem gameItem, boolean sendMessage) {
		if (gameItem == null || gameItem.getAmount() <= 0)
			return false;

		int item = gameItem.getId();
		int amount = gameItem.getAmount();
		int currentAmount = getItemAmount(item);

		if (gameItem.getDef().isStackable() && currentAmount > 0) {
			if ((long) currentAmount + (long) amount > Integer.MAX_VALUE) {
				player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
				return false;
			}

			if (!updateStackableInventoryItemAmount(gameItem))
				throw new IllegalStateException("Impossible condition, reinstall existence.");
			return true;
		} else {
			if (gameItem.isStackable()) {
				return addInventoryItemToFreeSlot(gameItem);
			} else {
				if (freeSlots() < gameItem.getAmount()) {
					player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
					return false;
				}
				for (int i = 0; i < gameItem.getAmount(); i++) {
					GameItem one = new GameItem(gameItem.getId(), 1);
					if (gameItem.hasAttrs()) one.setAttrs(gameItem.getAttrs());
					if (!addInventoryItemToFreeSlot(one))
						throw new IllegalStateException();
				}
				return true;
			}
		}
	}


	/**
	 * @see ItemAssistant#addItemUntilFullReverse(GameItem, int, boolean)
	 */
	public Optional<GameItem> addItemUntilFullReverse(GameItem gameItem) {
		return addItemUntilFullReverse(gameItem, true);
	}

	/**
	 * @see ItemAssistant#addItemUntilFullReverse(GameItem, int, boolean)
	 */
	public Optional<GameItem> addItemUntilFullReverse(GameItem gameItem, boolean sendMessage) {
		return addItemUntilFullReverse(gameItem, Integer.MAX_VALUE, sendMessage);
	}

	/**
	 * Same as {@link ItemAssistant#addItemUntilFull(GameItem, boolean)} but instead of returning the
	 * remaining items (items that couldn't be added), it returns the items that were added.
	 * It will send empty if no items were deleted.
	 */
	public Optional<GameItem> addItemUntilFullReverse(GameItem gameItem, int maxStack, boolean sendMessage) {
		Optional<GameItem> optional = addItemUntilFull(gameItem, maxStack, sendMessage);
		if (optional.isEmpty())
			return Optional.of(new GameItem(gameItem.getId(), gameItem.getAmount()));
		GameItem remaining = optional.get();
		if (remaining.getAmount() == gameItem.getAmount())
			return Optional.empty();
		return Optional.of(new GameItem(gameItem.getId(), gameItem.getAmount() - remaining.getAmount()));
	}

	/**
	 * @see ItemAssistant#addItemUntilFull(GameItem, int, boolean)
	 */
	public Optional<GameItem> addItemUntilFull(GameItem gameItem) {
		return addItemUntilFull(gameItem, true);
	}

	/**
	 * @see ItemAssistant#addItemUntilFull(GameItem, int, boolean)
	 */
	public Optional<GameItem> addItemUntilFull(GameItem gameItem, boolean sendMessage) {
		return addItemUntilFull(gameItem, Integer.MAX_VALUE, sendMessage);
	}

	/**
	 * Add an item until it can no longer be added, returns a game item with the amount that could not be added,
	 * or empty if all was added.
	 * @param gameItem The GameItem to add
	 * @param sendMessage Should a message be sent when there's not enough space
	 * @return An empty optional if all items were added successfully, otherwise an optional of the remaining game items
	 *         (i.e. the items that were not added).
	 */
	public Optional<GameItem> addItemUntilFull(GameItem gameItem, int maxStack, boolean sendMessage) {
		int item = gameItem.getId();
		int amount = gameItem.getAmount();
		gameItem = new GameItem(item, amount);

		if (amount <= 0)
			return Optional.empty();
		int currentAmount = getItemAmount(item);

		if (gameItem.getDef().isStackable() && currentAmount > 0) {
			if (currentAmount >= maxStack) {
				player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
				return Optional.of(gameItem);
			}

			if ((long) currentAmount + (long) amount > maxStack) {
				int difference = maxStack - currentAmount;
				updateStackableInventoryItemAmount(item, difference);
				player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
				return Optional.of(new GameItem(item, amount - difference));
			}

			if (!updateStackableInventoryItemAmount(item, amount))
				throw new IllegalStateException();
			return Optional.empty();
		} else {
			if (gameItem.getDef().isStackable()) {
				if (!addInventoryItemToFreeSlot(gameItem)) {
					player.sendMessageIf(sendMessage, "Not enough space in your inventory.");
					return Optional.of(gameItem);
				}
				return Optional.empty();
			}

			while (gameItem.getAmount() > 0) {
				if (!addInventoryItemToFreeSlot(new GameItem(item, 1)))
					break;
				gameItem.incrementAmount(-1);
			}

			return gameItem.getAmount() == 0 ? Optional.empty() : Optional.of(gameItem);
		}
	}

	/**
	 * Weapon type.
	 *
	 * @param weapon*/
	public void sendWeapon(int weapon) {
		player.getCombatConfigs().updateWeapon();

		WeaponData weaponData = WeaponData.forItemId(weapon);
		player.setSidebarInterface(0, weaponData.getWeaponInterface().getInterfaceId());
		player.getPA().sendString(ItemDef.forId(weapon).getName().startsWith("unknown") ? "Unarmed" : ItemDef.forId(weapon).getName(), weaponData.getWeaponInterface().getNameInterfaceId());
	}

	/**
	 * Two handed weapon check.
	 **/
	public boolean is2handed(String itemName, int itemId) {

		if (itemName.contains("godsword") || itemName.contains("crystal") || itemName.contains("aradomin sword")
				|| itemName.contains("2h") || itemName.contains("spear") || itemName.contains("halberd")
				|| itemName.contains("longbow") || itemName.contains("shortbow") || itemName.contains("ark bow")
				|| itemName.contains("karil") || itemName.contains("verac")
				|| itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag") ||
				itemName.contains("abyssal bludgeon") || itemName.contains("spade") || itemName.contains("casket") ||
				itemName.contains("clueless") || itemName.contains("scythe") || itemName.contains("ballista") || itemName.contains("hunting knife") || itemName.contains("elder maul") || itemName.contains("bulwark") || itemName.contains("claws")) {
			return true;
		}
		switch (itemId) {

			case 25867:// boffa c
			case 20999:// boffa
			case 24111:// Zilyana's godbow
			case 24158://k'ril swords
			case 24106://Vorkath blowpipe
			case 12926:
			case 6724:
			case 11838:
			case 12809:
			case 14484:
			case 4153:
			case 12848:
			case 6528:
			case 10887:
			case 12424:
			case 20784:
			case 20997:
			case 22550:
				return true;
		}
		return false;
	}

	public void addSpecialBar(int weapon) {
		addSpecialBar(weapon, true);
	}

	/**
	 * Adds special attack bar to special attack weapons. Removes special attack bar to weapons that do not have special attacks.
	 **/
	public void addSpecialBar(int weapon, boolean sendWeapon) {
		WeaponData weaponData = WeaponData.forItemId(weapon);
		boolean special = Specials.forWeaponId(weapon) != null;

		if (weaponData.getWeaponInterface().getSpecialBarInterfaceId() != -1) {
			player.getPA().sendInterfaceHidden(special ? 0 : 1, weaponData.getWeaponInterface().getSpecialBarInterfaceId());
		}

		if (special && weaponData.getWeaponInterface().getSpecialBarAmountInterfaceId() != -1) {
			specialAmount(weapon, player.specAmount, weaponData.getWeaponInterface().getSpecialBarAmountInterfaceId(), false);
		}

		if (sendWeapon) {
			sendWeapon(weapon);
		}
	}

	public void updateSpecialBarAmount() {
		int weapon = player.playerEquipment[3];
		WeaponData weaponData = WeaponData.forItemId(weapon);
		boolean special = Specials.forWeaponId(weapon) != null;
		if (special && weaponData.getWeaponInterface().getSpecialBarAmountInterfaceId() != -1) {
			specialAmount(weapon, player.specAmount, weaponData.getWeaponInterface().getSpecialBarAmountInterfaceId(), false);
		}
	}

	/**
	 * Special attack bar filling amount.
	 **/
	public void specialAmount(int weapon, double specAmount, int barId, boolean sendWeapon) {
		player.specBarId = barId;
		player.getPA().sendFrame70(specAmount >= 10 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 9 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 8 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 7 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 6 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 5 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 4 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 3 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 2 ? 500 : 0, 0, (--barId));
		player.getPA().sendFrame70(specAmount >= 1 ? 500 : 0, 0, (--barId));
		updateSpecialBar();
		if (sendWeapon) {
			sendWeapon(weapon);
		}
	}

	/**
	 * Special attack text.
	 **/
	public void updateSpecialBar() {
		String percent = Double.toString(player.specAmount);
		if (percent.contains(".")) {
			percent = percent.replace(".", "");
		}
		if (percent.startsWith("0") && !percent.equals("00")) {
			percent = percent.replace("0", "");
		}
		if (percent.startsWith("0") && percent.equals("00")) {
			percent = percent.replace("00", "0");
		}
		player.getPA().sendSpecialAttack(Integer.valueOf(percent), player.usingSpecial ? 1 : 0);
		player.getPA().sendFrame126(player.usingSpecial ? "@yel@Special Attack (" + percent + "%)" : "@bla@Special Attack (" + percent + "%)", player.specBarId);
	}

	public boolean canEquip(int wearID) {
		ItemDef item = ItemDef.forId(wearID);
		switch (wearID) {

			case 24106://vorkath blow pipe
			case 24107:
				if(player.playerLevel[4] < 75) {
					player.sendMessage("You must have a ranged level of at least 75 to wield the vorkath blowpipe.");
					return false;
				}
				break;

			case 4587:
			case 20000:
				if (player.getQuesting().handleItemClick(wearID)) {
					return false;
				}
				break;

			case 12902:
				if(player.playerLevel[0] < 75) {
					player.sendMessage("You must have a attack level of at least 75 to weild the toxic staff (uncharged)");
					return false;
				}
				if(player.playerLevel[6] < 75) {
					player.sendMessage("You must have a magic level of at least 75 to weild the toxic staff (uncharged)");
					return false;
				}
				break;
			case 13108:
			case 13109:
			case 13110:
			case 13111:
				if(player.playerLevel[5] < 0) {
					player.sendMessage("contact staff if you see this message.");
					//the swords was saying needing 60 prayer but wasnt in files anywhere or json, this fixes it.
					return false;
				}
				break;
			case 21816:
				if(player.braceletEtherCount < 0) {
					player.sendMessage("You have no charges remaining in your bracelet.");
					player.braceletEtherCount = 0;
					return false;
				}
				break;
			case 20727:
				if(player.playerLevel[18] < 55) {
					player.sendMessage("You must have a slayer level of at least 55 to weild the Leaf-bladed battleaxe");
					return false;
				}
				if(player.playerLevel[0] < 65) {
					player.sendMessage("You must have a attack level of at least 65 to weild the Leaf-bladed battleaxe");
					return false;
				}
				break;
			case 21003:
				if(player.playerLevel[0] < 75) {
					player.sendMessage("You must have a attack level of at least 75 to weild the Elder Maul");
					return false;
				}
				if(player.playerLevel[2] < 75) {
					player.sendMessage("You must have a strength level of at least 75 to weild the Elder Maul");
					return false;
				}
				break;
			case Items.THAMMARONS_SCEPTRE:
				if(player.playerLevel[6] < 60) {
					player.sendMessage("You must have a magic level of at least 60 to weild the Thammaron's sceptre");
					return false;
				}
				break;
			case 773:
				if(!player.getRights().isOrInherits(Right.OWNER) && !Server.isDebug()) {
					player.sendMessage("You must be an owner to wear this.");
					return false;
				}
				break;
			case 12249: //its bugged in game and needs removing, so it deletes out inventory if old people still have it
				player.getItems().deleteItem(12249, 28);
				player.getItems().addItem(995, 200000);
				player.sendMessage("@red@This mask was bugged and got deleted.");
				player.sendMessage("@red@You have been awarded 200k coins..");
				break;
			case 21817:
				if(player.braceletEtherCount <=0) {
					player.sendMessage("@red@You have no ether in your bracelet.");
					return false;
				}
				break;
			case 13235:
				if(player.playerLevel[6] < 75) {
					player.sendMessage("You must have a magic level of at least 75 to wear these boots");
					return false;
				}
				if(player.playerLevel[1] < 75) {
					player.sendMessage("You must have a defence level of at least 75 to wear these boots");
					return false;
				}
				break;
			case 13237:
				if(player.playerLevel[4] < 75) {
					player.sendMessage("You must have a ranged level of at least 75 to wear these boots");
					return false;
				}
				if(player.playerLevel[1] < 75) {
					player.sendMessage("You must have a defence level of at least 75 to wear these boots");
					return false;
				}
				break;
			case 13239:
				if(player.playerLevel[1] < 75) {
					player.sendMessage("You must have a defence level of at least 75 to wear these boots");
					return false;
				}
				if(player.playerLevel[2] < 75) {
					player.sendMessage("You must have a strength level of at least 75 to wear these boots");
					return false;
				}
			case 20784:
				if(player.playerLevel[0] < 60) {
					player.sendMessage("You must have a attack level of at least 60 to weild Dragon Claws.");
					return false;
				}
				break;
			case 13199:
			case 12931:
			case 13197:
				if(player.playerLevel[1] < 75) {
					player.sendMessage("You must have a defence level of at least 75 to wear this helm.");
					return false;
				}
				break;
			case 21633:
				if(player.playerLevel[6] < 70) {
					player.sendMessage("You must have a magic level of at least 70 to wear this Ancient Shield");
					return false;
				}
				if(player.playerLevel[1] < 75) {
					player.sendMessage("You must have a defence level of at least 75 to wear this Ancient Shield ");
					return false;
				}
				break;
			case 21298:
			case 21301:
			case 21304:
				if(player.playerLevel[1] < 60) {
					player.sendMessage("You must have a defence level of at least 60 to wear Obsidian Armour.");
					return false;
				}
				break;
			case 19481:
				if(player.playerLevel[4] < 75) {
					player.sendMessage("You must have a ranged level of at least 75 to weild the Heavy Ballista.");
					return false;
				}
				break;
			case 19478:
				if(player.playerLevel[4] < 65) {
					player.sendMessage("You must have a ranged level of at least 65 to weild the Light Ballista.");
					return false;
				}
				break;
			case 13280:
			case 13329:
			case 13337:
			case 21898:
			case 13331:
			case 13333:
			case 13335:
			case 20760:
			case 21285:
			case 21776:
			case 21778:
			case 21780:
			case 21782:
			case 21784:
			case 21786:
				if (!player.maxRequirements(player)) {
					player.sendMessage("You must have maxed out all skills in order to wear the max cape.");
					return false;
				}
				break;

			case 6583:
				if (!player.getPA().morphPermissions()) {
					return false;
				}
				//	for (int i = 0; i <= 12; i++) {
				//	player.setSidebarInterface(i, 6014);
				//	}
				player.npcId2 = 2188;
				player.isNpc = true;
				player.setUpdateRequired(true);
				player.morphed = true;
				player.setAppearanceUpdateRequired(true);
				break;
			case 20005:
			case 20017:
				if (!player.getPA().morphPermissions()) {
					return false;
				}
				///for (int i = 0; i <= 12; i++) {
				//	player.setSidebarInterface(i, 6014);
				//}
				player.npcId2 = wearID == 20017 ? 7315 : 7314;
				player.isNpc = true;
				player.setUpdateRequired(true);
				player.morphed = true;
				player.setAppearanceUpdateRequired(true);
				break;
			case 7927:
				if (!player.getPA().morphPermissions()) {
					return false;
				}
				//	for (int i = 0; i <= 12; i++) {
				//		player.setSidebarInterface(i, 6014);
				//	}
				player.npcId2 = wearID == 7927 ? 5542 : 5541;
				player.isNpc = true;
				player.setUpdateRequired(true);
				player.morphed = true;
				player.setAppearanceUpdateRequired(true);
				break;
			case 1409:
				if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.IBANS_STAFF);
				}
				break;

			case 6570:
				if (Boundary.isIn(player, Boundary.TZHAAR_CITY_BOUNDARY)) {
					if (!player.getDiaryManager().getKaramjaDiary().hasCompleted("HARD")) {
						player.sendMessage("@red@If you complete all the hard tasks in the karamja diary, this will");
						player.sendMessage("@red@count towards your elite tasks!");
					} else {
						player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.EQUIP_FIRE_CAPE);
					}
				}
				break;

			case 10501:
				if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
					return false;
				}
				player.getPA().showOption(3, 0, "Throw-At");
				break;
		}
		for (SkillLevel requirement : item.getRequirements()) {
			if (player.getLevelForXP(player.playerXP[requirement.getSkill().getId()]) < requirement.getLevel()) {
				player.sendMessage("You need " + Misc.anOrA(requirement.getSkill().toString()) + " " + requirement.getSkill().toString()
						+ " level of " + requirement.getLevel() + " to wear this item.");
				return false;
			}
		}
		Optional<DegradableItem> degradable = DegradableItem.forId(wearID);
		if (degradable.isPresent()) {
			if (player.claimDegradableItem[degradable.get().ordinal()]) {
				player.sendMessage("A previous item similar to this has degraded. You must go to the old man");
				player.sendMessage("in edgeville to claim this item.");
				return false;
			}
		}

		int targetSlot = ItemStats.forId(item.getId()).getEquipment().getSlot();
		if (targetSlot == -1) {
			player.sendMessage(item.getName() + " can't be equipped.");
			return false;
		}

		if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
			if (!Objects.isNull(session)) {
				if (targetSlot == Player.playerHat && session.getRules().contains(DuelSessionRules.Rule.NO_HELM)) {
					player.sendMessage("Wearing helmets has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerAmulet && session.getRules().contains(DuelSessionRules.Rule.NO_AMULET)) {
					player.sendMessage("Wearing amulets has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerArrows && session.getRules().contains(DuelSessionRules.Rule.NO_ARROWS)) {
					player.sendMessage("Wearing arrows has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerChest && session.getRules().contains(DuelSessionRules.Rule.NO_BODY)) {
					player.sendMessage("Wearing platebodies has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerFeet && session.getRules().contains(DuelSessionRules.Rule.NO_BOOTS)) {
					player.sendMessage("Wearing boots has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerHands && session.getRules().contains(DuelSessionRules.Rule.NO_GLOVES)) {
					player.sendMessage("Wearing gloves has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerCape && session.getRules().contains(DuelSessionRules.Rule.NO_CAPE)) {
					player.sendMessage("Wearing capes has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerLegs && session.getRules().contains(DuelSessionRules.Rule.NO_LEGS)) {
					player.sendMessage("Wearing platelegs has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerRing && session.getRules().contains(DuelSessionRules.Rule.NO_RINGS)) {
					player.sendMessage("Wearing a ring has been disabled for this duel.");
					return false;
				}
				if (targetSlot == Player.playerWeapon && session.getRules().contains(DuelSessionRules.Rule.NO_WEAPON)) {
					player.sendMessage("Wearing weapons has been disabled for this duel.");
					return false;
				}
				if (session.getRules().contains(DuelSessionRules.Rule.NO_SHIELD)) {
					if (targetSlot == Player.playerShield || targetSlot == Player.playerWeapon && is2handed(getItemName(wearID).toLowerCase(), wearID)) {
						player.sendMessage("Wearing shields and 2handed weapons has been disabled for this duel.");
						return false;
					}
				}
			}
		}

		return true;
	}
	private ItemAttributes getInvAttrsCopy(int invSlot) {
		ItemAttributes a = player.playerItemAttrs[invSlot];
		return a == null ? null : a.copy();
	}

	private int getInvAttrsHash(int invSlot) {
		return player.playerItemAttrHash[invSlot];
	}

	private void clearEquipmentSlotAttrs(int equipSlot) {
		player.playerEquipmentAttrs[equipSlot] = null;
		player.playerEquipmentAttrHash[equipSlot] = 0;
	}

	private void setEquipmentSlotWithAttrs(int equipSlot, int itemId, int amount, ItemAttributes attrs, int hash) {
		player.playerEquipment[equipSlot] = itemId;
		player.playerEquipmentN[equipSlot] = amount;

		if (attrs != null && hash != 0) {
			player.playerEquipmentAttrs[equipSlot] = attrs.copy();
			player.playerEquipmentAttrHash[equipSlot] = hash;
		} else {
			clearEquipmentSlotAttrs(equipSlot);
		}
	}
	private void addRemovedEquipToInventoryIfAny(int itemId, int amount, ItemAttributes attrs, int hash) {
		if (itemId <= 0 || amount <= 0) {
			return;
		}
		GameItem removed = new GameItem(itemId, amount);
		if (attrs != null && hash != 0) {
			removed.setAttrs(attrs);
		}
		addItem(removed, true);
	}

	/**
	 * Wielding items.
	 **/
	public boolean equipItem(int wearID, int slot) {
		int previousWeapon = player.playerEquipment[3];
		player.getPA().stopSkilling();

		// --- Vykna attrs: capture the inventory item's attributes before we move anything
		ItemAttributes invAttrs = getInvAttrsCopy(slot);
		int invHash = getInvAttrsHash(slot);

		int targetSlot = 0;
		boolean canWearItem = true;

		if (player.insidePost) {
			return false;
		}

		if (player.playerItems[slot] != (wearID + 1)) {
			return false;
		}

		ItemDef item = ItemDef.forId(wearID);
		if (item == null) {
			if (wearID == 15098) {
				return false;
			}
			player.sendMessage("This item is currently unwearable.");
			return false;
		}

		targetSlot = ItemStats.forId(item.getId()).getEquipment().getSlot();
		if (targetSlot == -1) {
			if (wearID >= 5509 && wearID <= 5512 || wearID == 21347 || wearID == 15098 || wearID == 11918 || wearID == 13656 || wearID == 7959 || wearID == 7960) {
				return false;
			} else {
				player.sendMessage("This item cannot be worn.");
				return false;
			}
		}

		boolean contains = AgilityHandler.graceful_ids.stream().anyMatch(x -> x == wearID);
		if (contains) {
			player.graceSum();
		}

		if (!this.canEquip(wearID)) {
			return false;
		}

		if (targetSlot == 3) {
			player.setSpellId(-1);
			player.usingMagic = false;
			player.autocasting = false;
			player.autocastId = 0;
			player.usingSpecial = false;
			addSpecialBar(wearID, false);
			player.getPA().resetAutocast(false);
			if (wearID != 4153 && wearID != 12848) {
				player.attacking.reset();
			}
		}

		if (!canWearItem) {
			return false;
		}

		int wearAmount = player.playerItemsN[slot];
		if (wearAmount < 1) {
			return false;
		}

		if (slot < 0 || wearID < 0) {
			return false;
		}

		int toEquip = player.playerItems[slot];
		int toEquipN = player.playerItemsN[slot];

		int toRemove = player.playerEquipment[targetSlot];
		int toRemoveN = player.playerEquipmentN[targetSlot];

		// Capture equipment attrs that are being replaced (so they can be returned to inv)
		ItemAttributes removedEquipAttrs = (player.playerEquipmentAttrs[targetSlot] == null) ? null : player.playerEquipmentAttrs[targetSlot].copy();
		int removedEquipHash = player.playerEquipmentAttrHash[targetSlot];

		boolean stackable = getItemName(toRemove).contains("javelin")
				|| getItemName(toRemove).contains("dart")
				|| getItemName(toRemove).contains("knife")
				|| getItemName(toRemove).contains("bolt")
				|| getItemName(toRemove).contains("arrow")
				|| getItemName(toRemove).contains("Bolt")
				|| getItemName(toRemove).contains("bolts")
				|| getItemName(toRemove).contains("thrownaxe")
				|| getItemName(toRemove).contains("throwing");

		// Stack into existing equipment stack (attrs remain on equipment slot)
		if (toEquip == toRemove + 1 && ItemDef.forId(toRemove).isStackable()) {
			deleteItem(toRemove, getInventoryItemSlot(toRemove), toEquipN);
			player.playerEquipmentN[targetSlot] += toEquipN;
		} else if (targetSlot != 5 && targetSlot != 3) {
			// Normal slots
			if (playerHasItem(toRemove, 1) && stackable) {
				clearInventorySlot(slot);
				addRemovedEquipToInventoryIfAny(toRemove, toRemoveN, removedEquipAttrs, removedEquipHash);
			} else {
				setInventorySlot(slot, toRemove + 1, toRemoveN, removedEquipAttrs);
			}
			setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);

		} else if (targetSlot == 5) {
			// Shield slot logic (handle 2h weapon)
			boolean wearing2h = is2handed(getItemName(player.playerEquipment[Player.playerWeapon]).toLowerCase(), player.playerEquipment[Player.playerWeapon]);
			if (wearing2h) {
				int w = player.playerEquipment[Player.playerWeapon];
				int wN = player.playerEquipmentN[Player.playerWeapon];
				ItemAttributes wAttrs = (player.playerEquipmentAttrs[Player.playerWeapon] == null) ? null : player.playerEquipmentAttrs[Player.playerWeapon].copy();
				int wHash = player.playerEquipmentAttrHash[Player.playerWeapon];

				player.playerEquipment[Player.playerWeapon] = -1;
				player.playerEquipmentN[Player.playerWeapon] = 0;
				clearEquipmentSlotAttrs(Player.playerWeapon);
				updateSlot(Player.playerWeapon);

				// return weapon to inventory if possible
				// (old code used inventory swapping; keep behavior consistent by trying to add)
				addRemovedEquipToInventoryIfAny(w, wN, wAttrs, wHash);
			}

			setInventorySlot(slot, toRemove + 1, toRemoveN, removedEquipAttrs);
			setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);

		} else if (targetSlot == 3) {
			// Weapon slot logic
			boolean is2h = is2handed(getItemName(wearID).toLowerCase(), wearID);
			boolean wearingShield = player.playerEquipment[Player.playerShield] > 0;
			boolean wearingWeapon = player.playerEquipment[Player.playerWeapon] > 0;

			if (is2h) {
				if (wearingShield && wearingWeapon) {
					if (freeSlots() > 0) {
						if (playerHasItem(toRemove, 1) && stackable) {
							clearInventorySlot(slot);
							addRemovedEquipToInventoryIfAny(toRemove, toRemoveN, removedEquipAttrs, removedEquipHash);
						} else {
							setInventorySlot(slot, toRemove + 1, toRemoveN, removedEquipAttrs);
						}

						setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);
						unequipItem(player.playerEquipment[Player.playerShield], Player.playerShield);
					} else {
						player.sendMessage("You do not have enough inventory space to do this.");
						return false;
					}
				} else if (wearingShield && !wearingWeapon) {
					ItemAttributes shieldAttrs = (player.playerEquipmentAttrs[Player.playerShield] == null) ? null : player.playerEquipmentAttrs[Player.playerShield].copy();
					int shieldHash = player.playerEquipmentAttrHash[Player.playerShield];

					setInventorySlot(slot, player.playerEquipment[Player.playerShield] + 1, player.playerEquipmentN[Player.playerShield], shieldAttrs);
					setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);

					player.playerEquipment[Player.playerShield] = -1;
					player.playerEquipmentN[Player.playerShield] = 0;
					clearEquipmentSlotAttrs(Player.playerShield);
					updateSlot(Player.playerShield);
				} else {
					if (playerHasItem(toRemove, 1) && stackable) {
						clearInventorySlot(slot);
						addRemovedEquipToInventoryIfAny(toRemove, toRemoveN, removedEquipAttrs, removedEquipHash);
					} else {
						setInventorySlot(slot, toRemove + 1, toRemoveN, removedEquipAttrs);
					}
					setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);
				}
			} else {
				if (playerHasItem(toRemove, 1) && stackable) {
					clearInventorySlot(slot);
					addRemovedEquipToInventoryIfAny(toRemove, toRemoveN, removedEquipAttrs, removedEquipHash);
				} else {
					setInventorySlot(slot, toRemove + 1, toRemoveN, removedEquipAttrs);
				}
				setEquipmentSlotWithAttrs(targetSlot, toEquip - 1, toEquipN, invAttrs, invHash);
			}
		}

		player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);

		// Update equipment slot packet
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(34);
			player.getOutStream().writeUnsignedWord(1688);
			player.getOutStream().writeByte(targetSlot);
			player.getOutStream().writeUnsignedWord(wearID + 1);

			if (player.playerEquipmentN[targetSlot] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(player.playerEquipmentN[targetSlot]);
			} else {
				player.getOutStream().writeByte(player.playerEquipmentN[targetSlot]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}

		boolean previousIs2h = is2handed(getItemName(previousWeapon).toLowerCase(), wearID);
		if (targetSlot == 5) {
			if ((!player.autocasting || !player.autocastingDefensive) && previousIs2h) {
				sendWeapon(player.playerEquipment[Player.playerWeapon]);
			}
		}
		if (targetSlot == 3) {
			sendWeapon(player.playerEquipment[Player.playerWeapon]);
		}

		player.getPA().requestUpdates();
		MeleeData.setWeaponAnimations(player);
		calculateBonuses();

		if (player.getPosition().inGodwars()) {
			player.updateGodItems();
		}

		player.getPA().sendSound(Sounds.getEquipItemSound(wearID));
		this.addContainerUpdate(ContainerUpdate.EQUIPMENT);
		this.addContainerUpdate(ContainerUpdate.INVENTORY);
		processContainerUpdates();
		return true;
	}


	/**
	 * Indicates the action to wear an item.
	 *
	 */
	public void updateItems() {
		this.addContainerUpdate(ContainerUpdate.INVENTORY);
	}

	public void equipItem(int wearID, int wearAmount, int targetSlot) {
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(34);
			player.getOutStream().writeUnsignedWord(1688);
			player.getOutStream().writeByte(targetSlot);
			player.getOutStream().writeUnsignedWord(wearID + 1);
			if (wearAmount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(wearAmount);
			} else {
				player.getOutStream().writeByte(wearAmount);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}

		player.playerEquipment[targetSlot] = wearID;
		player.playerEquipmentN[targetSlot] = wearAmount;
		clearEquipmentSlotAttrs(targetSlot);
		player.getItems().sendWeapon(player.playerEquipment[Player.playerWeapon]);
		player.updateItems = true;
		player.getItems().calculateBonuses();
		MeleeData.setWeaponAnimations(player);
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	public void refreshAll() {
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
		player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
	}

	/**
	 * Updates the slot when wielding an item.
	 *
	 * @param slot
	 */
	public void updateSlot(int slot) {
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(34);
			player.getOutStream().writeUnsignedWord(1688);
			player.getOutStream().writeByte(slot);
			player.getOutStream().writeUnsignedWord(player.playerEquipment[slot] + 1);
			if (player.playerEquipmentN[slot] > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(player.playerEquipmentN[slot]);
			} else {
				player.getOutStream().writeByte(player.playerEquipmentN[slot]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	/**
	 * Removes a wielded item.
	 **/
	public void unequipItem(int wearID, int slot) {
		player.getPlayerAssistant().resetFollow();
		if (wearID != 2550) {
			player.attacking.reset();
		}
		player.getPA().stopSkilling();
		if(player.insidePost) {
			return;
		}

		if (player.getOutStream() != null && player != null) {
			if (player.playerEquipment[slot] > -1) {
				GameItem removed = new GameItem(player.playerEquipment[slot], player.playerEquipmentN[slot]);
				ItemAttributes eqA = player.playerEquipmentAttrs[slot];
				int eqH = player.playerEquipmentAttrHash[slot];
				if (eqA != null && eqH != 0) removed.setAttrs(eqA.copy());
				if (addItem(removed, true)) {
					player.usingSpecial = false;
					updateSpecialBar();
					player.playerEquipment[slot] = -1;
					player.playerEquipmentN[slot] = 0;
					clearEquipmentSlotAttrs(slot);
					sendWeapon(player.playerEquipment[Player.playerWeapon]);
					calculateBonuses();

					boolean contains = AgilityHandler.graceful_ids.stream().anyMatch(x -> x == wearID);

					if (contains) {
						player.graceSum();
					}
					switch (wearID) {
						case 10501:
							player.getPA().showOption(3, 0, "Null");
							break;
					}
					if (player.getPosition().inGodwars()) {
						player.updateGodItems();
					}
					if (slot == 3) {
						player.getPA().resetAutocast(false);
					}
					MeleeData.setWeaponAnimations(player);
					player.getOutStream().createFrame(34);
					player.getOutStream().writeUnsignedWord(6);
					player.getOutStream().writeUnsignedWord(1688);
					player.getOutStream().writeByte(slot);
					player.getOutStream().writeUnsignedWord(0);
					player.getOutStream().writeByte(0);
					player.flushOutStream();
				}
				player.getPA().sendSound(Sounds.getEquipItemSound(wearID));
				player.setUpdateRequired(true);
				player.setAppearanceUpdateRequired(true);
				player.getItems().setEquipmentUpdateTypes();
			}
		}
	}

	public void updateBankContainer() {
		// Shift tabs
		for (int i = 0; i < player.getBank().getBankTab().length; i++) {
			if (i == 0)
				continue;
			if (i != player.getBank().getBankTab().length - 1
					&& player.getBank().getBankTab()[i].size() == 0
					&& player.getBank().getBankTab()[i + 1].size() > 0) {
				for (BankItem item : player.getBank().getBankTab()[i + 1].getItems()) {
					player.getBank().getBankTab()[i].add(item);
				}
				player.getBank().getBankTab()[i + 1].getItems().clear();
			}
		}

		player.getPA().sendFrame36(1667, 0);

		for (int index = 0; index < Bank.ITEM_CONTAINERS.length; index++) {
			sendItemContainer(Bank.ITEM_CONTAINERS[index], player.getBank().getBankTab()[index].getItems(), false);
		}
	}

	/**
	 * Reseting your bank.
	 */
	public void queueBankContainerUpdate() {
		addContainerUpdate(ContainerUpdate.BANK);
	}

	/**
	 * Resets temporary worn items. Used in minigames, etc
	 */
	public void resetTempItems() {
		// synchronized (c) {
		int itemCount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] > -1) {
				itemCount = i;
			}
		}
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeUnsignedWord(5064);
			player.getOutStream().writeUnsignedWord(itemCount + 1);
			for (int i = 0; i < itemCount + 1; i++) {
				if (player.playerItemsN[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
				} else {
					player.getOutStream().writeByte(player.playerItemsN[i] == -1 ? 0 : player.playerItemsN[i]);
				}
				if (player.playerItems[i] > Configuration.ITEM_LIMIT || player.playerItems[i] < 0) {
					player.playerItems[i] = Configuration.ITEM_LIMIT;
				}
				player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
	}

	public boolean addToBank(int itemId, int amount, boolean updateView) {
		return addToBank(itemId, amount, updateView, false);
	}

	/**
	 * Banking your item.
	 *
	 * @param overrideValidation True to ignore whether the bank is actually open
	 */
	/**
	 * Banking your item.
	 *
	 * Vykna attrs:
	 * - Deposits are split per-inventory-slot so different attribute variants of the same itemId
	 *   do NOT collapse into one stack.
	 * - Deletion is performed against the exact slot we pull from, so we don't accidentally remove
	 *   a different variant.
	 *
	 * @param overrideValidation True to ignore whether the bank is actually open
	 */
	public boolean addToBank(int itemId, int amount, boolean updateView, boolean overrideValidation) {
		if (player.getPA().viewingOtherBank) {
			player.getPA().resetOtherBank();
			return false;
		}
		if (!player.getMode().isBankingPermitted()) {
			if (!UimStorageChest.isStorageItem(player, itemId)) {
				return false;
			}
		}
		if (!overrideValidation && !player.isBanking)
			return false;
		if (amount <= 0)
			return false;
		if (!player.getItems().playerHasItem(itemId))
			return false;
		if (player.getBankPin().requiresUnlock()) {
			queueBankContainerUpdate();
			player.getBankPin().open(2);
			return false;
		}

		// Clamp amount to what the player actually has (across all variants)
		int invAmount = getItemAmount(itemId);
		if (invAmount <= 0) return false;
		if (amount > invAmount) amount = invAmount;

		BankTab preferredTab = player.getBank().getCurrentBankTab();
		int remaining = amount;
		boolean depositedAny = false;

		// Deposit across inventory slots, preserving per-slot attributes.
		for (int invSlot = 0; invSlot < player.playerItems.length && remaining > 0; invSlot++) {
			if (player.playerItems[invSlot] != (itemId + 1)) {
				continue;
			}

			GameItem invItem = getItemInInventorySlot(invSlot);
			if (invItem == null) {
				continue;
			}

			int take = Math.min(remaining, invItem.getAmount());
			if (take <= 0) continue;

			// Build the bank item (bank uses +1 ids)
			int bankIdPlus1 = itemId + 1;
			if (ItemDef.forId(itemId).isNoted()) {
				bankIdPlus1 = Server.itemHandler.getCounterpart(itemId) + 1;
			}
			BankItem bankItem = new BankItem(bankIdPlus1, take);
			if (invItem.hasAttrs()) {
				bankItem.setAttrs(invItem.getAttrs().copy());
			}

			// Choose the tab:
			// - If an exact stack already exists in another tab, deposit into that tab.
			// - Otherwise, deposit into current tab.
			BankTab tab = preferredTab;
			outer: for (BankTab t : player.getBank().getBankTab()) {
				if (t == null || t.size() == 0) continue;
				for (BankItem existing : t.getItems()) {
					if (existing != null && existing.stacksWith(bankItem)) {
						tab = t;
						break outer;
					}
				}
			}

			if (!player.getBank().hasRoomFor(bankItem)) {
				// stop early if bank is full
				break;
			}

			if (tab.freeSlots() == 0 && !tab.contains(bankItem)) {
				player.sendMessage("Your current bank tab is full.");
				break;
			}

			// Max-stack guard (per exact stack)
			if (tab.getItemAmount(bankItem) == Integer.MAX_VALUE) {
				player.sendMessage("Your bank is already holding the maximum amount of " + getItemName(itemId).toLowerCase() + " possible.");
				break;
			}
			long totalAmount = ((long) tab.getItemAmount(bankItem) + (long) bankItem.getAmount());
			if (totalAmount >= Integer.MAX_VALUE) {
				int difference = Integer.MAX_VALUE - tab.getItemAmount(bankItem);
				bankItem.setAmount(difference);
				take = difference;
			}

			// Delete from the exact inventory slot we are pulling from.
			if (player.playerItemsN[invSlot] > take) {
				player.playerItemsN[invSlot] -= take;
				// keep attrs on the slot
			} else {
				clearInventorySlot(invSlot);
			}

			tab.add(bankItem);
			depositedAny = true;
			remaining -= take;
		}

		if (!depositedAny) {
			return false;
		}

		addContainerUpdate(ContainerUpdate.INVENTORY);
		if (updateView) {
			resetTempItems();
			queueBankContainerUpdate();
		}
		return true;
	}

	public boolean bankContains(int itemId) {
		for (BankTab tab : player.getBank().getBankTab())
			if (tab.contains(new BankItem(itemId + 1)))
				return true;
		return false;
	}

	public boolean bankContains(int itemId, int itemAmount) {
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.containsAmount(new BankItem(itemId + 1, itemAmount))) {
				return true;
			}
		}
		return false;
	}

	public boolean isBankSpaceAvailable(BankItem item) {
		for (BankTab tab : player.getBank().getBankTab()) {
			if (tab.contains(item)) {
				return tab.spaceAvailable(item);
			}
		}
		return false;
	}

	public boolean removeFromAnyTabWithoutAdding(int interfaceId, int slot, int itemAmount, boolean updateView) {
		BankTab tab = player.getBank().getBankTabForInterfaceId(interfaceId);
		if (tab == null) return false;
		if (slot < 0 || slot >= tab.size()) return false;

		BankItem bankItem = tab.getItem(slot);
		if (bankItem == null) return false;

		int removeAmt = Math.min(itemAmount, bankItem.getAmount());
		if (removeAmt <= 0) return false;

		BankItem toRemove = new BankItem(bankItem.getId(), removeAmt);
		if (bankItem.hasAttrs()) toRemove.setAttrs(bankItem.getAttrs());

		tab.remove(toRemove, 0, player.placeHolders);

		if (tab.size() == 0) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
		}
		if (updateView) queueBankContainerUpdate();
		player.getItems().sendInventoryInterface(5064);
		return true;
	}


	public void removeFromBank(BankTab tab, int itemId, int itemAmount, boolean updateView) {
		// Legacy withdraw call (no slot given). Prefer removeFromBankSlot(...).
		// We choose the first matching stack by itemId so we can preserve attrs.
		if (tab == null) {
			return;
		}
		int slot = -1;
		for (int i = 0; i < tab.size(); i++) {
			BankItem bi = tab.getItem(i);
			if (bi != null && bi.getId() == itemId + 1) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return;
		}
		removeFromBankSlot(tab, slot, itemAmount, updateView);
	}

	/**
	 * Withdraw by bank slot so we can preserve per-item attributes.
	 * This is REQUIRED when multiple stacks of the same item id exist in a tab.
	 */
	public void removeFromBankSlot(BankTab tab, int slot, int itemAmount, boolean updateView) {
		if (!player.isBanking) {
			return;
		}
		if (tab == null || slot < 0 || slot >= tab.size() || itemAmount <= 0) {
			return;
		}
		if (player.getPA().viewingOtherBank) {
			player.getPA().resetOtherBank();
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			queueBankContainerUpdate();
			player.getBankPin().open(2);
			return;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.getPA().closeAllWindows();
			return;
		}

		BankItem bankItem = tab.getItem(slot);
		if (bankItem == null) {
			return;
		}

		// Placeholder release (amount 0)
		if (bankItem.getAmount() == 0) {
			if (!player.placeHolderWarning) {
				player.lastPlaceHolderWarning = bankItem.getId();
				player.sendMessage("@cr10@@red@Are you sure you want to release the placeholder of "
						+ ItemDef.forId(bankItem.getId() - 1).getName() + "?");
				player.sendMessage("@cr10@@red@If so, click the item once more.");
				player.placeHolderWarning = true;
				return;
			}
			if (bankItem.getId() != player.lastPlaceHolderWarning) {
				player.placeHolderWarning = false;
				return;
			}
			player.placeHolderWarning = false;
			// remove placeholder (type=1)
			tab.removeAtSlot(slot, itemAmount, player.placeHolders);

			if (updateView) {
				queueBankContainerUpdate();
			}
			player.getItems().sendInventoryInterface(5064);
			return;
		}

		boolean notedWithdraw = false;
		int baseItemId = bankItem.getId() - 1;
		if (player.takeAsNote) {
			// Don’t allow withdrawing augmented items as notes; it breaks identity.
			if (bankItem.hasAttrs()) {
				player.sendMessage("You can't withdraw augmented items as notes.");
				notedWithdraw = false;
			} else if (!ItemDef.forId(baseItemId).isNoted() && Server.itemHandler.getCounterpart(baseItemId) > 0) {
				notedWithdraw = true;
			} else {
				player.sendMessage("This item can't be withdrawn as a note.");
			}
		}

		int withdrawAmount = Math.min(itemAmount, bankItem.getAmount());
		if (withdrawAmount <= 0) {
			return;
		}

		// Inventory capacity checks
		int withdrawId = notedWithdraw ? Server.itemHandler.getCounterpart(baseItemId) : baseItemId;
		if (getItemAmount(withdrawId) == Integer.MAX_VALUE) {
			player.sendMessage("Your inventory is already holding the maximum amount of "
					+ getItemName(withdrawId).toLowerCase() + " possible.");
			return;
		}
		boolean stackable = isStackable(withdrawId);
		if (!stackable && freeSlots() < withdrawAmount) {
			withdrawAmount = freeSlots();
		}
		if (withdrawAmount <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return;
		}

		GameItem withdrawing = new GameItem(withdrawId, withdrawAmount);
		if (!notedWithdraw && bankItem.hasAttrs()) {
			withdrawing.setAttrs(bankItem.getAttrs());
		}
		if (!addItem(withdrawing, true)) {
			return;
		}

		BankItem toRemove = new BankItem(bankItem.getId(), withdrawAmount);
		if (bankItem.hasAttrs()) {
			toRemove.setAttrs(bankItem.getAttrs());
		}
		tab.remove(toRemove, 0, player.placeHolders);

		if (tab.size() == 0) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
		}
		if (updateView) {
			queueBankContainerUpdate();
		}
		player.getItems().sendInventoryInterface(5064);
	}
	public boolean addToBankFromSlot(int fromSlot, int itemId, int amount, boolean updateView, boolean overrideValidation) {
		if (amount <= 0) return false;
		if (fromSlot < 0 || fromSlot >= player.playerItems.length) return false;

		// slot must contain that item
		if (player.playerItems[fromSlot] != itemId + 1) return false;

		int remaining = amount;

		// 1) consume the clicked slot first
		remaining = depositFromInvSlot(fromSlot, itemId, remaining, updateView, overrideValidation);
		if (remaining <= 0) return true;

		// 2) then consume other slots (old behaviour)
		for (int s = 0; s < player.playerItems.length; s++) {
			if (s == fromSlot) continue;
			if (player.playerItems[s] != itemId + 1) continue;

			remaining = depositFromInvSlot(s, itemId, remaining, updateView, overrideValidation);
			if (remaining <= 0) break;
		}

		return remaining != amount;
	}

	/**
	 * Moves up to 'remaining' from a single inventory slot into the bank, preserving attrs.
	 * Returns new remaining amount after deposit from this slot.
	 */
	private int depositFromInvSlot(int invSlot, int itemId, int remaining, boolean updateView, boolean overrideValidation) {
		if (remaining <= 0) return 0;

		if (player.getPA().viewingOtherBank) {
			player.getPA().resetOtherBank();
			return remaining;
		}
		if (!overrideValidation && !player.isBanking) return remaining;
		if (invSlot < 0 || invSlot >= player.playerItems.length) return remaining;

		if (player.playerItems[invSlot] != (itemId + 1)) return remaining;

		int inSlotAmt = player.playerItemsN[invSlot];
		if (inSlotAmt <= 0) return remaining;

		int move = Math.min(remaining, inSlotAmt);
		if (move <= 0) return remaining;

		// Build the bank item (+1 ids in bank)
		int bankIdPlus1 = itemId + 1;
		if (ItemDef.forId(itemId).isNoted()) {
			bankIdPlus1 = Server.itemHandler.getCounterpart(itemId) + 1;
		}

		BankItem bankItem = new BankItem(bankIdPlus1, move);

		// Attach attrs from THIS inventory slot (if any)
		if (player.playerItemAttrHash != null
				&& player.playerItemAttrs != null
				&& player.playerItemAttrHash[invSlot] != 0
				&& player.playerItemAttrs[invSlot] != null) {
			bankItem.setAttrs(player.playerItemAttrs[invSlot]); // BankItem.setAttrs copies + hashes
		}

		// Pick tab: existing exact stack wins, else current tab
		BankTab preferredTab = player.getBank().getCurrentBankTab();
		BankTab tab = preferredTab;

		outer:
		for (BankTab t : player.getBank().getBankTab()) {
			if (t == null || t.size() == 0) continue;
			for (BankItem existing : t.getItems()) {
				if (existing != null && existing.stacksWith(bankItem)) {
					tab = t;
					break outer;
				}
			}
		}

		if (!player.getBank().hasRoomFor(bankItem)) return remaining;
		if (tab.freeSlots() == 0 && !tab.contains(bankItem)) return remaining;

		// Cap to max stack
		long total = (long) tab.getItemAmount(bankItem) + (long) bankItem.getAmount();
		if (total >= Integer.MAX_VALUE) {
			int diff = Integer.MAX_VALUE - tab.getItemAmount(bankItem);
			if (diff <= 0) return remaining;
			bankItem.setAmount(diff);
			move = diff;
		}

		// Remove from *this* inv slot
		if (player.playerItemsN[invSlot] > move) {
			player.playerItemsN[invSlot] -= move; // keep slot attrs
		} else {
			clearInventorySlot(invSlot);
		}

		tab.add(bankItem);

		addContainerUpdate(ContainerUpdate.INVENTORY);
		if (updateView) {
			resetTempItems();
			queueBankContainerUpdate();
		}

		return remaining - move;
	}

	public boolean addEquipmentToBank(int itemId, int slot, int amount, boolean updateView) {
		return addEquipmentToBank(itemId, slot, amount, updateView, false);
	}

	/**
	 * @param overrideValidation True to ignore if the bank is open or not.
	 */
	public boolean addEquipmentToBank(int itemId, int slot, int amount, boolean updateView, boolean overrideValidation) {
		if (player.getPA().viewingOtherBank) {
			player.getPA().resetOtherBank();
			return false;
		}
		if (!overrideValidation && !player.isBanking)
			return false;
		if (player.playerEquipment[slot] != itemId || player.playerEquipmentN[slot] <= 0)
			return false;
		BankTab tab = player.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		if (ItemDef.forId(itemId).isNoted()) {
			item = new BankItem(Server.itemHandler.getCounterpart(itemId) + 1, amount);
		}

		if (!player.getBank().hasRoomFor(item)) {
			return false;
		}

		Iterator<BankTab> iterator = Arrays.asList(player.getBank().getBankTab()).iterator();
		outer: while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				for (BankItem i : t.getItems()) {
					if (i.stacksWith(item)) {
						if (t.getTabId()!=tab.getTabId()) {
							tab=t;
							break outer;
						}
					}
				}
			}
		}

		if (item.getAmount() > player.playerEquipmentN[slot])
			item.setAmount(player.playerEquipmentN[slot]);
		if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
			player.sendMessage("Your bank is already holding the maximum amount of " + getItemName(itemId).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots() == 0 && !tab.contains(item)) {
			player.sendMessage("Your current bank tab is full.");
			return false;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			player.sendMessage("Your bank is already holding the maximum amount of this item.");
			return false;
		} else
			player.playerEquipmentN[slot] -= item.getAmount();
		if (player.playerEquipmentN[slot] <= 0) {
			player.playerEquipmentN[slot] = -1;
			player.playerEquipment[slot] = -1;
		}
		tab.add(item);
		if (updateView) {
			resetTempItems();
			queueBankContainerUpdate();
			updateSlot(slot);
		}
		return true;
	}

	/**
	 * Checks if the item is stackable.
	 *
	 * @param itemID
	 * @return
	 */
	public boolean isStackable(int itemID) {
		return ItemDef.forId(itemID).isStackable();
	}

	public void setEquipmentSlot(int slot, GameItem gameItem) {
		if (gameItem == null) {
			setEquipment(-1, 0, slot, true);
			return;
		}

		setEquipment(gameItem.getId(), gameItem.getAmount(), slot, true);
	}

	/**
	 * Updates the equipment tab.
	 **/
	public void setEquipment(int wearID, int amount, int targetSlot, boolean calculateBonuses) {
		player.getItems().setEquipmentUpdateTypes();
		if (player.getOutStream() != null) {
			player.getOutStream().createFrameVarSizeWord(34);
			player.getOutStream().writeUnsignedWord(1688);
			player.getOutStream().writeByte(targetSlot);
			player.getOutStream().writeUnsignedWord(wearID + 1);
			if (amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
		player.playerEquipment[targetSlot] = wearID;
		player.playerEquipmentN[targetSlot] = amount;
		clearEquipmentSlotAttrs(targetSlot);
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
		if (calculateBonuses) {
			calculateBonuses();
		}
	}

	public void swapBankItem(BankTab tab, int from, int to) {
		BankItem item = tab.getItem(from);
		tab.setItem(from, tab.getItem(to));
		tab.setItem(to, item);
	}

	public void moveItems(int from, int to, int moveWindow, boolean insertMode) {
		if (GroupIronmanBank.swap(player, moveWindow, insertMode, from, to))
			return;
		if (player.getBank().moveItems(from, to, moveWindow, insertMode)) {
			return;
		}
		if (moveWindow == 3214) {
			int tempI = player.playerItems[from];
			int tempN = player.playerItemsN[from];
			ItemAttributes tempA = player.playerItemAttrs[from];
			int tempH = player.playerItemAttrHash[from];

			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItemAttrs[from] = player.playerItemAttrs[to];
			player.playerItemAttrHash[from] = player.playerItemAttrHash[to];

			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
			player.playerItemAttrs[to] = tempA;
			player.playerItemAttrHash[to] = tempH;

		}
		if (moveWindow == 5064) {
			int tempI = player.playerItems[from];
			int tempN = player.playerItemsN[from];
			ItemAttributes tempA = player.playerItemAttrs[from];
			int tempH = player.playerItemAttrHash[from];

			player.playerItems[from] = player.playerItems[to];
			player.playerItemsN[from] = player.playerItemsN[to];
			player.playerItemAttrs[from] = player.playerItemAttrs[to];
			player.playerItemAttrHash[from] = player.playerItemAttrHash[to];

			player.playerItems[to] = tempI;
			player.playerItemsN[to] = tempN;
			player.playerItemAttrs[to] = tempA;
			player.playerItemAttrHash[to] = tempH;

			this.addContainerUpdate(ContainerUpdate.INVENTORY);
		}
		resetTempItems();
		if (moveWindow == 3214) {
			this.addContainerUpdate(ContainerUpdate.INVENTORY);
		}

	}

	/**
	 * Delete item equipment.
	 **/
	public void deleteEquipment(int index) {
		if (PlayerHandler.players[player.getIndex()] == null) {
			return;
		}


		player.playerEquipment[index] = -1;
		player.playerEquipmentN[index] = 0;
		clearEquipmentSlotAttrs(index);
		player.getOutStream().createFrame(34);
		player.getOutStream().writeUnsignedWord(6);
		player.getOutStream().writeUnsignedWord(1688);
		player.getOutStream().writeByte(index);
		player.getOutStream().writeUnsignedWord(0);
		player.getOutStream().writeByte(0);
		if (index == Player.playerWeapon) {
			sendWeapon(-1);
		}
		calculateBonuses();
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}

	/**
	 * Delete items.
	 *
	 * @param id
	 * @param amount
	 */
	public void deleteItem(int id, int amount) {
		deleteItem(id, getInventoryItemSlot(id), amount);
	}

	public void deleteItem(SlottedItem item) {
		deleteItem(item.getId(), item.getSlot(), item.getAmount());
	}

	public void deleteItem(int id, int slot, int amount) {
		if (id <= 0 || slot < 0 || amount < 1) {
			return;
		}
		if (player.playerItems[slot] == (id + 1)) {
			if (player.playerItemsN[slot] > amount) {
				player.playerItemsN[slot] -= amount;
			} else {
				clearInventorySlot(slot);
			}
			this.addContainerUpdate(ContainerUpdate.INVENTORY);
		}
	}

	public List<Integer> deleteItemAndReturnAmount(int id, int amount) {
		if (id <= 0) {
			return null;
		}
		int count = 0;
		List<Integer> amountsToReturn = new ArrayList<>();
		for (int j = 0; j < player.playerItems.length; j++) {
			if (count >= amount) {
				break;
			}
			if (player.playerItems[j] == (id + 1)) {
				if (player.playerItemsN[j] > amount) {
					player.playerItemsN[j] -= amount;
					count += amount;
					amountsToReturn.add(amount);
				} else {
					count += player.playerItemsN[j];
					amountsToReturn.add(player.playerItemsN[j]);
					clearInventorySlot(j);

				}
			}
		}
		this.addContainerUpdate(ContainerUpdate.INVENTORY);
		PlayerSave.saveGame(player);

		return amountsToReturn;
	}

	public void deleteItemNoSave(int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (player.playerItems[slot] == (id + 1)) {
			if (player.playerItemsN[slot] > amount) {
				player.playerItemsN[slot] -= amount;
			} else {
				clearInventorySlot(slot);
			}
			this.addContainerUpdate(ContainerUpdate.INVENTORY);
		}
	}

	public void deleteItem2(int id, int amount) {
		int am = amount;
		for (int slot = 0; slot < player.playerItems.length; slot++) {
			if (am == 0) {
				break;
			}
			if (player.playerItems[slot] == (id + 1)) {
				if (player.playerItemsN[slot] > amount) {
					player.playerItemsN[slot] -= amount;
					break;
				} else {
					clearInventorySlot(slot);
					am--;
				}
			}
		}
		this.addContainerUpdate(ContainerUpdate.INVENTORY);
	}

	/**
	 * Delete arrows.
	 **/
	public void deleteArrow() {
		if (player.getItems().isWearingItem(10033) || player.getItems().isWearingItem(10034)
				|| player.getItems().isWearingItem(11959)) {
			int chinchompa = player.playerEquipment[Player.playerWeapon];
			int chinStock = player.playerEquipmentN[Player.playerWeapon];
			int chinSlot = Player.playerWeapon;

			if (chinStock > 1) {
				player.getItems().equipItem(chinchompa, chinStock - 1, chinSlot);
			} else if (chinStock == 1) {
				player.getItems().equipItem(-1, 0, chinSlot);
			}
			return;
		}
		if (player.getItems().isWearingItem(12926)) {//blowpipe
			return;
		}
		if (player.getItems().isWearingItem(24106)) {//vorkath blowpipe
			return;
		}
		if (player.getItems().isWearingItem(10499, player.playerCape) || player.getItems().isWearingItem(22109, player.playerCape) ||SkillcapePerks.RANGING.isWearing(player)
				|| player.getItems().isWearingItem(33037, player.playerCape)|| SkillcapePerks.isWearingMaxCape(player) && !player.getItems().isWearingItem(4734) &&
				!player.getItems().isWearingItem(10033) && !player.getItems().isWearingItem(10034) && !player.getItems().isWearingItem(11959)) {
			if (RandomUtils.nextInt(0, 15) > 1) {
				return;
			}
		}
		int arrow = player.playerEquipment[Player.playerArrows];
		int stock = player.playerEquipmentN[Player.playerArrows];
		int slot = Player.playerArrows;

		if (stock > 1) {
			player.getItems().equipItem(arrow, stock - 1, slot);
		} else if (stock == 1) {
			player.getItems().equipItem(-1, 0, slot);
		}
	}

	public void deleteEquipment() {
		int thrownweapons = player.playerEquipment[3];
		if (player.getItems().isWearingItem(10033) || player.getItems().isWearingItem(10034)
				|| player.getItems().isWearingItem(11959)) {
			int chinchompa = player.playerEquipment[Player.playerWeapon];
			int chinStock = player.playerEquipmentN[Player.playerWeapon];
			int chinSlot = Player.playerWeapon;

			if (chinStock > 1) {
				player.getItems().equipItem(chinchompa, chinStock - 1, chinSlot);
			} else if (chinStock == 1) {
				player.getItems().equipItem(-1, 0, chinSlot);
			}
			return;
		}

		if (player.getItems().isWearingItem(12926)) {//blowpipe
			return;
		}
		if (player.getItems().isWearingItem(24106)) {//vorkath blowpipe
			return;
		}

		if ( (Arrays.stream(WeaponDataConstants.THROWN).anyMatch(id -> id == thrownweapons) || player.usingOtherRangeWeapons) && !player.getItems().isWearingItem(Items.TOXIC_BLOWPIPE) || !player.getItems().isWearingItem(24106)) {
			if (player.getItems().isWearingItem(10499, player.playerCape) || player.getItems().isWearingItem(22109, player.playerCape)
					|| player.getItems().isWearingItem(33037, player.playerCape) || SkillcapePerks.RANGING.isWearing(player)
					|| SkillcapePerks.isWearingMaxCape(player) && !player.getItems().isWearingItem(4734)) {
				if (RandomUtils.nextInt(0, 15) > 1) {
					return;
				}
			}

			int weapon = player.playerEquipment[Player.playerWeapon];
			int stock = player.playerEquipmentN[Player.playerWeapon];
			int slot = Player.playerWeapon;
			if (stock > 1) {
				player.getItems().equipItem(weapon, stock - 1, slot);
			} else if (stock == 1) {
				player.getItems().equipItem(-1, 0, slot);
			}
		}
	}
	/**
	 * Checks if you have a free slot.
	 *
	 * @return
	 */
	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public int freeEquipmentSlots() {
		int slots = 0;
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] <= 0) {
				slots++;
			}
		}
		return slots;
	}

	public boolean isWearingItems() {
		return freeEquipmentSlots() < 14;
	}

	/**
	 * Gets the item name from the item.cfg
	 *
	 * @return
	 */
	public static String getItemName(int itemId) {
		return ItemDef.forId(itemId).getName();
	}

	/**
	 * Gets the item ID from the item.cfg
	 *
	 * @param itemName
	 * @return
	 */
	public int getItemId(String itemName) {
		Optional<ItemDef> item = ItemDef.getDefinitions().values().stream().filter(def -> def.getName().equalsIgnoreCase(itemName)).findFirst();
		return item.isPresent() ? item.get().getId() : -1;
	}

	/**
	 * Gets the item slot.
	 *
	 * @param ItemID
	 * @return
	 */
	public int getInventoryItemSlot(int ItemID) {
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	public void setInventoryItemSlot(int slot, GameItem gameItem) {
		if (gameItem == null) {
			clearInventorySlot(slot);
			addContainerUpdate(ContainerUpdate.INVENTORY);
			return;
		}
		setInventorySlot(slot, gameItem.getId() + 1, gameItem.getAmount(), gameItem.hasAttrs() ? gameItem.getAttrs() : null);
		addContainerUpdate(ContainerUpdate.INVENTORY);
	}


	public void setInventoryItemSlot(int slot, int itemId) {
		setInventorySlot(slot, itemId + 1, player.playerItemsN[slot], null);
		addContainerUpdate(ContainerUpdate.INVENTORY);
	}


	public void setInventoryItemSlot(int slot, int itemId, int amount) {
		setInventorySlot(slot, itemId + 1, amount, null);
		addContainerUpdate(ContainerUpdate.INVENTORY);
	}

	/**
	 * Gets the item amount.
	 *
	 * @param ItemID
	 * @return
	 */
	public int getItemAmount(int ItemID) {
		int itemCount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == ItemID) {
				itemCount += player.playerItemsN[i];
			}
		}
		return itemCount;
	}

	/**
	 * Get a count of items inside the player's inventory.
	 */
	public int getInventoryCount(int itemId) {
		return getInventoryItems().stream().filter(it -> it.getId() == itemId).mapToInt(SlottedItem::getAmount).sum();
	}

	/**
	 * Checks if the player has the item.
	 *
	 * @param itemID
	 * @param amt
	 * @param slot
	 * @return
	 */
	public boolean playerHasItem(int itemID, int amt, int slot) {
		if (slot >= player.playerItems.length)
			return false;
		itemID++;
		int found = 0;
		if (player.playerItems[slot] == (itemID)) {
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == itemID) {
					if (player.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			return found>=amt;
		}
		return false;
	}

	public boolean playerHasItem(int itemID) {
		itemID++;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemID)
				return true;
		}
		return false;
	}

	public boolean playerHasAllItems(int[] items) {

		for (int item : items) {
			if (!playerHasItem(item))
				return false;
		}
		return true;
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemID) {
				if (player.playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		return found>=amt;
	}

	/**
	 * Check if an item is contained in inventory, equipment or bank.
	 * Doesn't support amounts simply because I didn't have the functions available.
	 * @param gameItems The game item ids.
	 * @return <code>true</code> if player has all items anywhere.
	 */
	public boolean hasAnywhere(int...gameItems) {
		return Arrays.stream(gameItems).allMatch(it -> hasItemOnOrInventory(it) || player.getBank().containsItem(it));
	}

	public boolean hasItemOnOrInventory(int... items) {
		for (int i = 0; i < items.length; i++) {
			if (player.getItems().playerHasItem(items[i]))
				return true;
		}
		for (int equipmentId : player.playerEquipment) {
			for (int item : items) {
				if (equipmentId == item) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Getting un-noted items.
	 *
	 * @param ItemID
	 * @return
	 */
	public int getUnnotedItem(int ItemID) {
		return ItemDef.forId(ItemID).getNoteId();
	}

	public void createGroundItem(GroundItem groundItem) {
		if (player.getOutStream() != null) {
			player.getLocalGroundItems().add(groundItem);
			player.getOutStream().createFrame(85);
			player.getOutStream().writeByteC((groundItem.getY() - 8 * player.mapRegionY));
			player.getOutStream().writeByteC((groundItem.getX() - 8 * player.mapRegionX));
			player.getOutStream().createFrame(44);
			player.getOutStream().writeWordBigEndianA(groundItem.getId());
			player.getOutStream().writeDWord_v1(groundItem.getAmount());
			player.getOutStream().writeByte(0);
			player.flushOutStream();
		}
	}

	public void removeGroundItem(GroundItem groundItem) {
		removeGroundItem(groundItem, true);
	}


	public void removeGroundItem(GroundItem groundItem, boolean removeFromLocalList) {
		if (player.getOutStream() != null) {
			if (removeFromLocalList)
				player.getLocalGroundItems().remove(groundItem);
			player.getOutStream().createFrame(85);
			player.getOutStream().writeByteC((groundItem.getY() - 8 * player.mapRegionY));
			player.getOutStream().writeByteC((groundItem.getX() - 8 * player.mapRegionX));
			player.getOutStream().createFrame(156);
			player.getOutStream().writeByteS(0);
			player.getOutStream().writeUnsignedWord(groundItem.getId());
			player.flushOutStream();
		}
	}

	/**
	 * Handles Amethyst crafting
	 */
	public void handleAmethyst() {
		if (!player.boltTips&&!player.arrowTips&&!player.javelinHeads&&!player.dartTips) {
			player.sendMessage("@pur@Right click your Amethyst to set its crafting method.");
		} else if (player.boltTips) {
			if (player.playerLevel[Skill.CRAFTING.getId()] < 83) {
				player.sendMessage("You need 83 crafting to do this.");
				return;
			}
			player.getItems().deleteItem(21347, 1);
			player.getItems().addItem(21338, 15);
			player.getPA().addSkillXPMultiplied(60, 12, true);
			player.sendMessage("You make some amethyst bolt tips.");
		} else if (player.arrowTips) {
			if (player.playerLevel[Skill.CRAFTING.getId()] < 85) {
				player.sendMessage("You need 85 crafting to do this.");
				return;
			}
			player.getItems().deleteItem(21347, 1);
			player.getItems().addItem(21350, 15);
			player.getPA().addSkillXPMultiplied(60, 12, true);
			player.sendMessage("You make some amethyst arrowtips.");
		} else if (player.javelinHeads) {
			if (player.playerLevel[Skill.CRAFTING.getId()] < 87) {
				player.sendMessage("You need 87 crafting to do this.");
				return;
			}
			player.getItems().deleteItem(21347, 1);
			player.getItems().addItem(21352, 5);
			player.getPA().addSkillXPMultiplied(60, 12, true);
			player.sendMessage("You make some amethyst javelin heads.");

		} else if (player.dartTips) {
			if (player.playerLevel[Skill.CRAFTING.getId()] < 89) {
				player.sendMessage("You need 89 crafting to do this.");
				return;
			}
			player.getItems().deleteItem(21347, 1);
			player.getItems().addItem(25853, 8);
			player.getPA().addSkillXPMultiplied(60, 12, true);
			player.sendMessage("You make some amethyst dart tps.");

		} else {
			player.sendMessage("Error. Please report.");
		}
	}

	/**
	 * Checks if the player has all the Kodai wand pieces.
	 *
	 * @return
	 */
	public boolean hasAllKodai() {
		return playerHasItem(21043, 1) && playerHasItem(6914, 1);
	}

	/**
	 * Checks if the player has all the shards.
	 *
	 * @return
	 */
	public boolean hasAllShards() {
		return playerHasItem(11818, 1) && playerHasItem(11820, 1) && playerHasItem(11822, 1);
	}

	public boolean hasAllPieces() {
		return playerHasItem(19679, 1) && playerHasItem(19681, 1) && playerHasItem(19683, 1);
	}
	/**
	 * Makes the Kodai wand.
	 */
	public void makeKodai() {
		deleteItem(21043, 1);
		deleteItem(6914, 1);
		addItem(21006, 1);
		player.getDH().sendStatement("You combine the insignia and wand to make a Kodai wand.");
	}
	/**
	 * Makes the godsword blade.
	 */
	public void makeBlade() {
		deleteItem(11818, 1);
		deleteItem(11820, 1);
		deleteItem(11822, 1);
		addItem(11798, 1);
		player.getDH().sendStatement("You combine the shards to make a godsword blade.");
	}

	public void makeTotem() {
		deleteItem(19679, 1);
		deleteItem(19681, 1);
		deleteItem(19683, 1);
		addItem(19685, 1);
		player.getDH().sendStatement("You combine the pieces to make a dark totem.");
	}
	public int getTotalWorth() {
		int worth = 0;
		for (int inventorySlot = 0; inventorySlot < player.playerItems.length; inventorySlot++) {
			int inventoryId = player.playerItems[inventorySlot] - 1;
			int inventoryAmount = player.playerItemsN[inventorySlot];
			int price = ShopAssistant.getItemShopValue(inventoryId);
			if (inventoryId == 996)
				price = 1;
			if (inventoryId > 0 && inventoryAmount > 0) {
				worth += (price * inventoryAmount);
			}
		}
		for (int equipmentSlot = 0; equipmentSlot < player.playerEquipment.length; equipmentSlot++) {
			int equipmentId = player.playerEquipment[equipmentSlot];
			int equipmentAmount = player.playerEquipmentN[equipmentSlot];
			int price = ShopAssistant.getItemShopValue(equipmentId);
			if (equipmentId > 0 && equipmentAmount > 0) {
				worth += (price * equipmentAmount);
			}
		}
		return worth;
	}

	/**
	 * Checks if the item is a godsword hilt.
	 *
	 * @param i
	 * @return
	 */
	public boolean isHilt(int i) {
		return i >= 11810 && i <= 11816 && i % 2 == 0;
	}

	public void openUpBank() {
		player.getPA().sendChangeSprite(58014, player.placeHolders ? (byte) 1 : (byte) 0);
		if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen() || player.viewingRunePouch) {
			player.sendMessage("You should stop what you are doing before opening the bank.");
			return;
		}
		player.getPA().resetVariables();
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			player.isBanking = false;
			return;
		}

		if (player.getPosition().inWild() && !(player.getRights().isOrInherits(Right.ADMINISTRATOR))) {
			player.sendMessage("You can't bank in the wilderness!");
			return;
		}
		if (!player.getMode().isBankingPermitted() && player.inUimBank == false) {
			player.sendMessage("Your game mode prohibits use of the banking system.");
			return;
		}
		player.inUimBank = false;
		if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
			Server.getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			} else {
				player.sendMessage("You cannot bank whilst dueling.");
				return;
			}
		}
		player.inBank = true;
		player.getPA().sendFrame126("Search", 58063);
		if (player.getOutStream() != null) {
			player.isBanking = true;
			player.getItems().sendInventoryInterface(5064);
			player.getItems().updateBankContainer();
			player.getItems().resetTempItems();
			player.getOutStream().createFrame(248);
			player.getOutStream().writeWordA(Bank.INTERFACE_ID);
			player.getOutStream().writeUnsignedWord(5063);
			player.flushOutStream();
		}
	}


	public static boolean hasItemInInventory(Player player, int itemId) {
		itemId++;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId) {
				return true;
			}
		}
		return false;
	}


	public static boolean hasItemAmountInInventory(Player player, int itemId, int amount) {
		itemId++;
		int found = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] == itemId) {
				if (player.playerItemsN[i] >= amount) {
					return true;
				} else {
					found++;
				}
			}
		}
		if (found >= amount) {
			return true;
		}
		return false;
	}


	public static int getItemAmount(Player player, int itemId) {
		int itemCount = 0;
		for (int i = 0; i < player.playerItems.length; i++) {
			if ((player.playerItems[i] - 1) == itemId) {
				itemCount += player.playerItemsN[i];
			}
		}
		return itemCount;
	}

	public static boolean hasEquipment(Player player) {
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] > 0 && player.playerEquipmentN[i] > 0) {
				return true;
			}
		}
		return false;
	}



	public void resetItems(int WriteFrame) {
		// synchronized (c) {
		if (player.getOutStream() != null && player != null) {
			player.getOutStream().createFrameVarSizeWord(53);
			player.getOutStream().writeWord(WriteFrame);
			player.getOutStream().writeWord(player.playerItems.length);
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItemsN[i] > 254) {
					player.getOutStream().writeByte(255);
					player.getOutStream().writeDWord_v2(player.playerItemsN[i]);
				} else {
					player.getOutStream().writeByte(player.playerItemsN[i]);
				}
				player.getOutStream().writeWordBigEndianA(player.playerItems[i]);
			}
			player.getOutStream().endFrameVarSizeWord();
			player.flushOutStream();
		}
		// }
	}

	public boolean updateInventory = false;

	public void updateInventory() {
		updateInventory = false;
		resetItems(3214);
	}




}