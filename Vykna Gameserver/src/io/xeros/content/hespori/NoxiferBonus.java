package io.xeros.content.hespori;

import io.xeros.content.QuestTab;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class NoxiferBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeNoxiferSeed = true;
        Hespori.NOXIFER_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayNameFormatted() + " @bla@donated an Noxifer seed which" +
                " granted @red@1 hour of 2x Slayer points.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeNoxiferSeed = false;
        Hespori.NOXIFER_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.NOXIFER;
    }
}
