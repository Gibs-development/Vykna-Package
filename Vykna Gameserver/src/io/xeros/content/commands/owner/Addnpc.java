package io.xeros.content.commands.owner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.NpcWalkingType;
import io.xeros.model.entity.npc.data.NpcMaxHit;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds an NPC spawn to a per-npc file and spawns it in-game.
 * Usage: ::addnpc <npcId> [walkingType]
 */
public class Addnpc extends Command {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIRECTORY = Server.getDataDirectory() + "/cfg/npc/spawns/";

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input == null ? new String[0] : input.trim().split("\\s+");
        if (args.length < 1 || args[0].isEmpty()) {
            player.sendMessage("Usage: ::addnpc <npcId> [walkingType]");
            return;
        }

        int npcId;
        try {
            npcId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid npc id: " + args[0]);
            return;
        }

        NpcWalkingType walkingType = NpcWalkingType.WALK;
        if (args.length > 1) {
            try {
                walkingType = NpcWalkingType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Invalid walkingType. Try: WALK, STAND, RUN.");
                return;
            }
        }

        File dir = new File(DIRECTORY);
        if (!dir.exists() && !dir.mkdirs()) {
            player.sendMessage("Failed to create npc spawn directory.");
            return;
        }

        File file = new File(DIRECTORY + npcId + ".json");
        List<NpcSpawn> spawns = new ArrayList<>();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                List<NpcSpawn> loaded = GSON.fromJson(reader, new TypeToken<List<NpcSpawn>>() {}.getType());
                if (loaded != null) {
                    spawns.addAll(loaded);
                }
            } catch (Exception e) {
                player.sendMessage("Failed to read existing spawn file, creating a new one.");
            }
        }

        Position pos = new Position(player.absX, player.absY, player.heightLevel);
        spawns.add(new NpcSpawn(npcId, pos, walkingType));

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(spawns));
        } catch (Exception e) {
            player.sendMessage("Failed to write spawn file.");
            return;
        }

        NPCSpawning.spawnNpc(player, npcId, player.absX, player.absY, player.heightLevel,
                walkingType.ordinal(), NpcMaxHit.getMaxHit(npcId), false, false);

        player.sendMessage("Spawned npc " + npcId + " and saved to " + file.getName() + ".");
    }

    private static class NpcSpawn {
        private final int id;
        private final Position position;
        private final NpcWalkingType walkingType;

        private NpcSpawn(int id, Position position, NpcWalkingType walkingType) {
            this.id = id;
            this.position = position;
            this.walkingType = walkingType;
        }
    }
}
