package io.xeros.content;

import java.util.*;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

/**
 *
 * 
 * @author C.T for koranes
 *
 */
	public class WildyCrate extends CycleEvent {

	/**
	 * The item id of the Pursuit Crate required to trigger the event
	 */
	public static final int WILDY_CRATE = 21307; //Crate

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static Map<Rarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(Rarity.COMMON, 
			Arrays.asList(
					new GameItem(565, 200),
					new GameItem(560, 200), 
					new GameItem(11937, 50 + Misc.random(25)),
					new GameItem(2996, Misc.random(15) + 10),
					new GameItem(9244, 75),
					new GameItem(4087), 
					new GameItem(1128, 2), 
					new GameItem(1080, 3),
					new GameItem(11730),
					new GameItem(12696, 10),

					new GameItem(565, 200),
					new GameItem(560, 200),
					new GameItem(11937, 50 + Misc.random(25)),
					new GameItem(2996, Misc.random(15) + 10),
					new GameItem(9244, 75),
					new GameItem(4087),
					new GameItem(1128, 2),
					new GameItem(1080, 3),
					new GameItem(11730),
					new GameItem(12696, 10),

					new GameItem(990, 2))
					
			);
			
		items.put(Rarity.UNCOMMON,
				Arrays.asList(

						new GameItem(4587, 1),// d scim
						new GameItem(11840), //d boots
						new GameItem(12749),                                       //common
						new GameItem(20595),
						new GameItem(20517),
						new GameItem(20520),
						new GameItem(20272),//cabbage shield
						new GameItem(11230, Misc.random(200) + 50),

						new GameItem(4587, 1),// d scim
						new GameItem(11840), //d boots
						new GameItem(12749),                                        //common
						new GameItem(20595),
						new GameItem(20517),
						new GameItem(20520),
						new GameItem(20272),//cabbage shield
						new GameItem(11230, Misc.random(200) + 50),


						new GameItem(4587, 1),// d scim
						new GameItem(11840), //d boots
						new GameItem(12749),                                         //common
						new GameItem(20595),
						new GameItem(20517),
						new GameItem(20520),
						new GameItem(20272),//cabbage shield
						new GameItem(11230, Misc.random(200) + 50),



						new GameItem(4587, 1),// d scim
						new GameItem(11840), //d boots
						new GameItem(12749),
						new GameItem(4675), //anceint staff
						new GameItem(20595), 
						new GameItem(20517), 
						new GameItem(20520), 
						new GameItem(20272),//cabbage shield
						new GameItem(11230, Misc.random(200) + 50),
						new GameItem(20113),
						new GameItem(20116),
						new GameItem(20119),
						new GameItem(20122),
						new GameItem(20125),
						new GameItem(4675), //anceint staff
						new GameItem(995, 1000000),
						new GameItem(6816, 20),
						new GameItem(19841),//master casket
						new GameItem(19685),//dark totem
						new GameItem(21298, 1),//obsid
						new GameItem(21301, 1),//obsid
						new GameItem(21304, 1))//obsid
						
		);
			
			items.put(Rarity.RARE,
					Arrays.asList(

							new GameItem(20119),
							new GameItem(20122),
							new GameItem(20125),
							new GameItem(4675), //anceint staff
							new GameItem(995, 1000000),            //  common
							new GameItem(6816, 20),
							new GameItem(19841),//master casket
							new GameItem(19685),//dark totem


							new GameItem(20119),
							new GameItem(20122),
							new GameItem(20125),
							new GameItem(4675), //anceint staff
							new GameItem(995, 1000000),            //  common
							new GameItem(6816, 20),
							new GameItem(19841),//master casket
							new GameItem(19685),//dark totem


							new GameItem(20119),
							new GameItem(20122),
							new GameItem(20125),
							new GameItem(4675), //anceint staff
							new GameItem(995, 1000000),            //  common
							new GameItem(6816, 20),
							new GameItem(19841),//master casket
							new GameItem(19685),//dark totem


							new GameItem(20119),
							new GameItem(20122),
							new GameItem(20125),
							new GameItem(4675), //anceint staff
							new GameItem(995, 1000000),            //  common
							new GameItem(6816, 20),
							new GameItem(19841),//master casket
							new GameItem(19685),//dark totem



							new GameItem(11818),//godswrod shard
							new GameItem(12746),
							new GameItem(12748),
							new GameItem(11820), //godsword shard
							new GameItem(11822), //godsword shard
							new GameItem(22638, 1),
						    new GameItem(22641, 1),
						    new GameItem(22644, 1),
						    new GameItem(22650, 1),
						    new GameItem(22653, 1),
						    new GameItem(22656, 1),
							new GameItem(24419, 1),
							new GameItem(24417, 1),
							new GameItem(24420, 1),
							new GameItem(24421, 1)
						));
							
							
	}

	/**
	 * The player object that will be triggering this event
	 */
	private Player player;

	/**
	 * Constructs a new Wildy Crate to handle item receiving for this player and this player alone
	 * 
	 * @param player the player
	 */
	public WildyCrate(Player player) {
		this.player = player;
	}

	/**
	 * Opens a PvM Casket if possible, and ultimately triggers and event, if possible.
	 *
	 */
	public void open() {
		if (System.currentTimeMillis() - player.lastMysteryBox < 1500) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need atleast two free slots to open a Rogue's Crate.");
			return;
		}
		if (!player.getItems().playerHasItem(WILDY_CRATE)) {
			player.sendMessage("You need a Rogue Crate to do this.");
			return;
		}
		player.getItems().deleteItem(WILDY_CRATE, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		CycleEventHandler.getSingleton().stopEvents(this);
		CycleEventHandler.getSingleton().addEvent(this, this, 2);
	}

	/**
	 * Executes the event for receiving the Wildy crate
	 */

	@Override
	public void execute(CycleEventContainer container) {
		if (player.isDisconnected() || Objects.isNull(player)) {
			container.stop();
			return;
		}
		int coins = 50000 + Misc.random(50000);
		int coinsDouble = 100000 + Misc.random(100000);
		int random = Misc.random(100);
		List<GameItem> itemList = random < 60 ? items.get(Rarity.COMMON) : random >= 60 && random <= 97 ? items.get(Rarity.UNCOMMON) : items.get(Rarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);
		int rights = player.getRights().getPrimary().getValue() - 1;
		if (random > 97) {
			PlayerHandler.executeGlobalMessage(" "+player.getLoginName() + "just received: <col=006600>"+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + "</col> from a Rogue Crate!");
			Discord.writeDropsSyncMessage(""+ player.getLoginName() + " just received: "+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + " from a Rogue Crate!");
		}
		player.lastMysteryBox = System.currentTimeMillis();
		player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
		player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");

		container.stop();
	}

	/**
	 * Represents the rarity of a certain list of items
	 */
	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}