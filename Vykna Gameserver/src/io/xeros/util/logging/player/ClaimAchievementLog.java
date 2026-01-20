package io.xeros.util.logging.player;

import io.xeros.content.redundant_achievement.Achievements;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;

public class ClaimAchievementLog extends PlayerLog {

    private final Achievements.Achievement achievement;

    public ClaimAchievementLog(Player player, Achievements.Achievement achievement) {
        super(player);
        this.achievement = achievement;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("claimed_achievement");
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("Claimed redundant_achievement {}", achievement);
    }
}
