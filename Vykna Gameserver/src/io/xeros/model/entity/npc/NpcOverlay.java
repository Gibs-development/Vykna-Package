package io.xeros.model.entity.npc;

import io.xeros.Configuration;
import io.xeros.model.definitions.NpcDefinitionData;
import io.xeros.model.entity.HealthStatus;
import io.xeros.util.Misc;

public final class NpcOverlay {
	public static final int PACKET_OPCODE = 245;
	public static final int STATUS_SNARE = 1 << 0;
	public static final int STATUS_FREEZE = 1 << 1;
	public static final int STATUS_POISON = 1 << 2;
	public static final int STATUS_VENOM = 1 << 3;
	public static final int STATUS_SALVE = 1 << 4;
	public static final int STATUS_DEMON_UNDEAD = 1 << 5;

	public static final int WEAKNESS_MELEE = 0;
	public static final int WEAKNESS_RANGED = 1;
	public static final int WEAKNESS_AIR = 2;
	public static final int WEAKNESS_WATER = 3;
	public static final int WEAKNESS_EARTH = 4;
	public static final int WEAKNESS_FIRE = 5;

	private NpcOverlay() {
	}

	public static int hpPercent(NPC npc) {
		int max = npc.getHealth().getMaximumHealth();
		if (max <= 0) {
			return 0;
		}
		int current = npc.getHealth().getCurrentHealth();
		int percent = (current * 100) / max;
		if (percent < 0) {
			return 0;
		}
		if (percent > 100) {
			return 100;
		}
		return percent;
	}

	public static int weaknessId(NPC npc) {
		NpcDefinitionData definition = NpcDefinitionData.forId(npc.getNpcId());
		NpcDefinitionData.Weakness weakness = NpcDefinitionData.Weakness.MELEE;
		if (definition != null && definition.getMetadata() != null && definition.getMetadata().getWeakness() != null) {
			weakness = definition.getMetadata().getWeakness();
		}
		switch (weakness) {
			case RANGED:
				return WEAKNESS_RANGED;
			case AIR_MAGIC:
				return WEAKNESS_AIR;
			case WATER_MAGIC:
				return WEAKNESS_WATER;
			case EARTH_MAGIC:
				return WEAKNESS_EARTH;
			case FIRE_MAGIC:
				return WEAKNESS_FIRE;
			case MELEE:
			default:
				return WEAKNESS_MELEE;
		}
	}

	public static int statusMask(NPC npc) {
		int mask = 0;
		int freezeTimer = npc.freezeTimer;
		if (freezeTimer > 0) {
			if (freezeTimer <= 4) {
				mask |= STATUS_SNARE;
			} else {
				mask |= STATUS_FREEZE;
			}
		}

		HealthStatus status = npc.getHealth().getStatus();
		if (status == HealthStatus.POISON) {
			mask |= STATUS_POISON;
		} else if (status == HealthStatus.VENOM) {
			mask |= STATUS_VENOM;
		}

		boolean undead = isUndead(npc);
		boolean demon = isDemon(npc);
		if (undead) {
			mask |= STATUS_SALVE;
		}
		if (undead || demon) {
			mask |= STATUS_DEMON_UNDEAD;
		}

		return mask;
	}

	private static boolean isUndead(NPC npc) {
		NpcDefinitionData definition = NpcDefinitionData.forId(npc.getNpcId());
		if (definition != null && definition.getMetadata() != null) {
			if (definition.getMetadata().getType() == NpcDefinitionData.NpcType.UNDEAD) {
				return true;
			}
		}
		return Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) >= 0;
	}

	private static boolean isDemon(NPC npc) {
		NpcDefinitionData definition = NpcDefinitionData.forId(npc.getNpcId());
		if (definition != null && definition.getMetadata() != null) {
			if (definition.getMetadata().getType() == NpcDefinitionData.NpcType.DEMON) {
				return true;
			}
		}
		return Misc.linearSearch(Configuration.DEMON_IDS, npc.getNpcId()) >= 0;
	}
}
