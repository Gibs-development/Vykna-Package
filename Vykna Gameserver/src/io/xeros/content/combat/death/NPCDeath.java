package io.xeros.content.combat.death;

import java.util.stream.IntStream;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
import io.xeros.content.Kratos;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.barrows.Barrows;
import io.xeros.content.bosses.*;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.events.monsterhunt.MonsterHunt;
import io.xeros.content.minigames.warriors_guild.AnimatedArmour;
import io.xeros.content.revenant_event.RevenantEventBossHandler;
import io.xeros.content.skills.Skill;
import io.xeros.content.sound.Sfx;
import io.xeros.content.world_event_solak.SolakEventBossHandler;
import io.xeros.model.Npcs;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.items.ItemAttributes;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.items.EquipmentSet;
import io.xeros.model.items.GameItem;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;


import static io.xeros.content.minigames.xeric.XericRewards.giveReward;
import static io.xeros.content.revenant_event.RevenantEventBossHandler.rewardPlayersSuperior;

public class NPCDeath {

    public static boolean superiorRevenantSpawned;

    public final static double SEREN_KEY_CHANCE = 20;//Seren key drop rate

    public static void dropItems(NPC npc) {
        Player c = PlayerHandler.players[npc.killedBy];
        if (c != null) {
            dropItemsFor(npc, c, npc.getNpcId());
        }
    }

    public static void dropItemsFor(NPC npc, Player c, int npcId) {
        if (c.getTargeted() != null && npc.equals(c.getTargeted())) {
            c.setTargeted(null);
            c.getPA().sendEntityTarget(0, npc);
        }
        c.getAchievements().kill(npc);
        PetHandler.rollOnNpcDeath(c, npc);

        if (npcId >= 1610 && npcId <= 1612) {
            //	c.setArenaPoints(c.getArenaPoints() + 1);
            c.getQuestTab().updateInformationTab();
            //	c.sendMessage("@red@You gain 1 point for killing the Mage! You now have " + c.getArenaPoints()
            //+ " Arena Points.");
        }

        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_DAGANNOTH_KINGS);
        }
        if (npcId == 411) {
            c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_KURASK);
        }

        if (npcId == 9021 || npcId == 9022 || npcId == 9023 || npcId == 9024) {
            c.hunllefDead = true;
            //PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ Hunllef has been defeated by @bla@" + c.playerName + "@blu@!");
            Hunllef.rewardPlayers(c);
        }
        if (npcId == 1047) {
            c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_CAVE_HORROR);
        }

        if (npcId == 1673) { //barrows npcs
            Achievements.increase(c, AchievementType.DH_KILLS, 1);
        }
        if (npcId == 5744 || npcId == 5762) {
            c.setShayPoints(c.getShayPoints() + 1);
            c.sendMessage("@red@You gain 1 point for killing the Penance! You now have " + c.getShayPoints()
                    + " Assault Points.");
        }


        if (npcId == 1101) {// Powerful sea snake
            SeaSnake.handleDeath();
            SeaSnake.giveReward();
        }

        if (SeaSnake.seaSnakeMob(npc.getNpcId()) && c != null) {// Powerful sea snake
            SeaSnake.death(c, npc.getNpcId());
        }


        if (Nex.nexMob(npc.getNpcId()) && c != null) {
            Nex.death(c, npc.getNpcId());
        }

        if ((npcId == 3129)) {// zammy bosses for instance respawning
            KrilTsutsarothInstance.spawned = false;
        }
        if (npcId == 2205) {// sara bosses for instance respawning
            CommanderZilyanaInstance.spawned = false;
        }
        if (npcId == 2215) {// bandos bosses for instance respawning
            GeneralGraadorInstance.spawned = false;
        }
        if (npcId == 3162) {// armadl bosses for instance respawning
            KreeArraInstance.spawned = false;
        }


        //if (npcId == 11278) {// Nex
        //    Nex.handleDeath(c);
        //    Nex.giveReward();
      //  }



        if (npc.getNpcId() == 264) {//Green dragons
            AchievementHandler.activate(c, AchievementList.DRAGON_SLAYER_I, 1);//NEW ACHIEVEMNTS
        }

        if (npc.getNpcId() == 3162 || npc.getNpcId() == 3129 || npc.getNpcId() == 2205 || npc.getNpcId() == 2215) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_GODWARS_BOSSES_OF_ANY_TYPE);
        }
        if (npc.getNpcId() == 9293) {
            c.getEventCalendar().progress(EventChallenge.KILL_BASILISK_KNIGHTS_X_TIMES);
        }

        if (npc.getNpcId() == 4804) {//VOTE BOSS
            AchievementHandler.activate(c, AchievementList.KILL_50_VOTE_BOSS, 1);//NEW ACHIEVEMNTS
        }

        if (npc.getNpcId() == 239) {//KBD
            AchievementHandler.activate(c, AchievementList.DRAGON_SLAYER_II, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.DRAGON_SLAYER_III, 1);//NEW ACHIEVEMNTS
        }

        if (npc.getNpcId() == 8031) {//RUNE DRAGON
            AchievementHandler.activate(c, AchievementList.DRAGON_SLAYER_IV, 1);//NEW ACHIEVEMNTS
        }

        if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {//VETION
            AchievementHandler.activate(c, AchievementList.VETION_I, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.VETION_II, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.VETION_III, 1);//NEW ACHIEVEMNTS
        }


        if (npc.getNpcId() == 2042 || npc.getNpcId() == 2043 || npc.getNpcId() == 2044) {
            c.getEventCalendar().progress(EventChallenge.KILL_ZULRAH_X_TIMES);
        }
        if (npc.getNpcId() == 9021) {
            c.getEventCalendar().progress(EventChallenge.KILL_HUNLLEF_X_TIMES);
        }
        if (IntStream.of(DropManager.wildybossesforgiveaway).anyMatch(id -> id == npc.getNpcId()) && c.getPosition().inWild()) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_WILDY_BOSSES);
        }
        if (npcId >= 7931 && npcId <= 7940) {//Revenants
            c.getEventCalendar().progress(EventChallenge.KILL_X_REVS_IN_WILDY);
            AchievementHandler.activate(c, AchievementList.REVENANT_HUNTER_I, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.REVENANT_HUNTER_II, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.REVENANT_HUNTER_III, 1);//NEW ACHIEVEMNTS
            AchievementHandler.activate(c, AchievementList.REVENANT_HUNTER_IV, 1);//NEW ACHIEVEMNTS
        }

        if (npcId == Npcs.CORPOREAL_BEAST) {
            NPCHandler.kill(Npcs.DARK_ENERGY_CORE, npc.heightLevel);
        }
        if (npcId == 7278) {
            if ((c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().equals("nechryael"))) {
                c.getPA().addSkillXPMultiplied(100, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == Npcs.DUSK_9) {
            Achievements.increase(c, AchievementType.GROTESQUES, 1);
        }
        if (npcId == Npcs.ALCHEMICAL_HYDRA_7) {
            Achievements.increase(c, AchievementType.HYDRA, 1);
        }

        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            if ((c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().equals("dagannoth"))) {
                c.getPA().addSkillXPMultiplied(165, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == 1673 || npcId == 1674 || npcId == 1677 || npcId == 1676 || npcId == 1675
                || npcId == 1672) {//barrows
            Achievements.increase(c, AchievementType.BARROWS_KILLS, 1);
            c.getEventCalendar().progress(EventChallenge.KILL_X_BARROWS_BROTHERS);
            if (EquipmentSet.AHRIM.isWearingBarrows(c) || EquipmentSet.KARIL.isWearingBarrows(c)
                    || EquipmentSet.DHAROK.isWearingBarrows(c)
                    || EquipmentSet.VERAC.isWearingBarrows(c)
                    || EquipmentSet.GUTHAN.isWearingBarrows(c)
                    || EquipmentSet.TORAG.isWearingBarrows(c)) {
                c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.BARROWS_CHEST);
            }
        }
        if (npcId == 7144 || npcId == 7145 || npcId == 7146) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_DEMONIC_GORILLAS, 1);
        }

        if (npcId == 1739 || npcId == 1740 || npcId == 1741 || npcId == 1742) {
            c.getEventCalendar().progress(EventChallenge.GAIN_X_PEST_CONTROL_POINTS, 7);
            c.pcPoints += 7;
        }

        if (npcId == FragmentOfSeren.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            int randomPkp = Misc.random(15) + 10;
            c.pkp += randomPkp;
            c.getQuestTab().updateInformationTab();
            c.sendMessage("Well done! You killed Seren!");
            c.sendMessage("You received: " + randomPkp + " pk Points for killing seren.");
            if (Misc.hasOneOutOf(SEREN_KEY_CHANCE)) {//Rewards the seren key to all players that are fighting the boss
                PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.SEREN_AREA))
                        .forEach(p -> {
                            p.getItems().addItemToBankOrDrop(6792, 1);
                            PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(p.getLoginName()) + "@blu@ has received the @red@Seren key @blu@from @red@Seren.");
                            Discord.writeDropsSyncMessage("" + p.getLoginName() + " has received the seren key.");
                        });
            }
            ;

        }

        //Revenants killed to spawn the revenant maledictus boss
        if (npc.getNpcId() == 7940 || npc.getNpcId() == 7938 || npc.getNpcId() == 7939 && npc.getHealth().getCurrentHealth() <= 0) {
            int chance = Misc.random(85);//85
            if (chance == 0) {
                if (superiorRevenantSpawned) {
                    return;
                }
                NPCSpawning.spawnNpcOld(5001, c.getX(), c.getY() - 1, 0, 1, 1250, 30, 650, 200);
                c.sendMessage("@red@You feel a strong superior revenant appear some where in the cave...");
                superiorRevenantSpawned = true;
            }
        }




        if (npc.getNpcId() == 5001 && npc.getHealth().getCurrentHealth() <= 0) {//Revenant maledictus normal
            rewardPlayersSuperior();
            NPCDeath.superiorRevenantSpawned = false;
            npc.setDead(true);
        }


        if (npcId == 6008 && npc.getHealth().getCurrentHealth() <= 0) {//Kratos
            Kratos.rewardPlayers();
            AchievementHandler.activate(c, AchievementList.KILL_25_KRATOS, 1);//NEW ACHIEVEMNTS
        }

        int dropX = npc.absX;
        int dropY = npc.absY;
        int dropHeight = npc.heightLevel;

        if (!PathFinder.getPathFinder().accessable(c, dropX, dropY)) {
            for (Position border : npc.getBorder()) {
                if (PathFinder.getPathFinder().accessable(c, dropX, dropY)) {
                    dropX = border.getX();
                    dropY = border.getY();
                    break;
                }
            }
        }

        if (npcId == 492 || npcId == 494 || npcId == NightmareConstants.NIGHTMARE_ACTIVE_ID || npcId == Npcs.VORKATH) {
            dropX = c.absX;
            dropY = c.absY;
        }
        if (npcId == 2042 || npcId == 2043 || npcId == 2044
                || npcId == 6720) {
            dropX = 2268;
            dropY = 3069;
            c.getItems().addItem(12938, 1);
            c.getZulrahEvent().stop();
        }
        if (npcId == Kraken.KRAKEN_ID) {
            dropX = 2280;
            dropY = 10031;
        }

        if (npcId == 1101) {// powerful sea snake
            dropX = c.absX;
            dropY = c.absY;
        }

      //  if (npcId == 11278) {// Nex
      //      dropX = c.absX;
      //      dropY = c.absY;
      //  }

        if (npcId == 11283 || npcId == 11284 || npcId == 11285
                || npcId == 11286) {// Nex minions
            dropX = c.absX;
            dropY = c.absY;
        }

        /**
         * Warriors guild
         */
        c.getWarriorsGuild().dropDefender(npc.absX, npc.absY);
        if (AnimatedArmour.isAnimatedArmourNpc(npcId)) {
            if (npc.getX() == 2851 && npc.getY() == 3536) {
                dropX = 2851;
                dropY = 3537;
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY + 1);
            } else if (npc.getX() == 2857 && npc.getY() == 3536) {
                dropX = 2857;
                dropY = 3537;
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY + 1);
            } else {
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY);
            }
        }


        Location3D location = new Location3D(dropX, dropY, dropHeight);
        int amountOfDrops = 1;
        if (isDoubleDrops()) {
            amountOfDrops++;
        }

        int bossPoints = BossPoints.getPointsOnDeath(npc);
        BossPoints.addPoints(c, bossPoints, false);

        if (NpcDef.forId(npcId).getCombatLevel() >= 1) {
            c.getNpcDeathTracker().add(NpcDef.forId(npcId).getName(), NpcDef.forId(npcId).getCombatLevel(), bossPoints);
        }

        Server.getDropManager().create(c, npc, location, amountOfDrops, npcId);
    }

    public static void announce(Player player, GameItem item, int npcId) {
        if (!player.getDisplayName().equalsIgnoreCase("thimble") && !player.getDisplayName().equalsIgnoreCase("top hat")) {
            announceNpcDrop(player, item, npcId, player.getNpcDeathTracker().getKc(NpcDef.forId(npcId).getName()));
        }
    }

    public static void announceKc(Player player, GameItem item, int kc) {
        String rarityLabel = ItemAttributes.rarityLabel(item.getAttrs());
        PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop" + rarityLabel + ": " +
                "" + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() + " at <col=E9362B>" + kc + "</col>@pur@ kills.");

        Discord.writeDropsSyncMessage("" + player.getLoginName() + " received a drop" + rarityLabel + ": " +
                "" + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() + " at " + kc + " kills.");
    }

    private static void announceNpcDrop(Player player, GameItem item, int npcId, int kc) {
        String npcName = NpcDef.forId(npcId).getName();
        String rarityLabel = ItemAttributes.rarityLabel(item.getAttrs());
        PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop" + rarityLabel + ": " +
                "" + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() + " from " + npcName
                + " at <col=E9362B>" + kc + "</col>@pur@ kills.");

        Discord.writeDropsSyncMessage("" + player.getLoginName() + " received a drop" + rarityLabel + ": " +
                "" + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() + " from " + npcName
                + " at " + kc + " kills.");
    }

    public static boolean isDoubleDrops() {
        return (Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS);
    }
}
