package io.xeros.content.bossfactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.xeros.model.entity.npc.NPC;

public class BossFactoryRegistry {
    private static final String CONTROLLER_KEY = "bossfactory_controller";
    private static final Map<Integer, Supplier<BossController>> REGISTRY = new ConcurrentHashMap<>();

    public static void register(int npcId, Supplier<BossController> supplier) {
        REGISTRY.put(npcId, supplier);
    }

    public static boolean isBoss(NPC npc) {
        return npc != null && isBossId(npc.getNpcId());
    }

    public static boolean isBossId(int npcId) {
        return REGISTRY.containsKey(npcId);
    }

    public static BossController getOrCreate(NPC npc) {
        BossController existing = getIfPresent(npc);
        if (existing != null) {
            return existing;
        }
        Supplier<BossController> supplier = REGISTRY.get(npc.getNpcId());
        if (supplier == null) {
            throw new IllegalStateException("No BossFactory controller registered for npcId=" + npc.getNpcId());
        }
        BossController controller = supplier.get();
        controller.bind(npc);
        npc.getAttributes().set(CONTROLLER_KEY, controller);
        return controller;
    }

    public static BossController getIfPresent(NPC npc) {
        if (npc == null) {
            return null;
        }
        Object controller = npc.getAttributes().get(CONTROLLER_KEY);
        return controller instanceof BossController ? (BossController) controller : null;
    }

    public static void cleanupIfBoss(NPC npc, BossCleanupReason reason) {
        if (!isBoss(npc)) {
            return;
        }
        BossController controller = getIfPresent(npc);
        if (controller != null) {
            controller.cleanup(reason);
        }
    }
}
