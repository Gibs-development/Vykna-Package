package io.xeros.model.entity.player.save.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.xeros.content.questsystem.model.QuestProfile;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import io.xeros.util.JsonUtil;

import java.util.List;

public class QuestProfileSaveEntry implements PlayerSaveEntry {
    private static final String KEY = "quest_profile";
    private static final Gson GSON = new Gson();

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (!KEY.equals(key)) {
            return false;
        }
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        try {
            QuestProfile profile = GSON.fromJson(value, QuestProfile.class);
            if (profile == null) {
                profile = new QuestProfile();
            }
            profile.ensureDefaults();
            player.setQuestProfile(profile);
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        if (!KEY.equals(key)) {
            return "";
        }
        QuestProfile profile = player.getQuestProfile();
        profile.ensureDefaults();
        return new Gson().toJson(profile);
    }

    @Override
    public void login(Player player) {
        QuestProfile profile = player.getQuestProfile();
        if (profile == null) {
            profile = new QuestProfile();
            player.setQuestProfile(profile);
        }
        profile.ensureDefaults();
    }
}
