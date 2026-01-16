package io.xeros.content;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.content.bonus_skill.BonusSkill;
import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.stats.MonsterKillLog;
import io.xeros.content.dailytasks.DailyTasks;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.evil_tree.EvilTree;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.content.koranian_event.KoranianEventBossHandler;
import io.xeros.content.preset.PresetManager;
import io.xeros.content.revenant_event.RevenantEventBossHandler;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.content.world_boss_events.EventBossHandler;
import io.xeros.content.world_event.Tournament;
import io.xeros.content.world_event.WorldEvent;
import io.xeros.content.world_event_galvek.GalvekEventBossHandler;
import io.xeros.content.world_event_solak.SolakEventBossHandler;

import io.xeros.model.Area;
import io.xeros.model.SquareArea;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import static io.xeros.content.world_event.WorldEvent.timeEventEnd;

public class QuestTab {

    public enum Tab {
        INFORMATION(50_417),//50_417
        COIN(50_419),//50_419
        DIARY(50_421),
        DONATOR(50_423)
        ;

        private final int buttonId;

        Tab(int buttonId) {
            this.buttonId = buttonId;
        }

        public int getConfigValue() {
            return ordinal();
        }
    }

    public enum CoinTab {
        COLLECTION_LOG,
        MONSTER_KILL_LOG,
        DROP_TABLE,
        LOOT_TABLES,
        WORLD_EVENTS,
        PRESETS,
        DONATOR_BENEFITS,
        TITLES,
        COMMUNITY_GUIDES,
        VOTE_PAGE,
        ONLINE_STORE,
        FORUMS,
        RULES,
        CALL_FOR_HELP
    }

    private static final int[] COIN_TAB_BUTTONS = {74107, 74112, 74117, 74122, 74127, 74132, 74137, 74142, 74147, 74152, 74157, 74162};

    public static final int INTERFACE_ID = 46220;//50414
    private static final int CONFIG_ID = 1355;

    public static void updateAllQuestTabs() {
        Arrays.stream(PlayerHandler.players).forEach(player -> {
            if (player != null) {
                player.getQuestTab().updateInformationTab();
            }
        });
    }

    private final Player player;

    public QuestTab(Player player) {
        this.player = player;
    }

    private boolean sentDiaries = false;
    public void openTab(Tab tab) {
        if (!sentDiaries && tab == Tab.DIARY) {
            TasksInterface.sendAchievementsEntries(player);
            TasksInterface.sendDiaryEntries(player);
            sentDiaries = true;
        }
        player.getPA().sendConfig(CONFIG_ID, tab.getConfigValue());
    }

    public boolean handleActionButton(int buttonId) {
        for (Tab tab : Tab.values()) {
            if (buttonId == tab.buttonId) {
                openTab(tab);
                return true;
            }
        }

        return false;
    }

    public List<Integer> getLines() {
        return IntStream.range(51901, 51901 + 300).boxed().collect(Collectors.toList());//51901
    }

    /**
     * Testiong View
     */
    public void updateInformationTab() {
        List<Integer> lines = getLines();
        int index = 0;

        // Server Information
        player.getPA().sendFrame126("@cr2@@or1@ "+Configuration.SERVER_NAME+" Information", lines.get(index++));
        if (player.getRights().contains(Right.OWNER)) {
            player.getPA().sendFrame126("<col=CC8400>- Players: @gre@" + PlayerHandler.getPlayerCount() + " (u" + PlayerHandler.getUniquePlayerCount() + ") (m" + Configuration.PLAYERMODIFIER + ")", lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Players online: @gre@" + PlayerHandler.getPlayerCount(), lines.get(index++));
        }
        player.getPA().sendFrame126("<col=CC8400>- Wilderness count: @gre@"+(Boundary.getPlayersInBoundary(Boundary.WILDERNESS) + Boundary.getPlayersInBoundary(Boundary.WILDERNESS_UNDERGROUND)), lines.get(index++));

        player.getPA().sendFrame126("", lines.get(index++));

        // Events Information
        player.getPA().sendFrame126("<img=16>@or1@ World Events", lines.get(index++));

        if(RevenantEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Maledictus:@gre@ Rev Caves@red@(41)", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Maledictus:@red@ not spawned", lines.get(index++));
        }

        if(SolakEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Solak:@gre@ has spawned ::Solak", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Solak:@red@ not spawned", lines.get(index++));
        }
        if(KoranianEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Nightmare:@gre@ Rogue's Castle@red@(52)", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Nightmare:@red@ not spawned", lines.get(index++));
        }

        if(EventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Glod:@gre@ has spawned ::Glod", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Glod:@red@ not spawned", lines.get(index++));
        }

        if(GalvekEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Galvek:@gre@ has spawned ::Galvek", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Galvek:@red@ not spawned", lines.get(index++));
        }

        if(ShootingStar.getCurrentLocation() != null) {
            player.getPA().sendFrame126("@whi@- Star: @gre@" + ShootingStar.getCurrentLocation().getLocationName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@whi@- Star: @red@not crashed", lines.get(index++));
        }

        if(EvilTree.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Crystal Tree: @gre@" + EvilTree.getCurrentLocation().getLocationName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Crystal Tree: @red@not sprouted", lines.get(index++));
        }

        long milliseconds2 = Configuration.BONUS_SKILL_TIMER * 600;
        long hours2 = TimeUnit.MILLISECONDS.toHours(milliseconds2);
        long minutes2 = TimeUnit.MILLISECONDS.toMinutes(milliseconds2 - TimeUnit.HOURS.toMillis(hours2));
        String time2 = hours2 + "h:" + minutes2 + "min";
        if (Configuration.BONUS_SKILL >= 0) {
            player.getPA().sendFrame126("<col=CC8400>- Bonus Skill: @gre@" +BonusSkill.getSkillName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Bonus Skill: @red@" + time2, lines.get(index++));
        }

        long minutesLeft = ((timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
        if (WorldEvent.eventStartedAnnounced) {
            player.getPA().sendFrame126("<col=CC8400>- Wild Event:@gre@ x2 drops", lines.get(index++));
            player.getPA().sendFrame126("@gre@" + minutesLeft + " minutes remaining.", lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Wild Event:@red@ Not active", lines.get(index++));
        }


        index = addBoostsInformation(lines, index);

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=10>@or1@ Slayer Information", lines.get(index++));
        points(player.getSlayer().getPoints(), "Slayer Points", lines.get(index++));
        if (player.getSlayer().getTask().isEmpty()) {
            player.getPA().sendFrame126("<col=CC8400>- Slayer Task: @gre@ None", lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Slayer Task: @gre@" +player.getSlayer().getTaskAmount()+" "+player.getSlayer().getTask().get().getFormattedName()+"s", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=19>@or1@ Daily Task Information", lines.get(index++));
        points(player.totalDailyDone, "Total amount done: @gr3@", lines.get(index++));
        points(player.dailyTaskPoints, "Daily points", lines.get(index++));
        points(player.dailyTaskAmount, "Tasks completed: @gr3@", lines.get(index++));
        if (player.currentDailyTask != null) {
            player.getPA().sendFrame126("<col=CC8400>- Daily Task: @gre@" +player.currentDailyTask, lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Daily Task: @red@ No task active", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=29>@or1@ Tournament Information", lines.get(index++));
        points(player.tournamentPoints, "Tournament points: @gr3@", lines.get(index++));
        points(player.tournamentWins, "Tournament wins", lines.get(index++));
        points(player.tournyKills, "Tournament kills: @gr3@", lines.get(index++));
        points(player.tournyDeaths, "Tournament deaths: @gr3@", lines.get(index++));
        points(player.tournamentTotalGames, "Total played: @gr3@", lines.get(index++));
        if (Tournament.isTournamentActive()) {
            player.getPA().sendFrame126("<col=CC8400>- Tourny: @gre@" +Tournament.eventType, lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Tourny: @red@ Not active", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("@cr1@@or1@ Player Information", lines.get(index++));

        // Player Information
        player.getPA().sendFrame126("<col=CC8400>- Rank: @gre@" + player.getRights().buildCrownString() + " " + player.getRights().getPrimary().toString(), lines.get(index++));
        player.getPA().sendFrame126("<col=CC8400>- Total donated: @gre@$" + player.amDonated, lines.get(index++));
        player.getPA().sendFrame126("<col=CC8400>- Drop rate bonus: @gre@" + DropManager.getModifier1(player), lines.get(index++));

        // Time played
        long milliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
        String time = days + " days, " + hours + " hrs";
        player.getPA().sendFrame126("<col=CC8400>- Time Played: @gre@"+time, lines.get(index++));

        player.getPA().sendFrame126("<col=CC8400>- KDR: @gre@"+ (double)(player.deathcount == 0 ? player.killcount + player.deathcount : player.killcount/player.deathcount), lines.get(index++));

        points(player.donatorPoints, "<col=CC8400>Donator Points", lines.get(index++));
        points(player.votePoints, "<col=CC8400>Vote Points", lines.get(index++));
        points(player.pkp, "<col=CC8400>PK Points", lines.get(index++));
        points(player.bossPoints, "<col=CC8400>Boss Points", lines.get(index++));
        points(player.pcPoints, "<col=CC8400>Pest Control Points", lines.get(index++));
        points(player.exchangePoints, "<col=CC8400>Exchange Points", lines.get(index++));
        points(player.triviaPoints, "<col=CC8400>Trivia Points", lines.get(index++));
        points(player.afkPoints, "<col=CC8400>Afk Points", lines.get(index++));
        points(player.dailyTaskPoints, "<col=CC8400>Daily Task Points", lines.get(index++));
        points(player.getLastManPoints(), "<col=CC8400>Lms Points", lines.get(index++));
        points(player.getLastManWins(), "<col=CC8400>Lms Wins", lines.get(index++));
        points(player.getEasyClueCounter(), "<col=CC8400>Easy Clues Completed:", lines.get(index++));
        points(player.getMediumClueCounter(), "<col=CC8400>Medium Clues Completed:", lines.get(index++));
        points(player.getHardClueCounter(), "<col=CC8400>Hard Clues Completed:", lines.get(index++));
        points(player.getMasterClueCounter(), "<col=CC8400>Master Clues Completed:", lines.get(index++));

        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }
    }

    private void points(int points, String name, int lineId) {
        player.getPA().sendFrame126("<col=CC8400>- " + name + " [@gre@" + Misc.insertCommas(points) + "@or1@]", lineId);
    }

    private int addBoostsInformation(List<Integer> lines, int index) {
        List<? extends Booster<?>> boosts = Boosts.getBoostsOfType(player, null, BoostType.EXPERIENCE);
        if (!boosts.isEmpty()) {
            player.getPA().sendFrame126("<col=00c0ff> " + boosts.get(0).getDescription(), lines.get(index++));
        }

        boosts = Boosts.getBoostsOfType(player, null, BoostType.GENERIC);
        for (Booster<?> boost : boosts) {
            player.getPA().sendFrame126("<col=00c0ff> " + boost.getDescription(), lines.get(index++));
        }

        return index;
    }

    /**
     * Handles all actions within the help tab
     */
    public boolean handleHelpTabActionButton(int button) {
        for (int index = 0; index < COIN_TAB_BUTTONS.length; index++) {
            if (button == COIN_TAB_BUTTONS[index]) {
                CoinTab coinTab = CoinTab.values()[index];
                player.getQuesting().handleHelpTabActionButton(button);
                switch(coinTab) {
                    case WORLD_EVENTS:
                        player.getUpgradeHandler().openInterface();
                        return true;
                    case COLLECTION_LOG:
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
                    case MONSTER_KILL_LOG:
                        MonsterKillLog.openInterface(player);
                        return true;
                    case DROP_TABLE:
                        Server.getDropManager().openDefault(player);
                        return true;
                    case PRESETS:
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
                    case DONATOR_BENEFITS:
                        player.getPA().sendFrame126(Configuration.DONATOR_BENEFITS_LINK, 12000);
                        return true;
                    case TITLES:
                        player.getTitles().display();
                        return true;
                    case COMMUNITY_GUIDES:
                        player.getPA().sendFrame126(Configuration.GUIDES_LINK, 12000);
                        return true;
                    case VOTE_PAGE:
                        player.getPA().sendFrame126(Configuration.VOTE_LINK, 12000);
                        return true;
                    case ONLINE_STORE:
                        player.getPA().sendFrame126(Configuration.STORE_LINK, 12000);
                        return true;
                    case FORUMS:
                        player.getPA().sendFrame126(Configuration.DISCORD_INVITE, 12000);
                        return true;
                    case RULES:
                        player.getPA().sendFrame126(Configuration.DISCORD_INVITE, 12000);
                        return true;
                    case LOOT_TABLES:
                        LootableInterface.openInterface(player);
                        return true;

                    case CALL_FOR_HELP:
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
                    default:
                        return false;
                }
            }
        }

        return false;
    }
}
