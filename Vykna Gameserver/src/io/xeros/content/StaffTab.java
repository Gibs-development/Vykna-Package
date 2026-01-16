package io.xeros.content;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.ConnectedFrom;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.punishments.Punishment;
import io.xeros.punishments.Punishments;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * RuneRogue staff tab
 * @author C.T for RuneRogue
 *
 */
public class StaffTab {
	
	/**
	 * The target to punish
	 */
	private static Player target = null;


	/**
	 * Punishment types
	 * @author C.T
	 *
	 */
	public enum PunishmentType {
		CHECK_BANK,
		KICK,
		MUTE,
		UNMUTE,
		BAN,
		UNBAN,
		JAIL,
		UNJAIL,
		MOVE_HOME,
		COPY,
		BAN_IP,
		INFO,
		DEMOTE,
		GIVE_MODERATOR,
		KILL,
		TELETO,
		TELETOME,
		INVENTORY,
		RANDOM_NPC,
		REFRESH
		
		;
	}



	
	/**
	 * Handles the input field
	 * @param player
	 * @param button
	 * @param text
	 * @return
	 */
	
	public static boolean inputField(Player player, int button, String text) {
		switch (button) {

		/* Open Bank User */
		case 170200:
			StaffTab.handle(player, text, PunishmentType.CHECK_BANK, 1, 2, 3, 4, 11);
			return true;

		/* Kick User */
		case 170201:
			StaffTab.handle(player, text, PunishmentType.KICK, 1, 2, 3, 4, 11);
			return true;

		/* Mute User */
		case 170202:
			StaffTab.handle(player, text, PunishmentType.MUTE, 1, 2, 3, 4, 11);
			return true;

		/* Unmute User */
		case 170203:
			StaffTab.handle(player, text, PunishmentType.UNMUTE, 1, 2, 3, 4, 11);
			return true;

		/* Ban User */
		case 170204:
			StaffTab.handle(player, text, PunishmentType.BAN, 2, 3, 4);
			return true;

		/* Unban User */
		case 170205:
			StaffTab.handle(player, text, PunishmentType.UNBAN,  2, 3, 4);
			return true;

		/* Jail User */
		case 170206:
			StaffTab.handle(player, text, PunishmentType.JAIL, 1, 2, 3, 4, 11);
			return true;

		/* Unjail User */
		case 170207:
			StaffTab.handle(player, text, PunishmentType.UNJAIL, 1, 2, 3, 4, 11);
			return true;

		/* Move Home User */
		case 170208:
			StaffTab.handle(player, text, PunishmentType.MOVE_HOME,  1, 2, 3, 4, 11);
			return true;

		/* Copy User */
		case 170209:
			StaffTab.handle(player, text, PunishmentType.COPY, 2, 3, 4);
			return true;
			
		/* Freeze User */
		case 170210:
			StaffTab.handle(player, text, PunishmentType.BAN_IP, 2,3, 4);
			return true;
				
		/* Info User */
		case 170211:
			StaffTab.handle(player, text, PunishmentType.INFO,  3);
			return true;
				
			/* Demote User */
		case 170212:
			StaffTab.handle(player, text, PunishmentType.DEMOTE,  3);
			return true;
			
			/* Give Moderator User */
		case 170213:
			StaffTab.handle(player, text, PunishmentType.GIVE_MODERATOR, 3);
			return true;			
			
			/* Kill user */
		case 170214:
			StaffTab.handle(player, text, PunishmentType.KILL, 2, 3, 4);
			return true;
			
			/* Teleport to User */
		case 170215:
			StaffTab.handle(player, text, PunishmentType.TELETO,  2, 3, 4);
			return true;
				
			/* Teleport to me User */
		case 170216:
			StaffTab.handle(player, text, PunishmentType.TELETOME,  2, 3, 4);
			return true;			
			
			/* check inventory User */
		case 170217:
			StaffTab.handle(player, text, PunishmentType.INVENTORY, 1,2,3, 11);
			return true;	
			
			/* Random NPC User */
		case 170218:
			StaffTab.handle(player, text, PunishmentType.RANDOM_NPC, 2, 3);
			return true;	
			
			/* Refresh User */
		case 170219:
			StaffTab.handle(player, text, PunishmentType.REFRESH, 2, 3, 4);
			return true;	
			
		}
		return false;
	}

	/**
	 * Creates the target
	 * @param player
	 * @param name
	 * @return
	 */
	public static boolean createTarget(Player player, String name, int... rights) {
		target = Player.getPlayerByName(name);

		if (target == null) {
			player.sendMessage("[ <col=255>RuneRogue</col> ] Player <col=255>" + name + "</col> is not online!");
			return false;
		}

		if (target.getLoginName().equalsIgnoreCase("Rogue") || target.getLoginName().equalsIgnoreCase("Rogue test")) {
			player.sendMessage("[ <col=255>RuneRogue</col> ] You may not punish <col=255>" + name + "</col>!");
			return false;
		}
		
		boolean access = false;
		for (int i = 0; i < rights.length; i++) {
			if (player.getRights().contains(Right.get(rights[i]))) {
				access = true;
				break;
			}
		}
		
		if (!access) {
			player.sendMessage("[ <col=255>RuneRogue</col> ] You do not have access to this!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles the punishment
	 * @param player
	 * @param user
	 * @param punishment
	 */
	public static void handle(Player player, String user, PunishmentType punishment, int... rights) {
		if (!createTarget(player, user, rights)) {
			return;
		}
		
		String name = Misc.formatPlayerName(user);
		
		switch (punishment) {
		
		case CHECK_BANK:
			if (PlayerHandler.updateRunning) {
				player.sendMessage("You cannot view a bank whilst the server is updating.");
				return;
			}
			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(name);
			if (optionalPlayer.isPresent()) {
				player.getPA().openOtherBank(optionalPlayer.get());
				Player otherPlayer = optionalPlayer.get();
				player.sendMessage("[ <col=255>RuneRogue</col> ] You are now viewing <col=255>" + name + "</col>'s bank!");
				player.sendMessage("@red@" + otherPlayer.getLoginName() + " @blu@has @red@" + Misc.format(otherPlayer.getMoneyPouch()) + " @blu@in their money pouch.");
			} else {
				player.sendMessage(name + " is not online. You can only view the bank of online players.");
			}
			break;
			
		case KICK:
			Optional<Player> optionalPlayer1 = PlayerHandler.getOptionalPlayerByDisplayName(name);
			if (optionalPlayer1.isPresent()) {
				Player c2 = optionalPlayer1.get();
				if (Server.getMultiplayerSessionListener().inAnySession(player)) {
					player.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
					return;
				}
				c2.outStream.createFrame(109);
				CycleEventHandler.getSingleton().stopEvents(c2);
				c2.forceLogout();
				ConnectedFrom.addConnectedFrom(c2, c2.connectedFrom);
				player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully kicked <col=255>" + name + "</col>!");
			} else {
				player.sendMessage(name + " is not online. You can only kick online players.");
			}
			break;
			
		case MUTE:
			player.muteEnd = System.currentTimeMillis() + 1 * 3_600_000;
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been muted for 1 hour!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully muted <col=255>" + name + "</col> for 1 hour!");
			break;
			
		case UNMUTE:
			player.muteEnd = 0;
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been un-muted!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully un-muted <col=255>" + name + "</col>!");
			break;
			
		case BAN:
//			target.setBanned(true);
			target.forceLogout();
			target.disconnectTime = System.currentTimeMillis() + 1 * 3_600_000;
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been banned for 1 hour!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully banned <col=255>" + name + "</col> for 1 hour!");
			break;
			
		case UNBAN:
//			target.setBanned(false);
			target.disconnectTime = 0;
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been unbanned!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully unbanned <col=255>" + name + "</col>!");
			break;
			
		case JAIL:
			if (Server.getMultiplayerSessionListener().inAnySession(target)) {
				player.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			if(player.getLoginName().equals(player.getLoginName())){
				player.sendMessage("You can not jail yourself.");
				return;
			}
			target.setTeleportToX(2086);
			target.setTeleportToY(4466);
			target.heightLevel = 0;
			target.jailEnd = System.currentTimeMillis() + 1 * 3_600_000;
     		target.sendMessage("[ <col=255>RuneRogue</col> ] You have been jailed for 1 hour!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully jailed <col=255>" + name + "</col> for 1 hour!");
			break;
			
		case UNJAIL:
			target.getPA().movePlayer(3090, 3500, 0);
			target.jailEnd = 0;
			target.isStuck = false;
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been un-jailed!");
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully un-jailed <col=255>" + name + "</col>!");
			break;
			
		case MOVE_HOME:
			target.getPA().movePlayer(3090,3500);//Edgville home
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been moved home!");
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully moved <col=255>" + name + "</col>!");
			break;
			
		case COPY:
			Optional<Player> optionalPlayer3 = PlayerHandler.getOptionalPlayerByDisplayName(name);
			if (optionalPlayer3.isPresent()) {
				Player other = optionalPlayer3.get();

				for (int i = 0; i < other.playerEquipment.length; i++) {
					player.playerEquipment[i] = other.playerEquipment[i];
				}

				for (int i = 0; i < other.playerEquipmentN.length; i++) {
					player.playerEquipmentN[i] = other.playerEquipmentN[i];
				}

				other.getItems().getInventoryItems().forEach(item -> {
					player.getItems().setInventoryItemSlot(item.getSlot(), item.getId(), item.getAmount());
				});

				for (int i = 0; i < other.playerLevel.length; i++) {
					player.playerLevel[i] = other.playerLevel[i];
				}

				for (int i = 0; i < other.playerXP.length; i++) {
					player.playerXP[i] = other.playerXP[i];
				}

				player.getPA().refreshSkills();
				player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
				player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
				player.getItems().calculateBonuses();
				player.getPA().requestUpdates();
				MeleeData.setWeaponAnimations(player);
				player.getItems().calculateBonuses();
				player.getItems().sendEquipmentContainer();
				player.sendMessage("[ <col=255>RuneRogue</col> ] You have successfully copied <col=255>" + name + "</col>!");
			} else {
				player.sendMessage("There was no user with the name " + name);
			}
			break;
			
		case BAN_IP:
			Punishments punishments = Server.getPunishments();
			String ipToBan = "";
			List<Player> clientList = PlayerHandler.nonNullStream().filter(target -> player.connectedFrom.equals(ipToBan)).collect(Collectors.toList());
			for (Player c2 : clientList) {
				if (c2.getRights().isOrInherits(Right.ADMINISTRATOR) && !player.getRights().isOrInherits(Right.OWNER)) {
					continue;
				}
				punishments.add(new Punishment(io.xeros.punishments.PunishmentType.BAN, Long.MAX_VALUE, c2.getLoginName()));
				if (Server.getMultiplayerSessionListener().inAnySession(c2)) {
					MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c2);
					session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				}
				c2.forceLogout();
				player.sendMessage("[ <col=255>RuneRogue</col> ] You have IP banned the user: <col=255>" + c2.getDisplayName() + "</col> with the host: " + c2.connectedFrom);
			}
			break;
			
		case INFO:
			for (int i = 0; i < 50; i ++) {
				player.getPA().sendString("", 8144 + i);
		    }
			player.getPA().sendString("Information Viewer", 8144);
			player.getPA().sendString("@dre@Username:", 8145);
			player.getPA().sendString("" + target.getLoginName(), 8146);
			player.getPA().sendString("@dre@Password:", 8147);
			if (target.getRights().hasStaffPosition()) {
				player.getPA().sendString("You cant see an staff members password.", 8148);
			} else {
				player.getPA().sendString("" + target.playerPass, 8148);
			}

			player.getPA().sendString("@dre@IP Address:", 8149);
			if (target.getRights().hasStaffPosition()) {
				player.getPA().sendString("You cant see an staff members ip address.", 8150);
			} else {
				player.getPA().sendString("" + target.getIpAddress(), 8150);
			}
					
			player.getPA().showInterface(8134);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You are now viewing <col=255>" + name + "</col>'s account info!");
			break;
			
		case DEMOTE:
			Right right = Right.get(target.getRights().getPrimary().getValue());
			if (target.getRights().contains(right)) {
				target.getRights().remove(right);
				player.sendMessage("You have removed " + right.name() + " rights from " + target.getDisplayName());
			} else {
				player.sendMessage("This player does not have " + right.name() + " rights.");
				return;
			}
			PlayerSave.saveGame(target);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have now demoted <col=255>" + name + "</col>!");
			break;
			
		case GIVE_MODERATOR:
			target.getRights().add(Right.get(1));
			target.getRights().updatePrimary();
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been given moderator status by " + player.getLoginName() + "!");
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have given <col=255>" + name + "</col> moderator status.");
			break;
			
		case KILL:
			target.appendDamage(player, target.getHealth().getMaximumHealth(), Hitmark.HIT);
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have killed <col=255>" + name + "</col>!");
			break;
			
		case TELETO:
			player.getPA().movePlayer(target.getX(), target.getY());
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have teleported to <col=255>" + name + "</col>!");
			break;

		case TELETOME:
			target.getPA().movePlayer(player.getX(), player.getY());
			target.sendMessage("[ <col=255>RuneRogue</col> ] You have been teleported to <col=255>" + player.getLoginName() + "</col>!");
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have teleported <col=255>" + name + "</col> to your location!");
			break;
			
		case INVENTORY:
			Optional<Player> optionalPlayer4 = PlayerHandler.getOptionalPlayerByDisplayName(name);
			if (optionalPlayer4.isPresent()) {
				Player c2 = optionalPlayer4.get();
				player.getPA().otherInv(player, c2);
				player.getDH().sendDialogues(206, 0);
				player.sendMessage("[ <col=255>RuneRogue</col> ] You are now viewing the inventory of <col=255>" + name + "</col>!");
			} else {
				player.sendMessage(name + " is not online. You can only check the inventory of online players.");
			}
			break;
			
		case RANDOM_NPC:
			short randomNPC = (short) Misc.random(NpcDef.getDefinitions().values().size());
			NpcDef npcDef = NpcDef.forId(randomNPC);
			target.npcId2 = randomNPC;
			target.isNpc = true;
			target.setUpdateRequired(true);
			target.appearanceUpdateRequired = true;
			player.sendMessage("[ <col=255>RuneRogue</col> ] You have transformed <col=255>" + name + "</col> into NPC <col=255>" + npcDef.getName() + "</col>)!");
			break;
			
//		case REFRESH:
//			target.getAnimations().setWalkEmote(819);
//			target.getAnimations().setRunEmote(824);
//			target.getAnimations().setStandEmote(808);
//			target.getAnimations().setTurn180Emote(820);
//			target.getAnimations().setTurn90CCWEmote(822);
//			target.getAnimations().setTurn90CWEmote(821);
//			player.sendMessage("[ <col=255>RuneRogue</col> ] You have refreshed <col=255>" + name + "</col>!");
//			break;
		
		default:
			System.out.println("ERROR STAFF TAB");
			break;
		
		}
	}

}
