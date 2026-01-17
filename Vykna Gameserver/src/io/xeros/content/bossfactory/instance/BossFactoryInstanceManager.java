package io.xeros.content.bossfactory.instance;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.xeros.model.entity.player.Player;

public class BossFactoryInstanceManager {
    private static final Map<String, BossFactoryInstance> INSTANCES = new ConcurrentHashMap<>();

    public static Optional<BossFactoryInstance> getByOwner(String ownerName) {
        return Optional.ofNullable(INSTANCES.get(ownerName.toLowerCase()));
    }

    public static BossFactoryInstance create(Player owner, BossFactoryInstanceConfig config) {
        BossFactoryInstance instance = new BossFactoryInstance(owner, config);
        INSTANCES.put(owner.getLoginNameLower(), instance);
        return instance;
    }

    public static void unregister(String ownerNameLower) {
        INSTANCES.remove(ownerNameLower);
    }
}
