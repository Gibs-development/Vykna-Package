package io.xeros.content.questsystem.sample;

import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.SpellBook;
import io.xeros.model.entity.player.Player;

public class DesertTreasureQuestHandler implements QuestHandler {
    public static final String QUEST_ID = "desert_treasure";
    public static final int STAGE_NOT_STARTED = 0;
    public static final int STAGE_STARTED = 10;
    public static final int STAGE_RING_OBTAINED = 20;
    public static final int STAGE_MALAK_TALKED = 30;
    public static final int STAGE_DIAMONDS_READY = 80;
    public static final int STAGE_AZZANADRA = 90;
    public static final int STAGE_COMPLETE = 100;

    public static final int RING_OF_VISIBILITY = 4657;
    public static final int BLOOD_DIAMOND = 4670;
    public static final int ICE_DIAMOND = 4671;
    public static final int SMOKE_DIAMOND = 4672;
    public static final int SHADOW_DIAMOND = 4673;

    public static final int NPC_RASOLO = 679;
    public static final int NPC_MALAK = 686;
    public static final int NPC_EBLIS = 688;
    public static final int NPC_AZZANADRA = 730;

    public static final int NPC_DESSOUS = 3459;
    public static final int NPC_FAREED = 3456;
    public static final int NPC_KAMIL = 3458;
    public static final int NPC_DAMIS = 682;

    public static final int ANCIENT_ALTAR_OBJECT_ID = 6552;

    private static final String VAR_BLOOD = "dt.bloodDiamond";
    private static final String VAR_ICE = "dt.iceDiamond";
    private static final String VAR_SMOKE = "dt.smokeDiamond";
    private static final String VAR_SHADOW = "dt.shadowDiamond";

    @Override
    public String questId() {
        return QUEST_ID;
    }

    @Override
    public int completionStage() {
        return STAGE_COMPLETE;
    }

    @Override
    public int nextStage(Player player, QuestProgress progress, QuestEvent event) {
        if (player == null || progress == null || event == null) {
            return progress == null ? STAGE_NOT_STARTED : progress.getStage();
        }
        if (progress.getState() != QuestState.IN_PROGRESS) {
            return progress.getStage();
        }
        switch (event.getType()) {
            case NPC_TALK:
                return handleNpcTalk(player, progress, event);
            case NPC_KILL:
                return handleNpcKill(player, progress, event);
            case ITEM_ON_OBJECT:
                return handleItemOnObject(player, progress, event);
            default:
                return progress.getStage();
        }
    }

    private int handleNpcTalk(Player player, QuestProgress progress, QuestEvent event) {
        Object npcValue = event.get(QuestEventKeys.NPC_ID);
        if (!(npcValue instanceof Number)) {
            return progress.getStage();
        }
        int npcId = ((Number) npcValue).intValue();
        if (npcId == NPC_RASOLO) {
            if (!hasRing(player)) {
                player.getItems().addItemUnderAnyCircumstance(RING_OF_VISIBILITY, 1);
                player.sendMessage("Rasolo hands you a ring of visibility.");
            }
            return advanceTo(progress, STAGE_RING_OBTAINED);
        }
        if (npcId == NPC_MALAK) {
            if (!hasRing(player)) {
                return progress.getStage();
            }
            return advanceTo(progress, STAGE_MALAK_TALKED);
        }
        if (npcId == NPC_AZZANADRA) {
            if (progress.getStage() < STAGE_AZZANADRA) {
                return progress.getStage();
            }
            if (progress.getStage() >= STAGE_COMPLETE) {
                return progress.getStage();
            }
            grantCompletionRewards(player);
            return STAGE_COMPLETE;
        }
        if (npcId == NPC_EBLIS) {
            return progress.getStage();
        }
        return progress.getStage();
    }

    private int handleNpcKill(Player player, QuestProgress progress, QuestEvent event) {
        Object npcValue = event.get(QuestEventKeys.NPC_ID);
        if (!(npcValue instanceof Number)) {
            return progress.getStage();
        }
        if (progress.getStage() < STAGE_MALAK_TALKED) {
            return progress.getStage();
        }
        int npcId = ((Number) npcValue).intValue();
        boolean updated = false;
        if (npcId == NPC_DESSOUS && !getBool(progress, VAR_BLOOD)) {
            setBool(progress, VAR_BLOOD, true);
            player.getItems().addItemUnderAnyCircumstance(BLOOD_DIAMOND, 1);
            updated = true;
        } else if (npcId == NPC_FAREED && !getBool(progress, VAR_SMOKE)) {
            setBool(progress, VAR_SMOKE, true);
            player.getItems().addItemUnderAnyCircumstance(SMOKE_DIAMOND, 1);
            updated = true;
        } else if (npcId == NPC_KAMIL && !getBool(progress, VAR_ICE)) {
            setBool(progress, VAR_ICE, true);
            player.getItems().addItemUnderAnyCircumstance(ICE_DIAMOND, 1);
            updated = true;
        } else if (npcId == NPC_DAMIS && !getBool(progress, VAR_SHADOW)) {
            setBool(progress, VAR_SHADOW, true);
            player.getItems().addItemUnderAnyCircumstance(SHADOW_DIAMOND, 1);
            updated = true;
        }
        if (!updated) {
            return progress.getStage();
        }
        int diamondCount = countDiamonds(progress);
        if (diamondCount >= 4) {
            return advanceTo(progress, STAGE_DIAMONDS_READY);
        }
        int staged = STAGE_MALAK_TALKED + (diamondCount * 10);
        return advanceTo(progress, staged);
    }

    private int handleItemOnObject(Player player, QuestProgress progress, QuestEvent event) {
        Object objectValue = event.get(QuestEventKeys.OBJECT_ID);
        Object itemValue = event.get(QuestEventKeys.ITEM_ID);
        if (!(objectValue instanceof Number) || !(itemValue instanceof Number)) {
            return progress.getStage();
        }
        int objectId = ((Number) objectValue).intValue();
        int itemId = ((Number) itemValue).intValue();
        if (objectId != ANCIENT_ALTAR_OBJECT_ID) {
            return progress.getStage();
        }
        if (progress.getStage() < STAGE_MALAK_TALKED || progress.getStage() >= STAGE_AZZANADRA) {
            return progress.getStage();
        }
        if (!isDiamond(itemId)) {
            return progress.getStage();
        }
        if (!hasAllDiamonds(player)) {
            player.sendMessage("You need all four diamonds to free Azzanadra.");
            return progress.getStage();
        }
        removeDiamonds(player);
        player.sendMessage("The altar absorbs the diamonds and a seal breaks.");
        return advanceTo(progress, STAGE_AZZANADRA);
    }

    private void grantCompletionRewards(Player player) {
        player.setSpellBook(SpellBook.ANCIENT);
        player.getPA().sendFrame126("Desert Treasure", QuestSystem.QUEST_REWARD_TITLE_ID);
        player.getPA().sendFrame126("Quest Points: 3", QuestSystem.QUEST_REWARD_LINE_1);
        player.getPA().sendFrame126("Ancient Magicks", QuestSystem.QUEST_REWARD_LINE_2);
        player.getPA().sendFrame126("Access to the Ancient spellbook", QuestSystem.QUEST_REWARD_LINE_3);
        player.getPA().showInterface(QuestSystem.QUEST_REWARD_INTERFACE_ID);
    }

    private boolean hasRing(Player player) {
        return player.getItems().getTotalCount(RING_OF_VISIBILITY) > 0;
    }

    private boolean isDiamond(int itemId) {
        return itemId == BLOOD_DIAMOND || itemId == ICE_DIAMOND || itemId == SMOKE_DIAMOND || itemId == SHADOW_DIAMOND;
    }

    private boolean hasAllDiamonds(Player player) {
        return player.getItems().playerHasItem(BLOOD_DIAMOND, 1)
                && player.getItems().playerHasItem(ICE_DIAMOND, 1)
                && player.getItems().playerHasItem(SMOKE_DIAMOND, 1)
                && player.getItems().playerHasItem(SHADOW_DIAMOND, 1);
    }

    private void removeDiamonds(Player player) {
        player.getItems().deleteItem2(BLOOD_DIAMOND, 1);
        player.getItems().deleteItem2(ICE_DIAMOND, 1);
        player.getItems().deleteItem2(SMOKE_DIAMOND, 1);
        player.getItems().deleteItem2(SHADOW_DIAMOND, 1);
    }

    private int countDiamonds(QuestProgress progress) {
        int count = 0;
        if (getBool(progress, VAR_BLOOD)) count++;
        if (getBool(progress, VAR_ICE)) count++;
        if (getBool(progress, VAR_SMOKE)) count++;
        if (getBool(progress, VAR_SHADOW)) count++;
        return count;
    }

    private boolean getBool(QuestProgress progress, String key) {
        Object value = progress.getVars().get(key);
        return value instanceof Boolean && (Boolean) value;
    }

    private void setBool(QuestProgress progress, String key, boolean value) {
        progress.getVars().put(key, value);
    }

    private int advanceTo(QuestProgress progress, int stage) {
        return Math.max(progress.getStage(), stage);
    }
}
