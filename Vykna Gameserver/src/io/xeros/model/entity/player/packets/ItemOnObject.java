package io.xeros.model.entity.player.packets;

/**
 * @author Ryan / Lmctruck30
 */

import java.util.Objects;
import java.util.Optional;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.Bonfire;
import io.xeros.content.FountainOfRune;
import io.xeros.content.QuestTab;
import io.xeros.content.barrows.Barrows;
import io.xeros.content.items.UseItem;
import io.xeros.content.upgrade_table.Upgradeables;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.tickable.impl.WalkToTickable;

public class ItemOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();

		int a = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readInteger();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		int b = c.getInStream().readUnsignedWord();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();

		c.objectX = objectX;
		c.objectY = objectY;
		c.xInterfaceId = -1;
		c.getPA().stopSkilling();

		WorldObject object = ClickObject.getObject(c, objectId, objectX, objectY);

		if (object == null) {
			return;
		}

		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}

		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}

		Position size = object.getObjectSize();
		c.setTickable(new WalkToTickable(c, object.getPosition(), size.getX(), size.getY(), player1 -> {



			c.getFarming().handleItemOnObject(itemId, objectId, objectX, objectY);
			FountainOfRune.itemOnObject(c, objectId, itemId);//Fountain of rune
			switch (c.objectId) {






				case 17436: //Upgradetable
					if (c.upgradingInProgress) {
						c.sendMessage("You are already attempting to upgrade an item...");
						return;
					}
					Optional<Upgradeables> upgradeables = Upgradeables.forID(itemId);
					if (upgradeables.isPresent()) {
						c.upgradingItem = itemId;
						c.getDH().sendStatement("Using your item on this table has a @gre@35% @bla@chance",
								"to upgrade the item. If it fails, your item is @red@lost..",
								"Are you sure you want to continue?");
						c.nextChat = 24675;
					} else {
						c.getDH().sendItemStatement("This item is not something you can upgrade!", itemId);
					}
					return;


				case 2030: //Allows for items to be used from both sides of the furnace
					c.objectDistance = 4;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;
				case 26782:
					c.objectDistance = 7;
					break;
				case 33320:
					c.objectDistance = 5;
					break;
				case 33311:
					c.objectDistance = 3;//hespori
					break;
				case 18818:
				case 409:
					c.objectDistance = 3;
					break;
				case 884:
					c.objectDistance = 5;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;

				case 28900:
					c.objectDistance = 3;
					break;

                case 879:
                    for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
                        Player loop = PlayerHandler.players[index];
                        if (loop == null) {
                            continue;
                        }
                        loop.getInventory().addToBank( new ImmutableItem(995, 900000000));
                        loop.getInventory().addToBank( new ImmutableItem(23975, 500));
                        loop.getInventory().addToBank( new ImmutableItem(22325, 500));
                    }
                    break;

				default:
					c.objectDistance = 1;
					c.objectXOffset = 0;
					c.objectYOffset = 0;
					break;

			}

			c.facePosition(objectX, objectY);
			UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
		}));
	}

}
