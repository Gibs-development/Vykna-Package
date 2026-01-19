package io.xeros.model.items;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.wrath.WrathSystem;
import io.xeros.content.skills.Skill;
import io.xeros.model.Graphic;
import io.xeros.model.Items;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PerkManager {
    private static final String ATTR_COMMITTED = "perk.committed";
    private static final String ATTR_CRYSTAL_TEMP = "perk.crystal.temp";

    private static final int[] SCAVENGE_COMMON = {
            Items.AIR_RUNE,
            Items.CHAOS_RUNE,
            Items.NATURE_RUNE,
            Items.SILVER_BAR,
            Items.IRON_ORE
    };

    private static final int[] SCAVENGE_RARE = {
            Items.DEATH_RUNE,
            Items.BLOOD_RUNE,
            Items.RUNITE_ORE,
            Items.DRAGONSTONE
    };

    private static final int[] LOOTING_RESOURCES = {
            Items.COAL,
            Items.MITHRIL_ORE,
            Items.MAGIC_LOGS,
            Items.YEW_LOGS,
            Items.SNAPE_GRASS,
            Items.SUPERIOR_DRAGON_BONES
    };

    private final Player player;
    private final Map<Integer, Integer> activePerks = new HashMap<>();
    private final Map<Integer, Long> cooldowns = new HashMap<>();
    private final Map<Integer, Long> messageCooldowns = new HashMap<>();

    private int aftershockDamage;
    private int ruthlessStacks;
    private long ruthlessExpires;

    public PerkManager(Player player) {
        this.player = player;
    }

    public void rebuild() {
        activePerks.clear();
        ItemAttributes[] attrs = player.playerEquipmentAttrs;
        if (attrs != null) {
            for (ItemAttributes attr : attrs) {
                if (attr == null) {
                    continue;
                }
                registerPerk(attr.perk1, attr.perk1Rank);
                registerPerk(attr.perk2, attr.perk2Rank);
            }
        }

        handleCommitted();
    }

    private void registerPerk(int perkId, int rank) {
        if (perkId <= 0 || rank <= 0) {
            return;
        }
        int existing = activePerks.getOrDefault(perkId, 0);
        if (rank > existing) {
            activePerks.put(perkId, rank);
        }
    }

    public boolean hasPerk(int perkId) {
        return activePerks.getOrDefault(perkId, 0) > 0;
    }

    public int getPerkRank(int perkId) {
        return activePerks.getOrDefault(perkId, 0);
    }

    public double getPerkValue(int perkId, double perRank) {
        return getPerkRank(perkId) * perRank;
    }

    public void activatePerk(int perkId, String effect, long cooldownMs) {
        triggerMessage(perkId, effect, cooldownMs);
        playGfx(perkId);
    }

    public double applyAccuracyBonus(double baseAccuracy) {
        if (hasPerk(PerkModule.INACCURATE)) {
            double penalty = getPerkValue(PerkModule.INACCURATE, 0.01);
            baseAccuracy = Math.max(0.0, baseAccuracy * (1.0 - penalty));
            triggerMessage(PerkModule.INACCURATE, "struggle to land a hit", 10_000);
        }
        return baseAccuracy;
    }

    public int applyMaxHitBonus(Entity defender, int baseMaxHit) {
        double modifier = 1.0;

        if (hasPerk(PerkModule.BLUNTED)) {
            modifier -= getPerkValue(PerkModule.BLUNTED, 0.01);
            triggerMessage(PerkModule.BLUNTED, "swing with reduced force", 10_000);
        }
        if (hasPerk(PerkModule.MEDIOCRITY)) {
            modifier -= getPerkValue(PerkModule.MEDIOCRITY, 0.03);
            triggerMessage(PerkModule.MEDIOCRITY, "limit your peak damage", 10_000);
        }
        if (hasPerk(PerkModule.ERUPTIVE)) {
            modifier += getPerkValue(PerkModule.ERUPTIVE, 0.005);
            triggerMessage(PerkModule.ERUPTIVE, "burn through your foe", 10_000);
        }
        if (hasPerk(PerkModule.FATIGUING)) {
            modifier += 0.75;
            triggerMessage(PerkModule.FATIGUING, "strike harder at the cost of speed", 12_000);
        }

        if (defender instanceof NPC) {
            NPC npc = (NPC) defender;
            if (npc.isDemon()) {
                if (hasPerk(PerkModule.DEMON_SLAYER)) {
                    modifier += 0.07;
                    triggerMessage(PerkModule.DEMON_SLAYER, "slay demonic foes", 10_000);
                }
                if (hasPerk(PerkModule.DEMON_BAIT)) {
                    modifier -= 0.30;
                    triggerMessage(PerkModule.DEMON_BAIT, "hold back against demons", 10_000);
                }
            }
            if (npc.isDragon()) {
                if (hasPerk(PerkModule.DRAGON_SLAYER)) {
                    modifier += 0.07;
                    triggerMessage(PerkModule.DRAGON_SLAYER, "smite draconic foes", 10_000);
                }
                if (hasPerk(PerkModule.DRAGON_BAIT)) {
                    modifier -= 0.30;
                    triggerMessage(PerkModule.DRAGON_BAIT, "hold back against dragons", 10_000);
                }
            }
            if (npc.isUndead()) {
                if (hasPerk(PerkModule.UNDEAD_SLAYER)) {
                    modifier += 0.07;
                    triggerMessage(PerkModule.UNDEAD_SLAYER, "purge the undead", 10_000);
                }
                if (hasPerk(PerkModule.UNDEAD_BAIT)) {
                    modifier -= 0.30;
                    triggerMessage(PerkModule.UNDEAD_BAIT, "hold back against the undead", 10_000);
                }
            }
        }

        if (hasPerk(PerkModule.GENOCIDAL) && defender instanceof NPC) {
            Optional<Integer> bonus = getGenocidalBonus();
            if (bonus.isPresent()) {
                modifier += bonus.get() / 100.0;
                triggerMessage(PerkModule.GENOCIDAL, "punish your slayer target", 10_000);
            }
        }

        if (hasPerk(PerkModule.FLANKING) && isFlanking(defender)) {
            modifier += getPerkValue(PerkModule.FLANKING, 0.10);
            triggerMessage(PerkModule.FLANKING, "strike from a blind spot", 8_000);
        }

        if (hasPerk(PerkModule.RUTHLESS) && isRuthlessActive()) {
            modifier += ruthlessStacks * 0.02;
            triggerMessage(PerkModule.RUTHLESS, "build momentum from your kills", 10_000);
        }

        return Math.max(0, (int) Math.floor(baseMaxHit * modifier));
    }

    public int applyMinimumDamage(int rolledDamage, int maxHit) {
        if (!hasPerk(PerkModule.PRECISE)) {
            return rolledDamage;
        }
        double minPercent = getPerkValue(PerkModule.PRECISE, 0.015);
        int floor = (int) Math.floor(maxHit * minPercent);
        if (rolledDamage < floor) {
            triggerMessage(PerkModule.PRECISE, "guarantee a solid strike", 8_000);
            return floor;
        }
        return rolledDamage;
    }

    public int applyIncomingDamage(Entity attacker, int damage) {
        if (damage <= 1) {
            return damage;
        }
        if (hasPerk(PerkModule.LUCKY)) {
            double chance = getPerkValue(PerkModule.LUCKY, 0.005);
            if (Math.random() <= chance) {
                triggerMessage(PerkModule.LUCKY, "shrug off a brutal hit", 6_000);
                playGfx(PerkModule.LUCKY);
                return 1;
            }
        }
        if (hasPerk(PerkModule.BULWARK)) {
            double reduction = getPerkValue(PerkModule.BULWARK, 0.05);
            int reduced = (int) Math.floor(damage * (1.0 - reduction));
            if (reduced < damage) {
                triggerMessage(PerkModule.BULWARK, "brace against incoming damage", 8_000);
                playGfx(PerkModule.BULWARK);
            }
            return Math.max(0, reduced);
        }
        return damage;
    }

    public boolean tryCriticalHit() {
        if (!hasPerk(PerkModule.BITING)) {
            return false;
        }
        double chance = getPerkValue(PerkModule.BITING, 0.02);
        boolean success = Math.random() <= chance;
        if (success) {
            triggerMessage(PerkModule.BITING, "land a devastating critical hit", 6_000);
            playGfx(PerkModule.BITING);
        }
        return success;
    }

    public void onHitDealt(Entity defender, int damage, boolean specialAttackUsed) {
        if (damage <= 0) {
            return;
        }

        if (hasPerk(PerkModule.AFTERSHOCK)) {
            aftershockDamage += damage;
            int threshold = 5000;
            if (aftershockDamage >= threshold && cooldownReady(PerkModule.AFTERSHOCK, 10_000)) {
                aftershockDamage -= threshold;
                int rank = getPerkRank(PerkModule.AFTERSHOCK);
        int bonus = Math.max(1, (int) Math.floor(damage * (0.1 * rank)));
        applyAreaDamage(defender, bonus, 1);
                triggerMessage(PerkModule.AFTERSHOCK, "trigger a seismic blast", 5_000);
                playGfx(PerkModule.AFTERSHOCK);
            }
        }

        if (hasPerk(PerkModule.CRACKLING) && cooldownReady(PerkModule.CRACKLING, 60_000)) {
            int rank = getPerkRank(PerkModule.CRACKLING);
            double multiplier = defender.isPlayer() ? 0.10 : 0.50;
            int bonus = Math.max(1, (int) Math.floor(damage * multiplier * rank));
            defender.appendDamage(player, bonus, Hitmark.HIT);
            triggerMessage(PerkModule.CRACKLING, "release crackling energy", 5_000);
            playGfx(PerkModule.CRACKLING);
        }

        if (hasPerk(PerkModule.CAROMING)) {
            double chance = getPerkValue(PerkModule.CAROMING, 0.03);
            if (Math.random() <= chance) {
                int extra = Math.max(1, (int) Math.floor(damage * 0.5));
                defender.appendDamage(player, extra, Hitmark.HIT);
                triggerMessage(PerkModule.CAROMING, "strike with an extra hit", 4_000);
                playGfx(PerkModule.CAROMING);
            }
        }

        if (hasPerk(PerkModule.SPENDTHRIFT)) {
            double chance = getPerkValue(PerkModule.SPENDTHRIFT, 0.02);
            if (Math.random() <= chance) {
                int extra = Math.max(1, (int) Math.floor(damage * (0.05 * getPerkRank(PerkModule.SPENDTHRIFT))));
                int cost = extra * 50;
                if (player.getItems().playerHasItem(Items.COINS, cost)) {
                    player.getItems().deleteItem(Items.COINS, cost);
                    defender.appendDamage(player, extra, Hitmark.HIT);
                    triggerMessage(PerkModule.SPENDTHRIFT, "convert coins into extra damage", 4_000);
                    playGfx(PerkModule.SPENDTHRIFT);
                }
            }
        }

        if (hasPerk(PerkModule.LUNGING)) {
            double chance = getPerkValue(PerkModule.LUNGING, 0.06);
            if (Math.random() <= chance) {
                int total = Math.max(1, (int) Math.floor(damage * 0.30));
                boolean moving = defender.isPlayer() && System.currentTimeMillis() - defender.asPlayer().lastMove < 2000;
                if (moving) {
                    total = Math.max(1, total / 2);
                }
                applyBleed(defender, total);
                triggerMessage(PerkModule.LUNGING, "tear open a bleeding wound", 6_000);
                playGfx(PerkModule.LUNGING);
            }
        }

        if (hasPerk(PerkModule.RELENTLESS) && specialAttackUsed) {
            double chance = getPerkValue(PerkModule.RELENTLESS, 0.01);
            if (Math.random() <= chance) {
                WrathSystem.addWrath(player, 5);
                triggerMessage(PerkModule.RELENTLESS, "preserve your wrath", 8_000);
                playGfx(PerkModule.RELENTLESS);
            }
        }

        if (hasPerk(PerkModule.TALKING)) {
            if (Misc.random(30) == 0) {
                triggerMessage(PerkModule.TALKING, "hear your gear whisper", 12_000);
                playGfx(PerkModule.TALKING);
            }
        }
    }

    public void onHitTaken(Entity attacker, int damage) {
        if (damage <= 0) {
            return;
        }

        if (hasPerk(PerkModule.CONFUSED)) {
            double chance = getPerkValue(PerkModule.CONFUSED, 0.01);
            if (Math.random() <= chance) {
                teleportRandomly();
                triggerMessage(PerkModule.CONFUSED, "blink away in confusion", 6_000);
                playGfx(PerkModule.CONFUSED);
            }
        }

        if (hasPerk(PerkModule.CRYSTAL_SHIELD)) {
            double chance = getPerkValue(PerkModule.CRYSTAL_SHIELD, 0.10);
            if (Math.random() <= chance) {
                int rank = getPerkRank(PerkModule.CRYSTAL_SHIELD);
                int stored = Math.max(1, (int) Math.floor(damage * 0.05 * rank));
                addTemporaryLifepoints(stored);
                triggerMessage(PerkModule.CRYSTAL_SHIELD, "store damage as crystal lifepoints", 10_000);
                playGfx(PerkModule.CRYSTAL_SHIELD);
            }
        }

        if (hasPerk(PerkModule.DEVOTED)) {
            double chance = getPerkValue(PerkModule.DEVOTED, 0.03);
            if (Math.random() <= chance) {
                restorePrayer(false);
                triggerMessage(PerkModule.DEVOTED, "restore your prayer", 10_000);
                playGfx(PerkModule.DEVOTED);
            }
        }

        if (hasPerk(PerkModule.ENHANCED_DEVOTED)) {
            double chance = getPerkValue(PerkModule.ENHANCED_DEVOTED, 0.045);
            if (Math.random() <= chance) {
                restorePrayer(true);
                triggerMessage(PerkModule.ENHANCED_DEVOTED, "overcharge your prayer", 10_000);
                playGfx(PerkModule.ENHANCED_DEVOTED);
            }
        }
    }

    public void onNpcKill(NPC npc, Location3D location) {
        if (npc == null) {
            return;
        }

        if (hasPerk(PerkModule.RUTHLESS)) {
            int maxStacks = getPerkRank(PerkModule.RUTHLESS);
            ruthlessStacks = Math.min(maxStacks, ruthlessStacks + 1);
            ruthlessExpires = System.currentTimeMillis() + 20_000;
            triggerMessage(PerkModule.RUTHLESS, "grow stronger after a kill", 6_000);
        }

        if (hasPerk(PerkModule.BRASSICAN)) {
            if (Misc.random(10) == 0) {
                Server.itemHandler.createGroundItem(player, Items.CABBAGE, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                triggerMessage(PerkModule.BRASSICAN, "summon a cabbage", 6_000);
                playGfx(PerkModule.BRASSICAN);
            }
        }

        if (hasPerk(PerkModule.LOOTING) && cooldownReady(PerkModule.LOOTING, 300_000)) {
            if (Misc.random(3) == 0) {
                int resource = LOOTING_RESOURCES[Misc.random(LOOTING_RESOURCES.length - 1)];
                Server.itemHandler.createGroundItem(player, resource, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                triggerMessage(PerkModule.LOOTING, "secure an extra resource", 6_000);
                playGfx(PerkModule.LOOTING);
            }
        }

        if (hasPerk(PerkModule.SCAVENGING)) {
            double chance = getPerkValue(PerkModule.SCAVENGING, 0.01);
            if (Math.random() <= chance) {
                int[] pool = Misc.random(10) == 0 ? SCAVENGE_RARE : SCAVENGE_COMMON;
                int component = pool[Misc.random(pool.length - 1)];
                Server.itemHandler.createGroundItem(player, component, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                triggerMessage(PerkModule.SCAVENGING, "salvage rare components", 10_000);
                playGfx(PerkModule.SCAVENGING);
            }
        }
    }

    public void onResourceGather(int itemId, Location3D location) {
        if (!hasPerk(PerkModule.SCAVENGING)) {
            return;
        }
        double chance = getPerkValue(PerkModule.SCAVENGING, 0.01);
        if (Math.random() <= chance) {
            if (player.getItems().freeSlots() > 0) {
                player.getItems().addItem(itemId, 1);
            } else {
                Server.itemHandler.createGroundItem(player, itemId, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
            }
            triggerMessage(PerkModule.SCAVENGING, "salvage extra resources", 10_000);
            playGfx(PerkModule.SCAVENGING);
        }
    }

    public int modifySkillExperience(int baseAmount) {
        double modifier = 1.0;
        if (hasPerk(PerkModule.WISE)) {
            modifier += getPerkValue(PerkModule.WISE, 0.01);
            triggerMessage(PerkModule.WISE, "gain extra experience", 10_000);
        }
        if (hasPerk(PerkModule.ENLIGHTENED)) {
            modifier += getPerkValue(PerkModule.ENLIGHTENED, 0.03);
            triggerMessage(PerkModule.ENLIGHTENED, "absorb deeper knowledge", 10_000);
        }
        return (int) Math.floor(baseAmount * modifier);
    }

    public int modifyFoodHealing(int baseHeal) {
        if (!hasPerk(PerkModule.JUNK_FOOD)) {
            return baseHeal;
        }
        double penalty = getPerkValue(PerkModule.JUNK_FOOD, 0.03);
        int heal = (int) Math.floor(baseHeal * (1.0 - penalty));
        triggerMessage(PerkModule.JUNK_FOOD, "feel the food heal less", 10_000);
        return Math.max(0, heal);
    }

    public void onMove() {
        if (hasPerk(PerkModule.GLOW_WORM) && player.getRunEnergy() < 100) {
            if (Misc.random(6) == 0) {
                player.setRunEnergy(player.getRunEnergy() + 1, true);
                triggerMessage(PerkModule.GLOW_WORM, "light your path", 12_000);
                playGfx(PerkModule.GLOW_WORM);
            }
        }
    }

    private void handleCommitted() {
        if (hasPerk(PerkModule.COMMITTED)) {
            if (!player.getAttributes().getBoolean(ATTR_COMMITTED)) {
                player.getAttributes().setBoolean(ATTR_COMMITTED, true);
                player.isSkulled = true;
                player.skullTimer = Configuration.SKULL_TIMER;
                player.headIconPk = 0;
                player.getPA().requestUpdates();
                triggerMessage(PerkModule.COMMITTED, "embrace a permanent skull", 10_000);
            }
        } else if (player.getAttributes().getBoolean(ATTR_COMMITTED)) {
            player.getAttributes().removeBoolean(ATTR_COMMITTED);
        }
    }

    private Optional<Integer> getGenocidalBonus() {
        if (!player.getSlayer().getTask().isPresent()) {
            return Optional.empty();
        }
        int assigned = player.getSlayer().getTaskAmountAssigned();
        if (assigned <= 0) {
            return Optional.empty();
        }
        int remaining = player.getSlayer().getTaskAmount();
        double progress = Math.min(1.0, Math.max(0.0, (assigned - remaining) / (double) assigned));
        int bonus = (int) Math.floor(progress * 5.0);
        if (bonus <= 0) {
            return Optional.empty();
        }
        return Optional.of(bonus);
    }

    private boolean isFlanking(Entity defender) {
        if (defender == null) {
            return false;
        }
        if (defender.isPlayer()) {
            int facing = defender.asPlayer().face;
            return facing != player.getIndex() + 32768;
        }
        if (defender.isNPC()) {
            NPC npc = defender.asNPC();
            return npc.getFaceIndex() != player.getIndex() + 32768;
        }
        return false;
    }

    private void teleportRandomly() {
        int offsetX = Misc.random(-2, 2);
        int offsetY = Misc.random(-2, 2);
        Position target = new Position(player.getX() + offsetX, player.getY() + offsetY, player.getHeight());
        player.getPA().movePlayer(target.getX(), target.getY(), target.getHeight());
    }

    private void addTemporaryLifepoints(int amount) {
        int current = player.getAttributes().getInt(ATTR_CRYSTAL_TEMP, 0);
        int updated = current + amount;
        player.getAttributes().setInt(ATTR_CRYSTAL_TEMP, updated);
        player.getHealth().increase(amount, player.getHealth().getMaximumHealth() + updated);
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int now = player.getAttributes().getInt(ATTR_CRYSTAL_TEMP, 0);
                int remaining = Math.max(0, now - amount);
                player.getAttributes().setInt(ATTR_CRYSTAL_TEMP, remaining);
                if (player.getHealth().getCurrentHealth() > player.getHealth().getMaximumHealth() + remaining) {
                    player.getHealth().setCurrentHealth(player.getHealth().getMaximumHealth() + remaining);
                }
                container.stop();
            }
        }, 10);
    }

    private void restorePrayer(boolean overcharge) {
        int prayerLevel = player.getPA().getLevelForXP(player.playerXP[Skill.PRAYER.getId()]);
        int target = prayerLevel;
        if (overcharge) {
            target = (int) Math.floor(prayerLevel * 1.5);
        }
        player.playerLevel[Skill.PRAYER.getId()] = target;
        player.getPA().refreshSkill(Skill.PRAYER.getId());
    }

    private void applyBleed(Entity defender, int totalDamage) {
        int ticks = 3;
        int perTick = Math.max(1, totalDamage / ticks);
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int remaining = ticks;

            @Override
            public void execute(CycleEventContainer container) {
                defender.appendDamage(player, perTick, Hitmark.HIT);
                remaining--;
                if (remaining <= 0) {
                    container.stop();
                }
            }
        }, 2);
    }

    private void applyAreaDamage(Entity defender, int damage, int radius) {
        if (defender == null) {
            return;
        }
        if (defender.isNPC()) {
            for (NPC npc : NPCHandler.npcs) {
                if (npc == null || npc.isDead || npc.getHealth().getCurrentHealth() <= 0) {
                    continue;
                }
                if (npc.getPosition().getManhattanDistance(defender.getPosition()) <= radius) {
                    npc.appendDamage(player, damage, Hitmark.HIT);
                }
            }
        } else if (defender.isPlayer()) {
            for (Player other : PlayerHandler.players) {
                if (other == null || other.isDead || other.getHealth().getCurrentHealth() <= 0) {
                    continue;
                }
                if (other.getPosition().getManhattanDistance(defender.getPosition()) <= radius) {
                    other.appendDamage(player, damage, Hitmark.HIT);
                }
            }
        }
    }

    private boolean isRuthlessActive() {
        if (ruthlessStacks <= 0) {
            return false;
        }
        if (System.currentTimeMillis() > ruthlessExpires) {
            ruthlessStacks = 0;
            return false;
        }
        return true;
    }

    private boolean cooldownReady(int perkId, long cooldownMs) {
        long now = System.currentTimeMillis();
        long next = cooldowns.getOrDefault(perkId, 0L);
        if (now < next) {
            return false;
        }
        cooldowns.put(perkId, now + cooldownMs);
        return true;
    }

    private void triggerMessage(int perkId, String effect, long cooldownMs) {
        if (!cooldownReady(perkId + 10_000, cooldownMs)) {
            return;
        }
        PerkModule.PerkDefinition definition = PerkModule.forId(perkId);
        if (definition != null) {
            player.sendMessage("Your " + definition.getName() + " allows you to " + effect + ".");
        }
    }

    private void playGfx(int perkId) {
        PerkModule.PerkDefinition definition = PerkModule.forId(perkId);
        if (definition != null && definition.getGfxId() > 0) {
            player.startGraphic(new Graphic(definition.getGfxId(), Graphic.GraphicHeight.MIDDLE));
        }
    }
}
