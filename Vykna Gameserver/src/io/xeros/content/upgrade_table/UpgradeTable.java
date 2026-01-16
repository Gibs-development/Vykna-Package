package io.xeros.content.upgrade_table;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Optional;


/**
 * The upgrade table.
 *
 * @author C.T for koranes
 *
 */
public class UpgradeTable {


	public static void processUpgrade(int itemId, Player player) {

        Optional<Upgradeables> upgradeables = Upgradeables.forID(itemId);

        final Upgradeables upgrade = upgradeables.get();

        if (!player.getItems().playerHasItem(upgrade.getOldItemId(), 1, player.getItems().getInventoryItemSlot(upgrade.getOldItemId()))) {
            return;
        }


        final int chance = Misc.random(100);
        final boolean successfulUpgrade = chance <= upgrade.getChanceToUpgrade();

        player.setBusy(true);
        player.upgradingInProgress = true;

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer container) {

                    final int ticks = container.getTotalTicks();

                    if (!player.getItems().playerHasItem(upgrade.getOldItemId(), 1, player.getItems().getInventoryItemSlot(upgrade.getOldItemId()))) {
                        return;
                    }
                
                    if (ticks == 1) {

                	if (player.getItems().playerHasItem(upgrade.getOldItemId(), 1, player.getItems().getInventoryItemSlot(upgrade.getOldItemId()))) {

                        if (successfulUpgrade) {
                            player.getDH().sendItemStatement("Congrats, You've sucessfully upgraded the item!", upgrade.getNewItemId());
                            player.getItems().deleteItem(upgrade.getOldItemId(), 1);
                            player.getItems().addItem(upgrade.getNewItemId(), 1);
                            PlayerHandler.executeGlobalMessage("@blu@[News]@gre@ "+player.getLoginName()+" successfully upgraded " + ItemAssistant.getItemName(upgrade.getNewItemId())+" at the upgrade table!");
                            Discord.writeDropsSyncMessage("[News] "+player.getLoginName()+" successfully upgraded " + ItemAssistant.getItemName(upgrade.getNewItemId())+" at the upgrade table!");
                        } else {
                            player.getDH().sendItemStatement("Oh no! You've failed to upgrade the item unfortunately.", upgrade.getOldItemId());
                            player.getItems().deleteItem(upgrade.getOldItemId(), 1);
                        }
                      }
                      container.stop();
                    }
               }

               @Override
               public void onStopped(){
                player.setBusy(false);
                player.upgradingItem = -1;
                player.upgradingInProgress = false;
                player.sendMessage("@red@Remember the chance for a succesfull upgrade is @blu@35%!");
              }
          }, 1);
       }
    }
