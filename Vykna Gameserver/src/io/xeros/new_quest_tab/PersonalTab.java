package io.xeros.new_quest_tab;


import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.world_event.Tournament;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * The personal tab inside the quest tab.
 *
 * @author C.T for runerogue
 *
 */

public class PersonalTab {


    public static List<Integer> getLines() {
        return IntStream.range(47_924, 47_924 + 400).boxed().collect(Collectors.toList());//51901
    }

    public static void loadInformation(Player player) {
        List<Integer> lines = getLines();
        int index = 0;

        player.getPA().sendFrame126("<col=CC8400>Personal information", 46_523);//11522

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

        points(player, player.donatorPoints, "<col=CC8400>Donator Points", lines.get(index++));
        points(player, player.votePoints, "<col=CC8400>Vote Points", lines.get(index++));
        points(player, player.pkp, "<col=CC8400>PK Points", lines.get(index++));
        points(player, player.bossPoints, "<col=CC8400>Boss Points", lines.get(index++));
        points(player, player.pcPoints, "<col=CC8400>Pest Control Points", lines.get(index++));
        points(player, player.exchangePoints, "<col=CC8400>Exchange Points", lines.get(index++));
        points(player, player.triviaPoints, "<col=CC8400>Trivia Points", lines.get(index++));
        points(player, player.afkPoints, "<col=CC8400>Afk Points", lines.get(index++));
        points(player, player.dailyTaskPoints, "<col=CC8400>Daily Task Points", lines.get(index++));
        points(player, player.getLastManPoints(), "<col=CC8400>Lms Points", lines.get(index++));
        points(player, player.getLastManWins(), "<col=CC8400>Lms Wins", lines.get(index++));
        points(player, player.getEasyClueCounter(), "<col=CC8400>Easy Clues Completed:", lines.get(index++));
        points(player, player.getMediumClueCounter(), "<col=CC8400>Medium Clues Completed:", lines.get(index++));
        points(player, player.getHardClueCounter(), "<col=CC8400>Hard Clues Completed:", lines.get(index++));
        points(player, player.getMasterClueCounter(), "<col=CC8400>Master Clues Completed:", lines.get(index++));

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=10>@or1@ Slayer Information", lines.get(index++));
        points(player, player.getSlayer().getPoints(), "Slayer Points", lines.get(index++));
        if (player.getSlayer().getTask().isEmpty()) {
            player.getPA().sendFrame126("<col=CC8400>- Slayer Task: @gre@ None", lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Slayer Task: @gre@" +player.getSlayer().getTaskAmount()+" "+player.getSlayer().getTask().get().getFormattedName()+"s", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=19>@or1@ Daily Task Information", lines.get(index++));
        points(player, player.totalDailyDone, "Total amount done: @gr3@", lines.get(index++));
        points(player, player.dailyTaskPoints, "Daily points", lines.get(index++));
        points(player, player.dailyTaskAmount, "Tasks completed: @gr3@", lines.get(index++));
        if (player.currentDailyTask != null) {
            player.getPA().sendFrame126("<col=CC8400>- Daily Task: @gre@" +player.currentDailyTask, lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Daily Task: @red@ No task active", lines.get(index++));
        }

        player.getPA().sendFrame126("", lines.get(index++));

        player.getPA().sendFrame126("<img=22>@or1@ Tournament Information", lines.get(index++));
        points(player, player.tournamentPoints, "Tournament points: @gr3@", lines.get(index++));
        points(player, player.tournamentWins, "Tournament wins", lines.get(index++));
        points(player, player.tournyKills, "Tournament kills: @gr3@", lines.get(index++));
        points(player, player.tournyDeaths, "Tournament deaths: @gr3@", lines.get(index++));
        points(player, player.tournamentTotalGames, "Total played: @gr3@", lines.get(index++));
        if (Tournament.isTournamentActive()) {
            player.getPA().sendFrame126("<col=CC8400>- Tourny: @gre@" +Tournament.eventType, lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Tourny: @red@ Not active", lines.get(index++));
        }

        //player.getPA().sendString("", lines.get(index++));

        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }

    }

    private static void points(Player player, int points, String name, int lineId) {
        player.getPA().sendFrame126("<col=CC8400>- " + name + " [@gre@" + Misc.insertCommas(points) + "@or1@]", lineId);
    }
}
