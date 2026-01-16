package io.xeros.new_quest_tab;

import io.xeros.Configuration;
import io.xeros.content.bonus_skill.BonusSkill;
import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.evil_tree.EvilTree;
import io.xeros.content.koranian_event.KoranianEventBossHandler;
import io.xeros.content.revenant_event.RevenantEventBossHandler;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.content.world_boss_events.EventBossHandler;
import io.xeros.content.world_event.WorldEvent;
import io.xeros.content.world_event_galvek.GalvekEventBossHandler;
import io.xeros.content.world_event_solak.SolakEventBossHandler;
import io.xeros.model.entity.player.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.xeros.content.world_event.WorldEvent.timeEventEnd;


/**
 * The Event tab inside the quest tab.
 *
 * @author C.T for runerogue
 *
 */

public class EventTab {

    public static List<Integer> getLines() {
        return IntStream.range(48_924, 48_924 + 400).boxed().collect(Collectors.toList());//51901
    }

    public static void loadInformation(Player player) {

        List<Integer> lines = getLines();
        int index = 0;

        player.getPA().sendFrame126("<col=CC8400>World events", 46_823);

        player.getPA().sendFrame126("<img=29>@or1@ Well of goodwill", lines.get(index++));
        index = addBoostsInformation(player, lines, index);

        player.getPA().sendFrame126("", lines.get(index++));

        // Events Information
        player.getPA().sendFrame126("<img=16>@or1@ World Events", lines.get(index++));

        long milliseconds = Configuration.REV_EVENT_TIMER * 600;
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds - TimeUnit.HOURS.toMillis(hours));
        String time = hours + "h:" + minutes + "min";
        if (RevenantEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Maledictus:@gre@ Rev Caves@red@(41)", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Maledictus:<col=00c0ff> "+time+"", lines.get(index++));
        }


        long milliseconds3 = Configuration.SOLAK_EVENT_TIMER * 600;
        long hours3 = TimeUnit.MILLISECONDS.toHours(milliseconds3);
        long minutes3 = TimeUnit.MILLISECONDS.toMinutes(milliseconds3 - TimeUnit.HOURS.toMillis(hours3));
        String time3 = hours3 + "h:" + minutes3 + "min";
        if (SolakEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Solak:@gre@ has spawned ::Solak", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Solak:<col=00c0ff> "+time3+"", lines.get(index++));
        }


        long milliseconds4 = Configuration.NIGHTMARE_EVENT_TIMER * 600;
        long hours4 = TimeUnit.MILLISECONDS.toHours(milliseconds4);
        long minutes4 = TimeUnit.MILLISECONDS.toMinutes(milliseconds4 - TimeUnit.HOURS.toMillis(hours4));
        String time4 = hours4 + "h:" + minutes4 + "min";
        if (KoranianEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Nightmare:@gre@ Rogue's Castle@red@(52)", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Nightmare:<col=00c0ff> "+time4+"", lines.get(index++));
        }

        long milliseconds5 = Configuration.GLOD_EVENT_TIMER * 600;
        long hours5 = TimeUnit.MILLISECONDS.toHours(milliseconds5);
        long minutes5 = TimeUnit.MILLISECONDS.toMinutes(milliseconds5 - TimeUnit.HOURS.toMillis(hours5));
        String time5 = hours5 + "h:" + minutes5 + "min";
        if (EventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Glod:@gre@ has spawned ::Glod", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Glod:<col=00c0ff> "+time5+"", lines.get(index++));
        }

        long milliseconds6 = Configuration.GALVEK_EVENT_TIMER * 600;
        long hours6 = TimeUnit.MILLISECONDS.toHours(milliseconds6);
        long minutes6 = TimeUnit.MILLISECONDS.toMinutes(milliseconds6 - TimeUnit.HOURS.toMillis(hours6));
        String time6 = hours6 + "h:" + minutes6 + "min";
        if (GalvekEventBossHandler.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Galvek:@gre@ has spawned ::Galvek", lines.get(index++));

        } else {
            player.getPA().sendFrame126("<col=CC8400>- Galvek:<col=00c0ff> "+time6+"", lines.get(index++));
        }

        long milliseconds7 = Configuration.STAR_EVENT_TIMER * 600;
        long hours7 = TimeUnit.MILLISECONDS.toHours(milliseconds7);
        long minutes7 = TimeUnit.MILLISECONDS.toMinutes(milliseconds7 - TimeUnit.HOURS.toMillis(hours7));
        String time7 = hours7 + "h:" + minutes7 + "min";
        if (ShootingStar.getCurrentLocation() != null) {
            player.getPA().sendFrame126("@whi@- Star: @gre@" + ShootingStar.getCurrentLocation().getLocationName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("@whi@- Star:<col=00c0ff> "+time7+"", lines.get(index++));
        }

        long milliseconds8 = Configuration.TREE_EVENT_TIMER * 600;
        long hours8 = TimeUnit.MILLISECONDS.toHours(milliseconds8);
        long minutes8 = TimeUnit.MILLISECONDS.toMinutes(milliseconds8 - TimeUnit.HOURS.toMillis(hours8));
        String time8 = hours8 + "h:" + minutes8 + "min";
        if (EvilTree.getCurrentLocation() != null) {
            player.getPA().sendFrame126("<col=CC8400>- Crystal Tree: @gre@" + EvilTree.getCurrentLocation().getLocationName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Crystal Tree:<col=00c0ff> "+time8+"", lines.get(index++));
        }

        long milliseconds2 = Configuration.BONUS_SKILL_TIMER * 600;
        long hours2 = TimeUnit.MILLISECONDS.toHours(milliseconds2);
        long minutes2 = TimeUnit.MILLISECONDS.toMinutes(milliseconds2 - TimeUnit.HOURS.toMillis(hours2));
        String time2 = hours2 + "h:" + minutes2 + "min";
        if (Configuration.BONUS_SKILL >= 0) {
            player.getPA().sendFrame126("<col=CC8400>- Bonus Skill: @gre@" + BonusSkill.getSkillName(), lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Bonus Skill:<col=00c0ff> " + time2, lines.get(index++));
        }

        long milliseconds9 = Configuration.WILD_EVENT_TIMER * 600;
        long hours9 = TimeUnit.MILLISECONDS.toHours(milliseconds9);
        long minutes9 = TimeUnit.MILLISECONDS.toMinutes(milliseconds9 - TimeUnit.HOURS.toMillis(hours9));
        String time9 = hours9 + "h:" + minutes9 + "min";

        long minutesLeft = ((timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
        if (WorldEvent.eventStartedAnnounced) {
            player.getPA().sendFrame126("<col=CC8400>- Wild Event:@gre@ x2 drops", lines.get(index++));
            player.getPA().sendFrame126("@gre@" + minutesLeft + " minutes remaining.", lines.get(index++));
        } else {
            player.getPA().sendFrame126("<col=CC8400>- Wild Event:<col=00c0ff> "+time9+"", lines.get(index++));
        }

        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }
    }

    private static int addBoostsInformation(Player player,List<Integer> lines, int index) {
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


}
