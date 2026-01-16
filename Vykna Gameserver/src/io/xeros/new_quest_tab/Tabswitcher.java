package io.xeros.new_quest_tab;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementInterface;
import io.xeros.achievements.AchievementList;
import io.xeros.achievements.InterfaceHandler;
import io.xeros.content.NotificationsTab;
import io.xeros.content.SigilSystem;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.stats.MonsterKillLog;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.content.preset.PresetManager;
import io.xeros.content.questing.LearningTheRopes.LearningTheRopesQuest;
import io.xeros.content.questing.MonkeyMadness.MonkeyMadnessQuest;
import io.xeros.content.questing.Quest;
import io.xeros.content.questing.hftd.HftdQuest;
import io.xeros.model.Area;
import io.xeros.model.SquareArea;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.discord.Discord;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tabswitcher {



    public static boolean handleButton(Player player, int actionButtonId) {
        if (System.currentTimeMillis() - player.lastTabSwitch >= 200) {
            switch (actionButtonId) {
                case 180_120:
                    player.setSidebarInterface(2, 46_520);
                    player.infoTabSelected = 1;
                    return true;

                case 180_121:
                    player.setSidebarInterface(2, 46_820);
                    player.infoTabSelected = 2;
                    return true;

                case 181_135:
                    player.setSidebarInterface(2, 46_120);
                    player.infoTabSelected = 3;
                    return true;

                case 179_202://general tab
                    player.setSidebarInterface(2, 46_220);
                    player.infoTabSelected = 0;
                    return true;

                case 180128://Staff tab
                    if (!player.getRights().hasStaffPosition()) {
                        player.sendMessage("@red@You do not have access to this tab.");
                        return false;
                    }
                    player.getPA().sendString("</col>Rank: " + player.getRights().buildCrownString() + " " + player.getRights().getPrimary().toString(), 43705);
                    if (player.getRights().contains(Right.HELPER) || player.getRights().contains(Right.MODERATOR)) {
                        player.getPA().sendString("You have limited access.", 43704);
                    } else {
                        player.getPA().sendString("You have full access.", 43704);
                    }
                    player.setSidebarInterface(2, 43700);
                    player.sendMessage("Staff tab has been turned on.");
                    player.infoTabSelected = 4;
                    return true;

               // Achievement diaries
                case 183076://New system
                    player.getDiaryManager().getVarrockDiary().display();
                    return true;

                case 183077://New system
                    player.getDiaryManager().getArdougneDiary().display();
                    return true;

                case 183078://New system
                    player.getDiaryManager().getDesertDiary().display();
                    return true;

                case 183079://New system
                    player.getDiaryManager().getFaladorDiary().display();
                    return true;

                case 183080://New system
                    player.getDiaryManager().getFremennikDiary().display();
                    return true;

                case 183081://New system
                    player.getDiaryManager().getKandarinDiary().display();
                    return true;

                case 183082://New system
                    player.getDiaryManager().getKaramjaDiary().display();
                    return true;

                case 183083://New system
                    player.getDiaryManager().getLumbridgeDraynorDiary().display();
                    return true;

                case 183084://New system
                    player.getDiaryManager().getMorytaniaDiary().display();
                    return true;

                case 183085://New system
                    player.getDiaryManager().getWesternDiary().display();
                    return true;

                case 183086://New system
                    player.getDiaryManager().getWildernessDiary().display();
                    return true;

                //Quick actions

                case 215193://Droptable action
                    Server.getDropManager().openDefault(player);
                    return true;

                case 215194://Collection log action
                    CollectionLog group = player.getGroupIronmanCollectionLog();
                    if (group != null) {
                        new DialogueBuilder(player).option(
                                new DialogueOption("Personal", plr -> player.getCollectionLog().openInterface(plr)),
                                new DialogueOption("Group", group::openInterface)
                        ).send();
                        return true;
                    }
                    player.getCollectionLog().openInterface(player);
                    return true;

                case 215195://Achievement action
                    InterfaceHandler.writeText(new AchievementInterface(player, AchievementHandler.AchievementDifficulty.EASY));
                    AchievementInterface.sendInterfaceForAchievement(player, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY);
                    player.setAchievement(AchievementHandler.AchievementDifficulty.EASY);
                    player.getPA().showInterface(35_000);
                    return true;

                case 215196://Loot table action
                    LootableInterface.openInterface(player);
                    return true;

                case 215197://upgrade table action
                    player.getUpgradeHandler().openInterface();
                    return true;

                case 215198://Kill log action
                    MonsterKillLog.openInterface(player);
                    return true;

                case 215199://view presets action
                    Area[] areas = {
                            new SquareArea(3066, 3521, 3135, 3456),
                    };
                    if (Arrays.stream(areas).anyMatch(area -> area.inside(player))) {
                        PresetManager.getSingleton().open(player);
                        player.inPresets = true;
                    } else {
                        player.sendMessage("You must be in Edgeville to open presets.");
                    }
                    return true;

                case 215200://View titles action
                    player.getTitles().display();
                    return true;

                case 215201://Vote action
                    player.getPA().sendFrame126(Configuration.VOTE_LINK, 12000);
                    return true;

                case 215202://Discord action
                    player.getPA().sendFrame126(Configuration.DISCORD_INVITE, 12000);
                    return true;

                case 215203://Request help action
                    List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.HELPER))
                            .collect(Collectors.toList());
                    player.sendMessage("@red@You can also type ::help to report something.");
                    if (staff.size() > 0) {
                        String message = "@blu@[Help] " + player.getDisplayName()
                                + " needs help, PM or TELEPORT and help them.";
                        Discord.writeServerSyncMessage(message);
                        PlayerHandler.sendMessage(message, staff);
                    } else {
                        player.sendMessage("@red@You've activated the help command but there are no staff-members online.");
                        player.sendMessage("@red@Please try contacting a staff on the forums and discord and they will respond ASAP.");
                        player.sendMessage("@red@You can also type ::help to report something.");
                    }
                    return true;

                case 215204://Drop Notification tab
                    player.getPA().sendTabAreaOverlayInterface(42658);
                    return true;

                case 215205://Sigil tab
                    SigilSystem.openInterface(player);
                    return true;

            }
        }
        return false;
    }



}
