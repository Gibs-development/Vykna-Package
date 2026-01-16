package io.xeros.content;

import io.xeros.Configuration;
import io.xeros.achievements.InterfaceHandler;
import io.xeros.content.item.lootable.impl.CrystalChest;
import io.xeros.content.item.lootable.impl.HunllefChest;
import io.xeros.content.item.lootable.impl.RaidsChestCommon;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.HashMap;


/**
 * Handles the credit system
 * @author C.T For RuneRogue
 *
 */
public enum CreditHandler {

	ONLINE_STORE(173217, 0, new Handle() {//205051
		@Override
		public void handle(Player player) {
			player.getPA().sendFrame126(Configuration.STORE_LINK, 12000);
		}

	}),
	
	VOTE_SHOP(173243, 0, new Handle() {//205051
		@Override
		public void handle(Player player) {
			player.getShops().openShop(77);
			player.sendMessage("You have @blu@" + player.votePoints + "</col> Voting points.");
		}
		
	}),
	
	GENERAL_STORE(173244, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(122);
		}
		
	}),
	
	SKILLING_SUPPLIES(173245, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(21);
		}
		
	}),
	
	FOOD_POTIONS(173246, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(113);
		}
		
	}),
	
	WEAPONS(173247, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(111);
		}
		
	}),
	
	ARMOUR(173248, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(8);
		}
		
	}),
	
	COMBAT_STORES(173249, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(8);//
		}
		
	}),
	
	MAGIC(173250, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(6);
		}
		
	}),
	
	RANGE(173251, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(4);
		}
		
	}),
	
	OUTFITS(173252, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(114);
		}
		
	}),
	
	DAILY_TASKS(173253, 0, new Handle() {
		@Override
		public void handle(Player player) {
			 player.getShops().openShop(205);

		}
		
	}),
	
	PKP_STORE(173254, 0, new Handle() {// PKP STORE
		@Override
		public void handle(Player player) {
			player.getShops().openShop(80);
		}
		
	}),

	JOSSSK(173255, 0, new Handle() {
		@Override
		public void handle(Player player) {
			//player.getShops().openShop(13);
			player.sendStatement("Soon to be opened.");
		}
		
	}),
	
	SLAYER_STORE(174000, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(10);
		}
		
	}),
	
	BOSS_POINT(174001, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(121);
		}
		
	}),
	
	DONATOR(174002, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(9);
		}
		
	}),
	
	FANCY_STORE(174003, 0, new Handle() {
		@Override
		public void handle(Player player) {
			//player.getShops().openShop(79);
			player.sendStatement("Soon to be opened.");
		}

	}),
	
	AFK_STORE(174004, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(199);
		}
		
	}),
	
	PRESTIGE(174005, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(120);
		}
		
	}),
	
	
	ITEM_EXCHANGE(174006, 0, new Handle() {//ITEM EXCHANGE STORE
		@Override
		public void handle(Player player) {
			player.getShops().openShop(171);
		}
		
	}),
	
	IRONMAN(174007, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getShops().openShop(41);
		}
		
	}),



	KRATOS_CHEST(174009, 0, new Handle() {
		@Override
		public void handle(Player player) {
			KratosChest.searchChest(player);
		}

	}),

	NIGHTMARE_CHEST(174010, 0, new Handle() {
		@Override
		public void handle(Player player) {
			KoranianChest.searchChest(player);
		}

	}),

	SOLAK_CHEST(174011, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (player.getItems().playerHasItem(22428)) {
				EventChestSolak.searchChest(player);
				return;
			} else
				player.getDH().sendStatement("@red@You need a solak key to open this chest.");
		}

	}),

	GLOD_CHEST(174012, 0, new Handle() {
		@Override
		public void handle(Player player) {
			GlodChest.searchChest(player);
		}

	}),

	SEREN_CHEST(174013, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (player.getItems().playerHasItem(6792)) {
				new SerenChest().roll(player);
				return;
			} else
				player.getDH().sendStatement("@red@You need a seren key to open this chest.");
		}

	}),

	LOYALTY_CHEST(174014, 0, new Handle() {
		@Override
		public void handle(Player player) {
			player.getDH().sendOption2("Claim loyalty rewards", "Cancel");
			player.dialogueAction = 26273;
		}

	}),

	SLAYER_CHEST(174015, 0, new Handle() {
		@Override
		public void handle(Player player) {
			SlayerChest.searchChest(player);
		}

	}),

	CRYSTAL_CHEST(174016, 0, new Handle() {
		@Override
		public void handle(Player player) {
			new CrystalChest().roll(player);
		}

	}),

	HUNLLEF_CHEST(174017, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (!(System.currentTimeMillis() - player.chestDelay > 2000)) {
				player.getDH().sendStatement("Please wait before doing this again.");
				return;
			}

			if (player.getItems().freeSlots() < 3) {
				player.getDH().sendStatement("@red@You need at least 3 free slots for safety");
				return;
			}
			if (player.getItems().playerHasItem(23776, 1)) {
				new HunllefChest().roll(player);
				player.chestDelay = System.currentTimeMillis();
				return;
			}
			player.getDH().sendStatement("@red@You need Hunllef's key to unlock this chest.");
		}

	}),

	RAID_1_CHEST(174018, 0, new Handle() {
		@Override
		public void handle(Player player) {
			if (player.getItems().freeSlots() < 3) {
				player.getDH().sendStatement("@red@You need at least 3 free slots for safety");
				return;
			}
			if (player.getItems().playerHasItem(Raids.COMMON_KEY, 1)) {
				new RaidsChestCommon().roll(player);
				return;
			}
			if (player.getItems().playerHasItem(Raids.RARE_KEY, 1)) {
				new RaidsChestRare().roll(player);
				return;
			}
			player.getDH().sendStatement("@red@You need either a rare or common key.");
		}

	}),

	;

	private int button;
	private int creditCost;
	private Handle handle;

	CreditHandler(int button, int creditCost, Handle handle) {
		this.button = button;
		this.creditCost = creditCost;
		this.handle = handle;
	}


	public int getButton() {
		return button;
	}

	public int getCost() {
		return creditCost;
	}


	public Handle getHandle() {
		return handle;
	}

	public static HashMap<Integer, CreditHandler> credits = new HashMap<Integer, CreditHandler>();

	static {
		for (final CreditHandler credits : CreditHandler.values()) {
			CreditHandler.credits.put(credits.button, credits);
		}
	}

	
	/**
	 * Checks if player is allowed to access feature
	 * @param player
	 * @param amount
	 * @return
	 */
	public static boolean allowed(Player player, CreditPurchase credit, int amount) {
		if (player.isCreditUnlocked(credit)) {
			player.sendStatement("@red@You have already unlocked this.");
			return false;
		}
		if (player.donatorPoints < amount) {
			player.sendStatement("@red@You do not have enough Donation points to do this!");
			player.sendMessage("Please visit @red@http://runerogue.co.uk/store/ </col>to purchase more scrolls!");
			return false;
		}
		if (player.wildLevel > 0 ) {
			player.sendStatement("You can not do this in the wilderness!");
			return false;
		}
		if (player.underAttackByPlayer > 0 || player.underAttackByNpc > 0) {
			player.sendStatement("You can not do this while in combat!");
			return false;
		}
		return true;
	}

	/**
	 * Handles what happens when player has spent credits
	 * @param player
	 * @param amount
	 */
	public static void spent(Player player, int amount) {
		player.sendMessage("@blu@You have spent " + amount + " Donation Points; Remaining: " + player.donatorPoints + ".");
		player.getPA().sendString("</col>Donation Points: @gre@" + Misc.format(player.donatorPoints), 44504);
		InterfaceHandler.writeText(new CreditTab(player));
	}

	/**
	 * Handles clicking buttons
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean handleClicking(Player player, int buttonId) {
		CreditHandler credits = CreditHandler.credits.get(buttonId);

		if (credits == null) {
			return false;
		}

		if (Boundary.isInAny(player, Boundary.FIGHT_CAVE)) {
			player.sendStatement("@red@You cant use this in the fight caves.");
			return false;
		}

		if (Boundary.isInAny(player, Boundary.INFERNO)) {
			player.sendStatement("@red@You cant use this in the inferno.");
			return false;
		}

		if (Boundary.isInAny(player, Boundary.LMS_AREAS)) {
			player.sendStatement("@red@You cant use this in the lms.");
			return false;
		}

		if (Boundary.isInAny(player, Boundary.TOURNY_LOBBY)) {
			player.sendStatement("@red@You cant use this in the lobby.");
			return false;
		}

		if (Boundary.isInAny(player, Boundary.TOURNY_COMBAT_AREA)) {
			player.sendStatement("@red@You cant use this in the arena.");
			return false;
		}

		if (player.wildLevel > 0 ) {
			player.sendStatement("@red@You can not do this in the wilderness!");
			return false;
		}
		if (player.underAttackByPlayer > 0 || player.underAttackByNpc > 0) {
			player.sendStatement("@red@You can not do this while in combat!");
			return false;
		}

		credits.getHandle().handle(player);
		return false;
	}
	
}
