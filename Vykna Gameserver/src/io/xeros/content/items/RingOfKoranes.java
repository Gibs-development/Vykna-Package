package io.xeros.content.items;


import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.items.bank.Bank;

/**
 * Handles the ring of koranes effects.
 *
 * @author C.T for koranes
 */
public class RingOfKoranes {

    /**
     * The ring's item ID.
     */
    public static final int ID = 21752;

    /**
     * The location that the loot will be stored.
     */
    public enum CollectionLocation {

        /**
         * Regular location, will drop under the NPC.
         */
        GROUND,

        /**
         * Loot will be added to inventory.
         */
        INVENTORY,

        /**
         * Loot will be added to bank.
         */
        BANK

    }

    /**
     * Returns if the player is wearing the ring.
     *
     * @param player
     * @return if the ring is worn
     */
    public static boolean isWearing(Player player) {
    	return player.getItems().isWearingItem(ID);
    }

    /**
     * Collects the loot for the player.
     *
     * @param player
     * @param itemId
     * @param itemAmount 
     * @return
     */
    public static boolean collectLoot(Player player, int itemId, int itemAmount ) {

        if (!isWearing(player)) {
            return false;
        }

        if (player.getRingOfKoranesCollection() == CollectionLocation.GROUND) {
        	  return false;
        }

        switch (player.getRingOfKoranesCollection()) {
        
            case BANK:
            	
            	String itemName = ItemAssistant.getItemName(itemId);
				//player.sendMessage("<img=10> <col=996633>" + itemName + " have been added to your bank.");
            	//Bank.addItemToBank(player, itemId, itemAmount, false);
                player.getItems().addItemToBankOrDrop(itemId, itemAmount);
                return true;

            case INVENTORY:
            	
            	String itemName1 = ItemAssistant.getItemName(itemId);
				player.sendMessage("<img=10> <col=996633>" + itemName1 + " have been added to your inventory.");
            	//ItemAssistant.addItemToInventoryOrDrop(player, itemId, itemAmount);
                player.getItems().addItem(itemId, itemAmount, true);
                return true;
		    
            case GROUND:
			break;
			
		default:
			break;
		
        }

        return false;
    }

    /**
     * Sends the ring's config.
     *
     * @param player
     */
    public static void sendConfig(Player player) {

        if (player.getRingOfKoranesCollection() == CollectionLocation.GROUND) {
            player.sendMessage("<img=10> <col=996633>Your ring is not collecting items.");
            return;
        }

        player.sendMessage("<img=10> <col=996633>Once equipped your ring will collect items and send them to " + player.getRingOfKoranesCollection().name().toLowerCase() + ".");
    }

    /**
     * Toggles the rings configuration.
     *
     * @param player
     */
    public static void toggleConfig(Player player) {

        int index = player.getRingOfKoranesCollection().ordinal() + 1;

        if (index >= CollectionLocation.values().length) {
            index = 0;
        }

        player.setRingOfKoranesColletion(CollectionLocation.values()[index]);

        sendConfig(player);
    }

	

}
