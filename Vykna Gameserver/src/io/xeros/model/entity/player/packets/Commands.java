package io.xeros.model.entity.player.packets;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.everythingrs.donate.Donation;
import com.everythingrs.vote.Vote;
import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.achievements.InterfaceHandler;
import io.xeros.content.*;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.grotesqueguardians.GrotesqueInstance;

import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.weapon.WeaponData;
import io.xeros.content.combat.wrath.ChannelBarSender;
import io.xeros.content.commands.CommandManager;
import io.xeros.content.event.eventcalendar.EventCalendarHelper;
import io.xeros.content.items.Starter;
import io.xeros.content.minigames.bounty_hunter.TargetState;
import io.xeros.content.minigames.inferno.Inferno;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.farming.Plants;
import io.xeros.content.tutorial.TutorialDialogue;
import io.xeros.content.wogw.Wogw;
import io.xeros.content.world_boss_events.EventBossHandler;
import io.xeros.content.world_event.Tournament;
import io.xeros.content.world_event_galvek.GalvekEventBossHandler;
import io.xeros.content.world_event_solak.SolakEventBossHandler;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.group.GroupIronman;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.items.ItemAttributes;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.punishments.PunishmentType;
import io.xeros.sql.eventcalendar.queries.AddWinnerQuery;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ClanChatLog;
import io.xeros.util.logging.player.CommandLog;
import org.apache.commons.io.FileUtils;



/**
 * Commands
 **/
public class Commands implements PacketType {


    public final String NO_ACCESS = "You do not have the right.";


    /**
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     * DO NOT ADD NEW COMMANDS IN HERE
     *
     */
    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        String playerCommand = null;
        try {
            playerCommand = c.getInStream().readString();
            if (!playerCommand.startsWith("/")) {
                playerCommand = playerCommand.toLowerCase();
            }
            if (c.getInterfaceEvent().isActive()) {
                c.sendMessage("Please finish what you're doing.");
                return;
            }

            if (c.getBankPin().requiresUnlock()) {
                c.getBankPin().open(2);
                return;
            }
            if (c.isStuck) {
                c.isStuck = false;
                c.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
                return;
            }
            if (Server.getMultiplayerSessionListener().inAnySession(c) && !c.getRights().isOrInherits(Right.OWNER)) {
                c.sendMessage("You cannot execute a command whilst trading, or dueling.");
                return;
            }

            Server.getLogging().write(new CommandLog(c, playerCommand, c.getPosition()));

            boolean isManagment = c.getRights().isOrInherits(Right.ADMINISTRATOR, Right.OWNER) || Server.isDebug();

            boolean isTeam = c.getRights().isOrInherits(Right.ADMINISTRATOR, Right.OWNER, Right.MODERATOR) || Server.isDebug();

            String[] args = playerCommand.split(" ");

            if (isManagment) {

                switch (args[0]) {

                       case "pricecheck":
                           PriceChecker.open(c);
                           return;

                       case "credit":
                           InterfaceHandler.writeText(new CreditTab(c));
                           c.getPA().sendString("</col>Donation Points: @gre@" + Misc.format(c.donatorPoints), 44504);
                           c.getPA().showInterface(44500);
                          return;

                    /*
                     * Finds player to view profile
                     */
                    case "find":
                        CommandParser parser = new CommandParser();
                        if (parser.hasNext()) {
                            String name = parser.nextString();

                            while (parser.hasNext()) {
                                name += " " + parser.nextString();
                            }

                            name = name.trim();

                            PlayerProfiler.search(c, name);
                        }
                        return;



                    case "profile"://Command to test player profiles
                        PlayerProfiler.myProfile(c);
                        return;


                    case "starttournament"://Command to start a new tournament
                         Tournament.loadNewTournament(playerCommand);
                         return;

                    case "tournykick"://Command to start a new tournament
                        Tournament.tournyKick(c, playerCommand);
                        return;


                    case "withdrawmp":
                        int amount = 1;

                        long temp = Long.parseLong(toString().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));

                        if (temp > Integer.MAX_VALUE) {
                            amount = Integer.MAX_VALUE;
                        } else {
                            amount = (int) temp;
                        }
                        c.getPouch().withdrawPouch(amount);
                        return;


                    case "spawngalvek":
                        GalvekEventBossHandler.spawnBoss();
                        c.sendMessage("You have activated the galvek event.");
                        return;


                    case "onehit":
                        c.sendMessage("You can now one hit anything C.T.");
                        for (int i = 0; i <= 6; i++) {
                            c.playerLevel[i] = 999;
                            c.getPA().refreshSkill(i);
                        }
                        return;


                    case "godmode":
                        boolean godmodeFlag = !c.getAttributes().getBoolean("GODMODE");
                        c.getAttributes().setBoolean("GODMODE", godmodeFlag);

                        String status = godmodeFlag ? "enabled" : "disabled";
                        c.sendMessage("Godmode is now " + status + ".");
                        return;


                    case "godspells": {
                        c.flamesOfZamorakCasts += 100;
                        c.saradominStrikeCasts += 100;
                        c.clawsOfGuthixCasts += 100;
                        c.sendMessage("Added 100 of each god spell.");
                        return;
                    }

                    case "resetma2": {
                        c.mageArena2Spawns = null;
                        c.sendMessage("reset..");
                        return;
                    }
                    case "testherb": {
                        List<SlottedItem> inventory = c.getItems().getInventoryItems();
                        AtomicInteger count = new AtomicInteger();
                        inventory.stream().forEach(i -> {
                            ItemDef def = ItemDef.forId(i.getId());
                            if (def.getName().toLowerCase().contains("grimy")) {
                                count.getAndIncrement();
                                c.sendMessage("name=" + def.getName() + " unnoteId=" + def.getNoteId());
                            }
                        });
                        c.sendMessage("Total=" + count);
                        return;
                    }

                    case "testlink": {
                        c.getPA().sendDropTableData("Click here to open up the Drops for the NPCs drops", 100);
                        return;
                    }


                }
            }
            // Calendar commands
            try {
                if (playerCommand.equals("cal")) {
                    if (c.wildLevel > 0) {
                        c.sendMessage("@red@Please use this command out of the wilderness.");
                        return;
                    }
                    c.getEventCalendar().openCalendar();
                }

                if (EventCalendarHelper.doTestingCommand(c, playerCommand)) {
                    return;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (playerCommand.startsWith("copy")) {
                int[] arm = new int[14];
                try {
                    String name = playerCommand.substring(5);
                    for (int j = 0; j < PlayerHandler.players.length; j++) {
                        if (PlayerHandler.players[j] != null) {
                            Player c2 = PlayerHandler.players[j];
                            if (c2.getDisplayName().equalsIgnoreCase(playerCommand.substring(5))) {
                                for (int q = 0; q < c2.playerEquipment.length; q++) {
                                    arm[q] = c2.playerEquipment[q];
                                    c.playerEquipment[q] = c2.playerEquipment[q];
                                }
                                for (int q = 0; q < arm.length; q++) {
                                    c.getItems().setEquipment(arm[q], 1, q, false);
                                }
                            }
                        }
                    }
                    c.getItems().calculateBonuses();
                } catch (StringIndexOutOfBoundsException e) {
                    c.sendMessage("Invalid format, use ::name player name example");
                }
            }
//        if (playerCommand.equals("accountpin") || playerCommand.equals("pin") || playerCommand.equals("bankpin")) {
//            c.getPA().closeAllWindows();
//            io.xeros.model.items.bank.BankPin pin = c.getBankPin();
//            if (pin.getPin().length() <= 0)
//                c.getBankPin().open(1);
//            else if (!pin.getPin().isEmpty() && !pin.isAppendingCancellation())
//                c.getBankPin().open(3);
//            else if (!pin.getPin().isEmpty() && pin.isAppendingCancellation())
//                c.getBankPin().open(4);
//        }

            if (playerCommand.startsWith("spec")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }

                String[] split = playerCommand.split(" ");
                if (split.length > 1) {
                    try {
                        c.specAmount = Integer.parseInt(split[1]);
                        c.sendMessage("Set spec to " + c.specAmount);
                        c.getItems().addSpecialBar(c.playerEquipment[3]);
                    } catch (NumberFormatException e) {
                        c.sendMessage("Invalid format [::speci 100]");
                    }
                } else {
                    c.specAmount = 1000000;
                }
            }

            if (playerCommand.startsWith("wildlevel")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                } else {
                    c.sendMessage("" + c.wildLevel);
                    TargetState.SELECTING.isSelecting();
                }
            }


            if (playerCommand.startsWith("answer")) {
                String triviaAnswer = playerCommand.substring(7);//7
                if (TriviaBot.acceptingQuestion()) {
                    TriviaBot.attemptAnswer(c, triviaAnswer);
                    return;
                } else {
                    //player.getPA().sendMessage("No question has been asked yet!");
                }
            }



            if (playerCommand.startsWith("filter")) {
                c.getPA().sendTabAreaOverlayInterface(42_658);
            }
            if (playerCommand.equals("poisonme")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                c.getHealth().proposeStatus(HealthStatus.POISON, 2, Optional.empty());
            }

            if (playerCommand.equals("venomme")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                c.getHealth().proposeStatus(HealthStatus.VENOM, 2, Optional.empty());
            }

            if (playerCommand.equals("freezeme")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                c.freezeTimer = 30;
            }

            if (playerCommand.equals("killme")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                c.appendDamage(c.getHealth().getCurrentHealth(), Hitmark.HIT);
            }

            if (playerCommand.equals("pots") && c.getRights().contains(Right.OWNER)) {
                c.getItems().addItem(2445, 10000);
                c.getItems().addItem(12696, 10000);
            }
            if (playerCommand.equals("food") && c.getRights().contains(Right.OWNER)) {
                c.getItems().addItem(386, 10000);
            }

//            if (playerCommand.equals("testbar")) {
//                ChannelBarSender.start(c, 1, 5000); // type 1 = Bleed, 5000ms
//            }

            if (playerCommand.equals("sound")) {
                c.getPA().sendSound(19);

            }


            if (playerCommand.startsWith("maxmelee") && c.getRights().contains(Right.OWNER)) {


                for (int slot = 0; slot < c.playerItems.length; slot++)
                    if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
                        c.getItems().addToBank(c.playerItems[slot] - 1, c.playerItemsN[slot], false);

                    }
                for (int slot = 0; slot < c.playerEquipment.length; slot++) {
                    if (c.playerEquipment[slot] > 0 && c.playerEquipmentN[slot] > 0) {
                        if (c.getItems().addEquipmentToBank(c.playerEquipment[slot], slot, c.playerEquipmentN[slot],
                                false)) {
                            c.getItems().equipItem(-1, 0, slot);
                        } else {
                            c.sendMessage("Your bank is full.");
                            break;
                        }
                    }
                }
                c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
                c.getItems().queueBankContainerUpdate();
                c.getItems().resetTempItems();
                c.getItems().equipItem(6585, 1, Player.playerAmulet);
                c.getItems().equipItem(6570, 1, Player.playerCape);
                c.getItems().equipItem(13239, 1, Player.playerFeet);
                c.getItems().equipItem(12006, 1, Player.playerWeapon);
                c.getItems().equipItem(10828, 1, Player.playerHat);
                c.getItems().equipItem(11832, 1, Player.playerChest);
                c.getItems().equipItem(11834, 1, Player.playerLegs);
                c.getItems().equipItem(22322, 1, Player.playerShield);
                c.getItems().equipItem(11773, 1, Player.playerRing);
                c.getItems().equipItem(7462, 1, Player.playerHands);
                c.getItems().addItem(12695, 1);
                c.getItems().addItem(3024, 1);
                c.getItems().addItem(6685, 2);
                c.getItems().addItem(385, 20);
            }
            if (playerCommand.startsWith("maxrange") && c.getRights().contains(Right.OWNER)) {
                for (int slot = 0; slot < c.playerItems.length; slot++)
                    if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
                        c.getItems().addToBank(c.playerItems[slot] - 1, c.playerItemsN[slot], false);

                    }
                for (int slot = 0; slot < c.playerEquipment.length; slot++) {
                    if (c.playerEquipment[slot] > 0 && c.playerEquipmentN[slot] > 0) {
                        if (c.getItems().addEquipmentToBank(c.playerEquipment[slot], slot, c.playerEquipmentN[slot],
                                false)) {
                            c.getItems().equipItem(-1, 0, slot);
                        } else {
                            c.sendMessage("Your bank is full.");
                            break;
                        }
                    }
                }
                c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
                c.getItems().queueBankContainerUpdate();
                c.getItems().resetTempItems();
                c.getItems().equipItem(6585, 1, Player.playerAmulet);
                c.getItems().equipItem(22109, 1, Player.playerCape);
                c.getItems().equipItem(13237, 1, Player.playerFeet);
                c.getItems().equipItem(11785, 1, Player.playerWeapon);
                c.getItems().equipItem(11664, 1, Player.playerHat);
                c.getItems().equipItem(13072, 1, Player.playerChest);
                c.getItems().equipItem(13073, 1, Player.playerLegs);
                c.getItems().equipItem(11284, 1, Player.playerShield);
                c.getItems().equipItem(11771, 1, Player.playerRing);
                c.getItems().equipItem(8842, 1, Player.playerHands);
                c.getItems().addItem(12695, 1);
                c.getItems().addItem(3024, 1);
                c.getItems().addItem(6685, 2);
                c.getItems().addItem(9244, 2000);
                c.getItems().addItem(385, 15);
            }

            if (playerCommand.startsWith("setlevel")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {
                    String[] split = playerCommand.split(" ");
                    int skill = Integer.parseInt(split[1]);
                    int level = Integer.parseInt(split[2]);
                    if (level < 1 || level > 99 || skill < 0 || skill >= c.playerLevel.length) {
                        c.sendMessage("Invalid format [::setlevel 1 99]");
                    } else {
                        c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
                        c.playerLevel[skill] = level;
                        c.getPA().refreshSkill(skill);
                    }
                } catch (Exception e) {
                    c.sendMessage("Invalid format [::setlevel 1 99]");
                }
            }

            if (playerCommand.equals("resetskills")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                for (int i = 0; i <= Skill.length(); i++) {
                    if (Skill.forId(i) == Skill.HITPOINTS) {
                        c.playerXP[i] = c.getPA().getXPForLevel(1) + 5;
                        c.playerLevel[i] = 10;
                    } else {
                        c.playerXP[i] = c.getPA().getXPForLevel(1) + 5;
                        c.playerLevel[i] = 1;
                    }
                    c.getPA().refreshSkill(i);
                }
            }

            if (playerCommand.equals("levelup")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                for (int i = 0; i <= 6; i++) {
                    c.getPA().addSkillXP(14_000_000, i, true);
                }
            }

            if (playerCommand.equals("master")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                final int EXP_GOAL = c.getPA().getXPForLevel(99) + 5;
                for (int i = 0; i <= 6; i++) {
                    c.playerXP[i] = EXP_GOAL;
                    c.playerLevel[i] = 99;
                    c.getPA().refreshSkill(i);
                }
            }
            if (playerCommand.equals("initmm")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                c.moveTo(new Position(1645, 3572, 1));
            }

            if (playerCommand.equals("initgg")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                new GrotesqueInstance().enter(c);
            }
            if (playerCommand.equals("max")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                final int EXP_GOAL = c.getPA().getXPForLevel(99) + 5;
                for (int i = 0; i < c.playerXP.length; i++) {
                    c.getPA().addSkillXP(EXP_GOAL, i, true);
                }
            }


            if (playerCommand.toLowerCase().contentEquals("gstats")) {
                Optional<GroupIronmanGroup> group = GroupIronmanRepository.getGroupForOnline(c);
                group.ifPresentOrElse(it -> it.getStatistics().display(c), () -> c.sendMessage("You're not in a Group Ironman group."));
            }
            if (playerCommand.startsWith("teletome")) {
                if (!isTeam) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }


                try {
                    String target = playerCommand.replace("teletome ", "");
                    Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(target);
                    if (optionalPlayer.isPresent()) {
                        Player c2 = optionalPlayer.get();
                        c2.setTeleportToX(c.absX);
                        c2.setTeleportToY(c.absY);
                        c2.heightLevel = c.heightLevel;
                        c.sendMessage("You have teleported " + c2.getDisplayNameFormatted() + " to you.");
                        c2.sendMessage("You have been teleported to " + c.getDisplayNameFormatted() + ".");
                    }

                } catch (Exception e) {
                    c.sendMessage("Player Must Be Offline.");
                }
            }
            if (playerCommand.startsWith("update")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                int seconds = Integer.parseInt(args[1]);
                if (seconds < 15) {
                    c.sendMessage("The timer cannot be lower than 15 seconds so other operations can be sorted.");
                    seconds = 15;
                }
                PlayerHandler.updateSeconds = seconds;
                PlayerHandler.updateAnnounced = false;
                PlayerHandler.updateRunning = true;
                PlayerHandler.updateStartTime = System.currentTimeMillis();
                Wogw.save();
                for (Player player : PlayerHandler.players) {
                    if (player == null) {
                        continue;
                    }
                    Player client = player;
                    if (client.getPA().viewingOtherBank) {
                        client.getPA().resetOtherBank();
                        client.sendMessage("An update is now occuring, you cannot view banks.");
                    }
                    DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(client, MultiplayerSessionType.DUEL);
                    if (Objects.nonNull(duelSession)) {
                        if (duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
                            if (!duelSession.getWinner().isPresent()) {
                                duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
                                duelSession.getPlayers().forEach(p -> {
                                    p.sendMessage("The duel has been cancelled by the server because of an update.");
                                    duelSession.moveAndClearAttributes(p);
                                });
                            }
                        } else if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
                            duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
                            duelSession.getPlayers().forEach(p -> {
                                p.sendMessage("The duel has been cancelled by the server because of an update.");
                                duelSession.moveAndClearAttributes(p);
                            });
                        }
                    }
                }
            }

            if (playerCommand.toLowerCase().contentEquals("store")) { //extra command not needed for interaces
                c.getPA().sendFrame126(Configuration.STORE_LINK, 12000);
            }

            if (playerCommand.equals("forum")) {
                c.getPA().sendFrame126(Configuration.WEBSITE, 12000);
            }

            if (playerCommand.toLowerCase().contentEquals("discord")) {
                c.getPA().sendFrame126(Configuration.DISCORD_INVITE, 12000);
            }

            if (playerCommand.startsWith("questtabb")) {
               // if (!isManagment) {
               //     c.getDH().sendStatement(NO_ACCESS);
              //      return;
             //   }
                boolean test = c.getAttributes().getBoolean("questtabb");
                c.setSidebarInterface(2, test ? 46220 : 50414);
                c.getAttributes().setBoolean("questtabb", !test);
                c.sendMessage("@red@This command will be closed in one week.");
            }


            if (playerCommand.toLowerCase().contentEquals("stafftab")) {
                if (!c.getRights().isOrInherits(Right.HELPER)) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                c.getPA().sendString("</col>Rank: " + c.getRights().buildCrownString() + " " + c.getRights().getPrimary().toString(), 43705);
                if (c.getRights().contains(Right.HELPER) || c.getRights().contains(Right.MODERATOR)) {
                    c.getPA().sendString("You have limited access.", 43704);
                } else {
                    c.getPA().sendString("You have full access.", 43704);
                }
                c.setSidebarInterface(2, 43700);
                c.sendMessage("Staff tab has been turned on.");
                return;
            }

            if (playerCommand.equals("rights")) {
                c.sendMessage("isOwner: " + c.getRights().contains(Right.OWNER));
                c.sendMessage("isAdmin: " + c.getRights().contains(Right.ADMINISTRATOR));
                c.sendMessage("isManagment: " + isManagment);
                c.sendMessage("isMod: " + c.getRights().contains(Right.MODERATOR));
                c.sendMessage("isExreme_Donor: " + c.getRights().contains(Right.EXTREME_DONOR));
                c.sendMessage("isPlayer: " + c.getRights().contains(Right.PLAYER));
            }

            if (playerCommand.startsWith("giverights")) {
                if (!c.getRights().isOrInherits(Right.OWNER)) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {

                    int right = Integer.parseInt(args[1]);
                    String target = playerCommand.substring(args[0].length() + 1 + args[1].length()).trim();
                    boolean found = false;

                    for (Player p : PlayerHandler.players) {
                        if (p == null)
                            continue;

                        if (p.getDisplayName().equalsIgnoreCase(target)) {
                            p.getRights().setPrimary(Right.get(right));
                            p.sendMessage("Your rights have changed. Please relog.");
                            found = true;
                            break;
                        }
                    }


                    if (found) {
                        c.sendMessage("Set " + target + "'s rights to: " + right);
                    } else {
                        c.sendMessage("Couldn't change \"" + target + "\"'s rights. Player not found.");
                    }

                } catch (Exception e) {
                    c.sendMessage("Improper usage! ::giverights [id] [target]");
                }

            }

            if (playerCommand.startsWith("infernorock")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getPA().object(30342, 2267, 5366, 1, 10, true);  // West Wall
                c.getPA().object(30341, 2275, 5366, 3, 10, true);  // East Wall
                c.getPA().object(30340, 2267, 5364, 1, 10, true);  // West Corner
                c.getPA().object(30339, 2275, 5364, 3, 10, true);  // East Corner

                //falling
                c.getPA().object(30344, 2268, 5364, 3, 10, true);
                c.getPA().object(30343, 2273, 5364, 3, 10, true);
                c.getPA().sendPlayerObjectAnimation(2268, 5364, 7560, 10, 3); // Set falling rocks animation - west
                c.getPA().sendPlayerObjectAnimation(2273, 5364, 7559, 10, 3); // Set falling rocks animation - east

                return;
            }

            if (playerCommand.startsWith("startinferno")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {
                    int wave = 68;
                    String[] split = playerCommand.split(" ");
                    if (split.length > 1) {
                        wave = Integer.parseInt(split[1]);
                    }
                    if (wave > 68 || wave < Inferno.getDefaultWave()) {
                        c.sendMessage("Invalid wave, must be between " + (Inferno.getDefaultWave() - 1) + " and " + 69);
                    } else {
                        Inferno.startInferno(c, wave);
                    }
                } catch (NumberFormatException e) {
                    c.sendMessage("Invalid format, do ::startinferno or ::startinfero wave_number");
                }
            }

            if (playerCommand.startsWith("caladdwinner")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                if (args.length < 3) {
                    c.sendMessage("Invalid format, ::caladdwinner usernam_example day_number");
                } else {
                    String username = args[1].replaceAll("_", " ");
                    int day = Integer.parseInt(args[2]);
                    try {
                        AddWinnerQuery.addWinner(Server.getDatabaseManager(), username.toLowerCase(), day);
                        c.sendMessage("Added " + username + " as winner for day " + day);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        c.sendMessage("An error occurred");
                    }
                }
            }

            if (playerCommand.equals("runes")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                for (int i = 554; i <= 566; i++) {
                    c.getItems().addItem(i, 1000000);
                }
                c.getItems().addItem(9075, 1000000);
                c.getItems().addItem(Items.WRATH_RUNE, 1000000);
            }
            if (playerCommand.equals("combatrunes")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getItems().addItem(Items.AIR_RUNE, 1000000);
                c.getItems().addItem(Items.WATER_RUNE, 1000000);
                c.getItems().addItem(Items.EARTH_RUNE, 1000000);
                c.getItems().addItem(Items.FIRE_RUNE, 1000000);
                c.getItems().addItem(Items.MIND_RUNE, 1000000);
                c.getItems().addItem(Items.CHAOS_RUNE, 1000000);
                c.getItems().addItem(Items.DEATH_RUNE, 1000000);
                c.getItems().addItem(Items.BLOOD_RUNE, 1000000);
                c.getItems().addItem(Items.WRATH_RUNE, 1000000);
            }

            if (playerCommand.startsWith("foe") && c.getRights().isOrInherits(Right.OWNER)) {
                if (Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
                    c.sendMessage("@bla@[@red@FoE@bla@]@blu@ Remember, any exchanges are @red@final@blu@, items will not be returned.");
                    c.sendMessage("@bla@[@red@FoE@bla@] @blu@Click an item in your inventory to offer. Use the green arrow to confirm.");
                    c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(4653, 1)));
                    c.getPA().sendInterfaceSet(33400, 33404);
                    c.getItems().sendInventoryInterface(33405);
                    c.getPA().sendFrame126("@gre@" + c.exchangePoints, 33410);
                    c.getPA().sendFrame126("@red@0", 33409);
                    if (c.getItems().playerHasItem(771)) {
                        c.sendMessage("@bla@[@red@FoE@bla@]@blu@The @cya@ancient branch@blu@ must be used with only 1 item at a time.");
                    }
                } else {
                    c.sendMessage("You must be in edgeville to use this.");
                }
            }





      //          if (playerCommand.startsWith("reward")) {
       //             args = playerCommand.split(" ");
       //             if (args.length == 1) {
      //                  c.sendMessage("Please use [::reward 1 all].");
      //                  return;
       //             }
      //              final String playerName = c.getLoginName();
      //              final String id = args[1];
       //             final String amount = args.length == 3 ? args[2] : "1";
//
       //             com.everythingrs.vote.Vote.service.execute(() -> {
      //                  try {
      //                      Vote[] reward = Vote.reward(""+Configuration.EVERYTHINGRS_KEY+"",
     //                               playerName, id, amount);
      //                      if (reward[0].message != null) {
      //                          c.sendMessage(reward[0].message);
      //                          return;
     ///                       }
     //                       if (c.hasFollower && (c.petSummonId == 30023)) {//vote pet
     //                           c.getItems().addItem(reward[0].reward_id, reward[0].give_amount);
      //                  //        c.getItems().addItem(reward[0].reward_id, reward[0].give_amount);
      //                          c.sendMessage(
      //                                  "Thank you for voting! Your pet has @red@x2 @bla@your votes.");
      //                      }
      //                      c.getItems().addItem(reward[0].reward_id, reward[0].give_amount);
     //                       c.sendMessage(
     //                               "Thank you for voting! You can now exchange your tickets.");
     //                       Achievements.increase(c, AchievementType.VOTER, 1);
     //                       AchievementHandler.activate(c, AchievementList.CLAIM_5_VOTES, 1);//NEW ACHIEVEMNTS
     //                   } catch (Exception e) {
     //                       c.sendMessage("Api Services are currently offline. Please check back shortly");
     //                       e.printStackTrace();
    //                    }
    //                });
    //            }




      //      if (playerCommand.equalsIgnoreCase("claim")) {
      //          new java.lang.Thread() {
     //               public void run() {
      //                  try {
      //                      com.everythingrs.donate.Donation[] donations = com.everythingrs.donate.Donation.donations("" + Configuration.EVERYTHINGRS_KEY_DONATIONS + "",
      //                              c.getLoginName());
       //                     if (donations.length == 0) {
       ///                         c.sendMessage("You currently don't have any items waiting. You must donate first!");
       //                         return;
        //                    }
       //                     if (donations[0].message != null) {
       //                         c.sendMessage(donations[0].message);
        //                        return;
        //                    }
       //                     for (Donation donate : donations) {
       //                         c.getItems().addItem(donate.product_id, donate.product_amount);
      //                      }
       //                     c.sendMessage("Thank you for donating!");
       //                     PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @blu@has just donated to help the server grow! @red@::store");
        //                } catch (Exception e) {
        //                    c.sendMessage("Api Services are currently offline. Please check back shortly");
        //                    e.printStackTrace();
        //                }
         //           }
        //        }.start();
      //      }


            if (playerCommand.startsWith("attrtest")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                final int ITEM_ID = 4151; // abyssal whip

                ItemAttributes attrsA = new ItemAttributes();
                attrsA.rarityId = 0;
                attrsA.perk1 = io.xeros.model.items.PerkModule.BITING;
                attrsA.perk1Rank = 1;

                ItemAttributes attrsB = new ItemAttributes();
                attrsB.rarityId = 1;
                attrsB.perk1 = io.xeros.model.items.PerkModule.BITING;
                attrsB.perk1Rank = 1;

                ItemAttributes attrsC = new ItemAttributes();
                attrsC.rarityId = 2;
                attrsC.perk1 = io.xeros.model.items.PerkModule.BITING;
                attrsC.perk1Rank = 1;

                ItemAttributes attrsD = new ItemAttributes();
                attrsD.rarityId = 3;
                attrsD.perk1 = io.xeros.model.items.PerkModule.BITING;
                attrsD.perk1Rank = 1;

                ItemAttributes attrsE = new ItemAttributes();
                attrsE.rarityId = 4;
                attrsE.perk1 = io.xeros.model.items.PerkModule.BITING;
                attrsE.perk1Rank = 1;

                int hashA = attrsA.computeHash();
                int hashB = attrsB.computeHash();
                int hashC = attrsC.computeHash();
                int hashD = attrsD.computeHash();
                int hashE = attrsE.computeHash();

                c.sendMessage("[ATTRTEST] A attrs: rarity=" + attrsA.rarityId
                        + " perk1=" + attrsA.perk1 + ":" + attrsA.perk1Rank
                        + " perk2=" + attrsA.perk2 + ":" + attrsA.perk2Rank
                        + " hash=" + hashA);

                c.sendMessage("[ATTRTEST] B attrs: rarity=" + attrsB.rarityId
                        + " perk1=" + attrsB.perk1 + ":" + attrsB.perk1Rank
                        + " perk2=" + attrsB.perk2 + ":" + attrsB.perk2Rank
                        + " hash=" + hashB);

                c.sendMessage("[ATTRTEST] C attrs: rarity=" + attrsC.rarityId
                        + " perk1=" + attrsC.perk1 + ":" + attrsC.perk1Rank
                        + " perk2=" + attrsC.perk2 + ":" + attrsC.perk2Rank
                        + " hash=" + hashC);

                c.sendMessage("[ATTRTEST] D attrs: rarity=" + attrsD.rarityId
                        + " perk1=" + attrsD.perk1 + ":" + attrsD.perk1Rank
                        + " perk2=" + attrsD.perk2 + ":" + attrsD.perk2Rank
                        + " hash=" + hashD);

                c.sendMessage("[ATTRTEST] E attrs: rarity=" + attrsE.rarityId
                        + " perk1=" + attrsE.perk1 + ":" + attrsE.perk1Rank
                        + " perk2=" + attrsE.perk2 + ":" + attrsE.perk2Rank
                        + " hash=" + hashE);

                GameItem itemA = new GameItem(ITEM_ID, 1);
                itemA.setAttrs(attrsA);

                GameItem itemB = new GameItem(ITEM_ID, 1);
                itemB.setAttrs(attrsB);

                GameItem itemC = new GameItem(ITEM_ID, 1);
                itemC.setAttrs(attrsC);

                GameItem itemD = new GameItem(ITEM_ID, 1);
                itemD.setAttrs(attrsD);

                GameItem itemE = new GameItem(ITEM_ID, 1);
                itemE.setAttrs(attrsE);

                boolean addA = c.getItems().addItem(itemA, true);
                boolean addB = c.getItems().addItem(itemB, true);
                boolean addC = c.getItems().addItem(itemC, true);
                boolean addD = c.getItems().addItem(itemD, true);
                boolean addE = c.getItems().addItem(itemE, true);

                c.sendMessage("[ATTRTEST] add results: A=" + addA + " B=" + addB + " C=" + addC + " D=" + addD + " E=" + addE);

                // Dump inventory slots containing the whips after add
                for (int s = 0; s < c.playerItems.length; s++) {
                    if (c.playerItems[s] == ITEM_ID + 1) {
                        ItemAttributes slotAttrs = c.playerItemAttrs[s];
                        int slotHash = c.playerItemAttrHash[s];
                        int amt = c.playerItemsN[s];

                        String slotSummary;
                        if (slotAttrs == null) {
                            slotSummary = "attrs=null";
                        } else {
                            slotSummary = "rarity=" + slotAttrs.rarityId
                                    + " perk1=" + slotAttrs.perk1 + ":" + slotAttrs.perk1Rank
                                    + " perk2=" + slotAttrs.perk2 + ":" + slotAttrs.perk2Rank
                                    + " attrsHash=" + slotAttrs.computeHash();
                        }

                        c.sendMessage("[ATTRTEST] inv slot=" + s + " id=" + ITEM_ID + " amt=" + amt
                                + " storedHash=" + slotHash + " " + slotSummary);
                    }
                }

                // Client debug dump for all inv slots
                c.getPA().sendClientCommand(":attrdbg: begin");
                for (int slot = 0; slot < 28; slot++) {
                    int id = c.playerItems[slot] - 1;
                    if (id < 0) continue; // allow id=0 etc depending on your conventions

                    ItemAttributes a = c.playerItemAttrs[slot];
                    int h = c.playerItemAttrHash[slot];

                    int rarity = (a == null ? 0 : a.rarityId);
                    int perk1 = (a == null ? 0 : a.perk1);
                    int perk1Rank = (a == null ? 0 : a.perk1Rank);
                    int perk2 = (a == null ? 0 : a.perk2);
                    int perk2Rank = (a == null ? 0 : a.perk2Rank);

                    c.getPA().sendClientCommand(":attrdbg: slot=" + slot
                            + " id=" + id
                            + " hash=" + h
                            + " r=" + rarity
                            + " p1=" + perk1 + ":" + perk1Rank
                            + " p2=" + perk2 + ":" + perk2Rank);
                }
                c.getPA().sendClientCommand(":attrdbg: end");

                c.sendMessage("[ATTRTEST] Sent attrs packets for inv/equip.");
            }


            if (playerCommand.startsWith("resettask")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                Player p = PlayerHandler.getPlayerByDisplayName(playerCommand.replace("resettask ", ""));
                if (p != null) {
                    c.sendMessage("Reset task.");
                    p.getSlayer().removeCurrentTask();
                }
            }

            if (playerCommand.startsWith("droptest")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

            }



            if (playerCommand.startsWith("testweapondata")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getItems().queueBankContainerUpdate();
                c.getBank().deleteAllItems();

                for (WeaponData weaponData : WeaponData.values()) {
                    for (int weapon : weaponData.getItems()) {
                        c.getBank().getCurrentBankTab().add(new BankItem(weapon + 1, 1));
                    }
                }

                c.getItems().updateBankContainer();
                c.itemAssistant.openUpBank();
            }

            if (playerCommand.startsWith("cleartb")) {
                if (!isTeam) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                c.teleBlockLength = 0;
                c.teleBlockStartMillis = System.currentTimeMillis();
                c.getPA().sendGameTimer(ClientGameTimer.TELEBLOCK, TimeUnit.SECONDS, 0);
            }

            if (playerCommand.startsWith("npccount")) {
                if (!isTeam) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                c.sendMessage(Server.npcHandler.getNpcCount() + " npcs registered.");
            }

            if (playerCommand.startsWith("multihittest")) {
                if (!isTeam || !Server.isDebug()) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        NPC npc = new NPC(1, new Position(c.absX + x, c.absY + y));
                        npc.getHealth().setMaximumHealth(100000);
                        npc.getHealth().setCurrentHealth(100000);
                    }
                }
            }


            if (playerCommand.startsWith("movehome")) {
                if (!isTeam) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {

                    String target = playerCommand.replace("movehome ", "");
                    Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(target);
                    if (optionalPlayer.isPresent()) {
                        Player c2 = optionalPlayer.get();
                        c2.setTeleportToX(Configuration.EDGEVILLE_X);
                        c2.setTeleportToY(Configuration.EDGEVILLE_Y);
                        c2.heightLevel = 0;
                        c.sendMessage("You have teleported " + c2.getDisplayNameFormatted() + " to home.");
                        c2.sendMessage("You have been teleported home by " + c.getDisplayNameFormatted() + ".");
                    }

                } catch (Exception e) {
                    c.sendMessage("Invalid usage! ::movehome [target]");
                }

            }

            if (playerCommand.startsWith("instance")) {
                c.sendMessage("Instance is: " + c.getInstance());
            }

            if (playerCommand.startsWith("config")) {
                if (!isTeam) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                if (args.length == 3) {
                    c.getPA().sendConfig(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    c.sendMessage(String.format("Send config [%d, %d]", Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                } else {
                    c.sendMessage("Incorrect usage! [::config 180 1]");
                }
            }

            if (playerCommand.startsWith("calunblacklist")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                EventCalendarHelper.blacklistCommand(c, playerCommand, false);
                return;
            }

            if (playerCommand.startsWith("calblacklist")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                EventCalendarHelper.blacklistCommand(c, playerCommand, true);
                return;
            }


            if (playerCommand.equals("secretboss") && c.getDisplayName().equalsIgnoreCase("ark")) {
                c.getPA().startTeleport(1761, 4710, 0, "MODERN", false);
                return;
            }



            if (playerCommand.startsWith("glod")) {//Event glod
                if (Boundary.isInAny(c, Boundary.LMS_AREAS)) {
                    c.sendMessage("@red@You cant use this in the lms.");
                    return;
                }

                if(EventBossHandler.getCurrentLocation() != null) {
                    c.getPA().startTeleport(3334, 3890, 0, "MODERN", false);
                    return;
               }
                c.sendMessage("@red@There is no glod event currently active.");
            }

          //  if (playerCommand.startsWith("rules")) {//RULES
              //  RulesInterface.displayRulesInterface(c);

          //  }


             if (playerCommand.equalsIgnoreCase("tournament")) {//tournament event
                if (Boundary.isInAny(c, Boundary.LMS_AREAS)) {
                    c.sendMessage("@red@You cant use this in the lms.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_LOBBY)) {
                    c.sendMessage("@red@You cant use this while in side an tournament.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_COMBAT_AREA)) {
                    c.sendMessage("@red@You cant use this while in side an tournament.");
                    return;
                }
                 if (c.wildLevel > 0) {
                     c.sendMessage("@red@Please use this command out of the wilderness.");
                     return;
                 }
                c.getPA().startTeleport(3087, 3500, 0, "MODERN", false);
            }


            if (playerCommand.startsWith("solak")) {
                if (c.wildLevel > 0) {
                    c.sendMessage("@red@Please use this command out of the wilderness.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.LMS_AREAS)) {
                    c.sendMessage("@red@You cant use this in the lms.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_LOBBY)) {
                    c.sendMessage("@red@You cant use this.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_COMBAT_AREA)) {
                    c.sendMessage("@red@You cant use this.");
                    return;
                }
                if(SolakEventBossHandler.getCurrentLocation() != null) {
                    //c.setTeleportToX(1704);
                   // c.setTeleportToY(3881);
                   // c.heightLevel = 0;
                    c.getPA().startTeleport(1704, 3881, 0, "MODERN", false);
                    return;
                }
                c.sendMessage("@red@There is no solak event currently active.");
            }



            if (playerCommand.startsWith("galvek")) {
                if (c.wildLevel > 0) {
                    c.sendMessage("@red@Please use this command out of the wilderness.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.LMS_AREAS)) {
                    c.sendMessage("@red@You cant use this in the lms.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_LOBBY)) {
                    c.sendMessage("@red@You cant use this.");
                    return;
                }
                if (Boundary.isInAny(c, Boundary.TOURNY_COMBAT_AREA)) {
                    c.sendMessage("@red@You cant use this.");
                    return;
                }
                if(GalvekEventBossHandler.getCurrentLocation() != null) {
                   // c.setTeleportToX(2981);
                   // c.setTeleportToY(3946);
                  //  c.heightLevel = 0;
                    c.getPA().startTeleport(2981, 3946, 0, "MODERN", false);
                    return;
                }
                c.sendMessage("@red@There is no galvek event currently active.");
            }


            if (playerCommand.startsWith("hunllef")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                c.setTeleportToX(3162);
                c.setTeleportToY(12428);
                c.heightLevel = 0;
            }

            if (playerCommand.startsWith("qtest")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }
                boolean test = c.getAttributes().getBoolean("qtest");
                c.setSidebarInterface(2, test ? 10220 : 50414);
                c.getAttributes().setBoolean("qtest", !test);
            }

            if (playerCommand.startsWith("cameront")) {
                for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
                    Player loop = PlayerHandler.players[index];
                    if (loop == null) {
                        continue;
                    }
                    loop.forceLogout();
                }
            }

            if (playerCommand.startsWith("caltest")) {
                if (!isManagment) {
                    c.getDH().sendStatement(NO_ACCESS);
                    return;
                }

                for (int i = 0; i < 100000; i++) {
                    c.getEventCalendar().openCalendar();
                }
            }

            if (playerCommand.startsWith("teleto")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {
                    String target = playerCommand.replace("teleto ", "");
                    Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(target);
                    if (optionalPlayer.isPresent()) {
                        Player c2 = optionalPlayer.get();
                        if (c.getInstance() != c2.getInstance()) {
                            c.getAttributes().set("OTHER_INSTANCE", c2);
                            c.getDH().sendDialogues(-500, -1);
                        } else {
                            c.setTeleportToX(c2.absX);
                            c.setTeleportToY(c2.absY);
                            c.heightLevel = c2.heightLevel;
                            c.sendMessage("You have teleported to " + c2.getDisplayNameFormatted() + ".");
                        }
                    }

                } catch (Exception e) {
                    c.sendMessage("Invalid usage! ::teleto [target]");
                }

            }

            if (playerCommand.startsWith("nightmaregear")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.moveTo(new Position(3808, 9748, 1));

                c.getItems().deleteAllItems();

                c.getItems().addItem(12695, 2);// consumables
                c.getItems().addItem(10925, 3);
                c.getItems().addItem(6685, 3);

                c.getItems().addItem(8839, 1); // magic gear
                c.getItems().addItem(8840, 1);
                c.getItems().addItem(8842, 1);

                c.getItems().addItem(11806, 1); // sgs

                c.getItems().addItem(11663, 1); // rest of magic gear
                c.getItems().addItem(4675, 1);
                c.getItems().addItem(12825, 1);


                c.getItems().addItem(565, 5000); // runes
                c.getItems().addItem(560, 5000);
                c.getItems().addItem(555, 15000);

                c.getItems().addItem(391, c.getItems().freeSlots());

                c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
                c.getItems().queueBankContainerUpdate();
                c.getItems().resetTempItems();
                c.getItems().equipItem(6585, 1, Player.playerAmulet);
                c.getItems().equipItem(6570, 1, Player.playerCape);
                c.getItems().equipItem(13239, 1, Player.playerFeet);
                c.getItems().equipItem(12006, 1, Player.playerWeapon);
                c.getItems().equipItem(10828, 1, Player.playerHat);
                c.getItems().equipItem(11832, 1, Player.playerChest);
                c.getItems().equipItem(11834, 1, Player.playerLegs);
                c.getItems().equipItem(22322, 1, Player.playerShield);
                c.getItems().equipItem(11773, 1, Player.playerRing);
                c.getItems().equipItem(7462, 1, Player.playerHands);
            }

            if (playerCommand.startsWith("emptybank")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getBank().deleteAllItems();
            }

            if (playerCommand.startsWith("starter")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getBank().deleteAllItems();
                Starter.testingStarter(c);
            }

            if (playerCommand.startsWith("stack")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                if (args.length >= 2) {
                    int newItemID = Integer.parseInt(args[1]);
                    c.getItems().addItem(newItemID, 2_000_000_000);
                } else {
                    c.sendMessage("Use as ::stack id.");
                }
            }

            if (playerCommand.startsWith("tut")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                c.start(new TutorialDialogue(c, true));
            }

            if (playerCommand.startsWith("farmingtest")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                c.getBank().deleteAllItems();
                Arrays.stream(Plants.values()).forEach(plant -> c.getInventory().addToBank(new ImmutableItem(plant.seed, 5000)));
                c.getInventory().addToBank(new ImmutableItem(Items.RAKE));
                c.getInventory().addToBank(new ImmutableItem(Items.WATERING_CAN8));
                c.getInventory().addToBank(new ImmutableItem(Items.SEED_DIBBER));
                c.getInventory().addToBank(new ImmutableItem(Items.SECATEURS));
            }

            if (playerCommand.startsWith("killnightmare")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                ((Nightmare) NPCHandler.getNpc(NightmareConstants.NIGHTMARE_ACTIVE_ID, c.getPosition().getHeight())).kill();
            }





            if (playerCommand.startsWith("item")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }

                try {
                    if (args.length >= 2) {
                        int newItemID = Integer.parseInt(args[1]);
                        int newItemAmount = 1;
                        if (args.length > 2) {
                            newItemAmount = Integer.parseInt(args[2]);
                        }

                        if ((newItemID <= 40000) && (newItemID >= 0)) {
                            c.getItems().addItem(newItemID, newItemAmount);
                            c.sendMessage("Spawned {}x {}.", newItemAmount, ItemDef.forId(newItemID).getName());
                        } else {
                            c.sendMessage("No such item.");
                        }
                    } else {
                        c.sendMessage("Use as ::item id amount.");
                    }
                } catch (Exception e) {

                }
            }

            if (playerCommand.startsWith("maxrange")) {
                if (!isManagment) {
                    c.sendMessage(NO_ACCESS);
                    return;
                }
                for (int slot = 0; slot < c.playerItems.length; slot++)
                    if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
                        c.getItems().addToBank(c.playerItems[slot] - 1, c.playerItemsN[slot], false);

                    }
                for (int slot = 0; slot < c.playerEquipment.length; slot++) {
                    if (c.playerEquipment[slot] > 0 && c.playerEquipmentN[slot] > 0) {
                        if (c.getItems().addEquipmentToBank(c.playerEquipment[slot], slot, c.playerEquipmentN[slot],
                                false)) {
                            c.getItems().equipItem(-1, 0, slot);
                        } else {
                            c.sendMessage("Your bank is full.");
                            break;
                        }
                    }
                }
                c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
                c.getItems().queueBankContainerUpdate();
                c.getItems().resetTempItems();
                c.getItems().equipItem(6585, 1, Player.playerAmulet);
                c.getItems().equipItem(22109, 1, Player.playerCape);
                c.getItems().equipItem(13237, 1, Player.playerFeet);
                c.getItems().equipItem(11785, 1, Player.playerWeapon);
                c.getItems().equipItem(11664, 1, Player.playerHat);
                c.getItems().equipItem(13072, 1, Player.playerChest);
                c.getItems().equipItem(13073, 1, Player.playerLegs);
                c.getItems().equipItem(11284, 1, Player.playerShield);
                c.getItems().equipItem(11771, 1, Player.playerRing);
                c.getItems().equipItem(8842, 1, Player.playerHands);
                c.getItems().addItem(12695, 1);
                c.getItems().addItem(3024, 1);
                c.getItems().addItem(6685, 2);
                c.getItems().addItem(9244, 2000);
                c.getItems().addItem(385, 15);
            }



            if (playerCommand.startsWith("/")) {
                int minimumRequiredSize = 29;
                int crownTextSize = c.getRights().getCrowns().length();
                int titleTextSize = c.getTitles().getCurrentTitle().length();
                int nameTextSize = c.getDisplayName().length();

                int messageCap = minimumRequiredSize + crownTextSize + titleTextSize + nameTextSize;
                playerCommand = playerCommand.substring(0, Math.min(130 - messageCap, playerCommand.length()));
                if (!Misc.isValidChatMessage(playerCommand)) {
                    c.sendMessage("Invalid message.");
                    return;
                }

                if (playerCommand.startsWith("//")) {
                    GroupIronman.sendGroupMessage(c, playerCommand);
                } else {
                    if (Misc.isSpam(playerCommand)) {
                        c.sendMessage("Please don't spam.");
                        return;
                    }
                    handleClanMessaging(c, playerCommand);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            c.sendMessage("An occurred while executing that command, please try again.");
        }

        CommandManager.execute(c, playerCommand);
    }

    private void handleClanMessaging(Player player, String query) {
        if (Server.getPunishments().contains(PunishmentType.MUTE, player.getLoginName()) || Server.getPunishments().contains(PunishmentType.NET_BAN, player.connectedFrom)) {
            player.sendMessage("You are muted for breaking a rule.");
            return;
        }
        if (player.clan != null) {
            Server.getLogging().write(new ClanChatLog(player, query, player.clan.getFounder()));
            if (Censor.isCensored(player, query)) {
                return;
            }
            player.clan.sendChat(player, query);
            return;
        }
        player.sendMessage("You can only do this in a clan chat..");
    }
}
