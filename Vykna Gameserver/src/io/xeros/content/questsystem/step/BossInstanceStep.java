package io.xeros.content.questsystem.step;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.instance.QuestBossInstance;
import io.xeros.content.questsystem.instance.QuestInstanceManager;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class BossInstanceStep extends QuestStepSupport implements QuestStep {
    private final String stepId;
    private final String questId;
    private final int bossNpcId;
    private final int bossMaxHit;
    private final Boundary boundary;
    private final Position entry;
    private final Position exit;
    private final Position bossSpawn;
    private final long timeoutMillis;
    private final List<String> journalText;

    public BossInstanceStep(String stepId,
                            String questId,
                            int bossNpcId,
                            int bossMaxHit,
                            Boundary boundary,
                            Position entry,
                            Position exit,
                            Position bossSpawn,
                            long timeoutMillis,
                            List<String> journalText) {
        this.stepId = stepId;
        this.questId = questId;
        this.bossNpcId = bossNpcId;
        this.bossMaxHit = bossMaxHit;
        this.boundary = boundary;
        this.entry = entry;
        this.exit = exit;
        this.bossSpawn = bossSpawn;
        this.timeoutMillis = timeoutMillis;
        this.journalText = journalText;
    }

    @Override
    public List<String> getJournalText(QuestProgress progress) {
        return journalText;
    }

    @Override
    public void onStart(Player player, QuestProgress progress) {
        QuestBossInstance instance = QuestInstanceManager.getOrCreate(player, questId, stepId, boundary, entry, exit,
                bossSpawn, bossNpcId, bossMaxHit, timeoutMillis);
        progress.getVars().put(key(stepId, "instanceId"), instance.getInstanceId());
        instance.enter(player);
    }

    @Override
    public void onEvent(Player player, QuestProgress progress, QuestEvent event) {
        if (event.getType() != QuestEventType.NPC_KILL) {
            return;
        }
        Object npcValue = event.get(QuestEventKeys.NPC_ID);
        if (!(npcValue instanceof Number) || ((Number) npcValue).intValue() != bossNpcId) {
            return;
        }
        Object instanceValue = event.get(QuestEventKeys.INSTANCE_ID);
        Object storedInstance = progress.getVars().get(key(stepId, "instanceId"));
        if (storedInstance instanceof Number) {
            if (!(instanceValue instanceof Number)) {
                return;
            }
            long storedId = ((Number) storedInstance).longValue();
            long eventId = ((Number) instanceValue).longValue();
            if (storedId != eventId) {
                return;
            }
        }
        setBool(progress, key(stepId, "complete"), true);
        QuestBossInstance instance = QuestInstanceManager.get(player, questId, stepId);
        if (instance != null) {
            instance.dispose();
        }
    }

    @Override
    public boolean isComplete(Player player, QuestProgress progress) {
        return getBool(progress, key(stepId, "complete"));
    }

    @Override
    public void onComplete(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "complete"), true);
        QuestBossInstance instance = QuestInstanceManager.get(player, questId, stepId);
        if (instance != null) {
            instance.dispose();
        }
    }
}
