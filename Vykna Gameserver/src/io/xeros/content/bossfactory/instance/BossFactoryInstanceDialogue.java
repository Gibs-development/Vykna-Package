package io.xeros.content.bossfactory.instance;

import io.xeros.content.bossfactory.BossMechanic;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;

public class BossFactoryInstanceDialogue {
    private static final int COST = 200_000;
    private static final double MIN_HP_SCALE = 0.6;
    private static final double MIN_DROP_SCALE = 0.2;
    private static final int OPTIONAL_MECHANICS = 2;

    public static void open(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player)
                .option("Boss Factory Instance",
                        new DialogueOption("Start instance (200k)", plr -> startInstance(plr)),
                        new DialogueOption("Join instance", BossFactoryInstanceDialogue::joinInstance));
        builder.send();
    }

    private static void startInstance(Player player) {
        if (!player.getItems().playerHasItem(Items.COINS, COST)) {
            player.sendMessage("You need 200,000 coins to start a Boss Factory instance.");
            return;
        }
        BossFactoryInstanceConfig config = new BossFactoryInstanceConfig();
        DialogueBuilder builder = new DialogueBuilder(player)
                .option("Instance join policy",
                        new DialogueOption("Solo (locked)", plr -> {
                            config.setJoinPolicy(JoinPolicy.SOLO);
                            openMechanicsDialogue(plr, config);
                        }),
                        new DialogueOption("Open (others may join)", plr -> {
                            config.setJoinPolicy(JoinPolicy.OPEN);
                            openMechanicsDialogue(plr, config);
                        }));
        builder.send();
    }

    private static void openMechanicsDialogue(Player player, BossFactoryInstanceConfig config) {
        double enabledFraction = config.enabledFraction(OPTIONAL_MECHANICS);
        double hpScale = Math.max(MIN_HP_SCALE, enabledFraction);
        double dropScale = Math.max(MIN_DROP_SCALE, enabledFraction);
        String tileLabel = BossMechanic.FLOOR_SPLAT.getDisplayName() + statusLabel(config.isEnabled(BossMechanic.FLOOR_SPLAT));
        String beamLabel = BossMechanic.ARENA_BEAM.getDisplayName() + statusLabel(config.isEnabled(BossMechanic.ARENA_BEAM));

        DialogueBuilder builder = new DialogueBuilder(player)
                .statement("Configure mechanics:",
                        tileLabel,
                        beamLabel,
                        "HP: " + (int) Math.round(hpScale * 100) + "%",
                        "Drops: " + (int) Math.round(dropScale * 100) + "%")
                .option("Toggle mechanics",
                        new DialogueOption("Toggle Tile Splat", plr -> {
                            config.toggleMechanic(BossMechanic.FLOOR_SPLAT);
                            openMechanicsDialogue(plr, config);
                        }),
                        new DialogueOption("Toggle Arena Beam", plr -> {
                            config.toggleMechanic(BossMechanic.ARENA_BEAM);
                            openMechanicsDialogue(plr, config);
                        }),
                        new DialogueOption("Confirm instance", plr -> confirmInstance(plr, config)),
                        new DialogueOption("Cancel", plr -> plr.getPA().closeAllWindows()));
        builder.send();
    }

    private static void confirmInstance(Player player, BossFactoryInstanceConfig config) {
        if (!player.getItems().playerHasItem(Items.COINS, COST)) {
            player.sendMessage("You need 200,000 coins to start a Boss Factory instance.");
            return;
        }
        if (BossFactoryInstanceManager.getByOwner(player.getLoginNameLower()).filter(instance -> !instance.isExpired()).isPresent()) {
            player.sendMessage("You already have an active Boss Factory instance.");
            return;
        }
        player.getItems().deleteItem(Items.COINS, COST);
        BossFactoryInstance instance = BossFactoryInstanceManager.create(player, config);
        instance.enter(player);
        player.getPA().closeAllWindows();
    }

    private static void joinInstance(Player player) {
        player.getPA().sendEnterString("Enter instance owner name", (plr, name) -> {
            BossFactoryInstanceManager.getByOwner(name)
                    .filter(instance -> !instance.isExpired())
                    .ifPresentOrElse(instance -> {
                        if (instance.getConfig().getJoinPolicy() == JoinPolicy.SOLO && !instance.getOwner().equals(plr)) {
                            plr.sendMessage("That instance is locked to the owner.");
                            return;
                        }
                        if (!instance.isOwnerInside() && !instance.getOwner().equals(plr)) {
                            plr.sendMessage("You can only join while the instance owner is inside.");
                            return;
                        }
                        instance.enter(plr);
                        plr.getPA().closeAllWindows();
                    }, () -> plr.sendMessage("No active Boss Factory instance found for that owner."));
        });
    }

    private static String statusLabel(boolean enabled) {
        return enabled ? " (Active)" : " (Disabled)";
    }
}
