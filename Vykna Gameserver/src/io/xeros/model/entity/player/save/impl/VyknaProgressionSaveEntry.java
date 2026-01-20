package io.xeros.model.entity.player.save.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.xeros.content.vykna_progression.VyknaProgressionPlayerState;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import io.xeros.util.JsonUtil;

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
        VyknaProgressionPlayerState state = GSON.fromJson(value, VyknaProgressionPlayerState.class);
        if (state != null) {
            player.setVyknaProgressionState(state);
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        if (!KEY.equals(key)) {
            return "";
        }
        return JsonUtil.toJson(player.getVyknaProgressionState());
    }

    @Override
    public void login(Player player) {
        // No login hooks needed yet.
    }
}
