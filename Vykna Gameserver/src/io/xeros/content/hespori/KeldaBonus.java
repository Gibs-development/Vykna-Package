package io.xeros.content.hespori;

import io.xeros.content.QuestTab;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class KeldaBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeKeldaSeed = true;
        Hespori.KELDA_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayNameFormatted() + " @bla@donated an Kelda seed which" +
                " granted @red@1 hour of 2x Larren's keys.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKeldaSeed = false;
        Hespori.KELDA_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KELDA;
    }
}
