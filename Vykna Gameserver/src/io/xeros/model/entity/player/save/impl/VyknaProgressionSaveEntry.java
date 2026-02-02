package io.xeros.model.entity.player.save.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.xeros.content.vykna_progression.VyknaProgressionPlayerState;
import io.xeros.content.vykna_progression.VyknaProgressionPersistence;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.List;

public class VyknaProgressionSaveEntry implements PlayerSaveEntry {
    private static final String KEY = "vykna_progression_state";
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
            VyknaProgressionPlayerState state = GSON.fromJson(value, VyknaProgressionPlayerState.class);
            if (state != null) {
                player.setVyknaProgressionState(state);
            }
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
        // Compact JSON to keep player save entries single-line.
        return GSON.toJson(player.getVyknaProgressionState());
    }

    @Override
    public void login(Player player) {
        VyknaProgressionPersistence.load(player);
    }
}
