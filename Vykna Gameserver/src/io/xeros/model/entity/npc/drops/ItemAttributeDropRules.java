package io.xeros.model.entity.npc.drops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAttributes;
import io.xeros.util.Misc;

/**
 * Rules engine for applying ItemAttributes to NPC drops.
 * Configure eligible items and rarity/perk pools per item (or via a default rule).
 */
public final class ItemAttributeDropRules {

	private static final ItemAttributeDropRules INSTANCE = new ItemAttributeDropRules();

	public static ItemAttributeDropRules getInstance() {
		return INSTANCE;
	}

	private final Set<Integer> eligibleItems = new HashSet<>();
	private final Set<Integer> blockedItems = new HashSet<>();
	private final Map<Integer, Rule> itemRules = new HashMap<>();
	private final Rule defaultRule = new Rule();

	private ItemAttributeDropRules() {
	}

	public Rule defaultRule() {
		return defaultRule;
	}

	/**
	 * Example usage:
	 * <pre>
	 * ItemAttributeDropRules rules = ItemAttributeDropRules.getInstance();
	 * rules.allowItems(Items.ABYSSAL_WHIP, Items.DRAGON_SCIMITAR);
	 * rules.ruleForItem(Items.ABYSSAL_WHIP)
	 *     .addRarityChance(1, 0.20) // uncommon 20%
	 *     .addRarityChance(2, 0.05) // rare 5%
	 *     .addPerkPool(0.10, 1, 3, 100, 101, 102); // 10% to roll a perk in this pool
	 * rules.defaultRule().setEnabled(false); // disable elsewhere if desired
	 * </pre>
	 */
	public void exampleSetup() {
		// Intentionally left empty: copy the snippet above into your startup config.
	}

	public Rule ruleForItem(int itemId) {
		return itemRules.computeIfAbsent(itemId, id -> new Rule());
	}

	public void allowItems(int... itemIds) {
		for (int id : itemIds) {
			eligibleItems.add(id);
		}
	}

	public void blockItems(int... itemIds) {
		for (int id : itemIds) {
			blockedItems.add(id);
		}
	}

	public GameItem applyAttributes(GameItem item) {
		if (item == null) return null;
		GameItem copy = item.copy();
		Optional<ItemAttributes> attrs = rollForDrop(item.getId());
		attrs.ifPresent(copy::setAttrs);
		return copy;
	}

	public GameItem applyAttributes(int npcId, GameItem item) {
		return applyAttributes(item);
	}

	public Optional<ItemAttributes> rollForDrop(int itemId) {
		if (!eligibleItems.contains(itemId) || blockedItems.contains(itemId)) {
			return Optional.empty();
		}

		Rule rule = itemRules.getOrDefault(itemId, defaultRule);
		if (!rule.enabled) {
			return Optional.empty();
		}

		ItemAttributes attrs = new ItemAttributes();
		boolean rolled = false;

		int rarity = rule.rollRarity();
		if (rarity > 0) {
			attrs.rarityId = (byte) rarity;
			rolled = true;
		}

		List<PerkSelection> perks = rule.rollPerks(rarity);
		if (!perks.isEmpty()) {
			PerkSelection first = perks.get(0);
			attrs.perk1 = (short) first.perkId;
			attrs.perk1Rank = (byte) first.rank;
			if (perks.size() > 1) {
				PerkSelection second = perks.get(1);
				attrs.perk2 = (short) second.perkId;
				attrs.perk2Rank = (byte) second.rank;
			}
			rolled = true;
		}

		if (!rolled && rule.forceAttributes) {
			int fallback = rule.forceRarityId > 0 ? rule.forceRarityId : rule.firstConfiguredRarity();
			if (fallback > 0) {
				attrs.rarityId = (byte) fallback;
				rolled = true;
			}
		}

		return rolled ? Optional.of(attrs) : Optional.empty();
	}

	public static final class Rule {
		private boolean enabled = true;
		private boolean forceAttributes = false;
		private int forceRarityId = 0;
		private boolean useRarityPerkPool = false;
		private int maxPerks = 2;
		private final List<RarityChance> rarityChances = new ArrayList<>();
		private final List<PerkPool> perkPools = new ArrayList<>();

		public Rule setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Rule forceAttributes(int fallbackRarityId) {
			this.forceAttributes = true;
			this.forceRarityId = fallbackRarityId;
			return this;
		}

		public Rule setMaxPerks(int maxPerks) {
			this.maxPerks = Math.max(0, maxPerks);
			return this;
		}

		public Rule useRarityPerkPool(boolean useRarityPerkPool) {
			this.useRarityPerkPool = useRarityPerkPool;
			return this;
		}

		public Rule addRarityChance(int rarityId, double chance) {
			this.rarityChances.add(new RarityChance(rarityId, chance));
			return this;
		}

		public Rule addPerkPool(double chance, int minRank, int maxRank, int... perkIds) {
			this.perkPools.add(new PerkPool(chance, minRank, maxRank, perkIds));
			return this;
		}

		private int rollRarity() {
			for (RarityChance chance : rarityChances) {
				if (roll(chance.chance)) {
					return chance.rarityId;
				}
			}
			return 0;
		}

		private List<PerkSelection> rollPerks(int rarityId) {
			if (useRarityPerkPool) {
				List<PerkSelection> selections = new ArrayList<>();
				List<io.xeros.model.items.RarityPerkPool.PerkRoll> rolls =
						io.xeros.model.items.RarityPerkPool.rollPerks(rarityId, maxPerks);
				for (io.xeros.model.items.RarityPerkPool.PerkRoll roll : rolls) {
					selections.add(new PerkSelection(roll.perkId, roll.rank));
				}
				return selections;
			}
			List<PerkSelection> selections = new ArrayList<>();
			for (PerkPool pool : perkPools) {
				if (selections.size() >= maxPerks) {
					break;
				}
				if (!roll(pool.chance)) {
					continue;
				}
				int perkId = pool.randomPerkId();
				if (perkId <= 0 || selections.stream().anyMatch(s -> s.perkId == perkId)) {
					continue;
				}
				int rank = pool.randomRank();
				selections.add(new PerkSelection(perkId, rank));
			}
			return selections;
		}

		private int firstConfiguredRarity() {
			return rarityChances.isEmpty() ? 0 : rarityChances.get(0).rarityId;
		}
	}

	private static final class RarityChance {
		private final int rarityId;
		private final double chance;

		private RarityChance(int rarityId, double chance) {
			this.rarityId = rarityId;
			this.chance = chance;
		}
	}

	private static final class PerkPool {
		private final double chance;
		private final int minRank;
		private final int maxRank;
		private final int[] perkIds;

		private PerkPool(double chance, int minRank, int maxRank, int[] perkIds) {
			this.chance = chance;
			this.minRank = Math.max(1, minRank);
			this.maxRank = Math.max(this.minRank, maxRank);
			this.perkIds = perkIds == null ? new int[0] : perkIds;
		}

		private int randomPerkId() {
			if (perkIds.length == 0) return 0;
			int index = Misc.random(perkIds.length - 1);
			return perkIds[index];
		}

		private int randomRank() {
			if (minRank == maxRank) return minRank;
			return minRank + Misc.random(maxRank - minRank);
		}
	}

	private static final class PerkSelection {
		private final int perkId;
		private final int rank;

		private PerkSelection(int perkId, int rank) {
			this.perkId = perkId;
			this.rank = rank;
		}
	}

	private static boolean roll(double chance) {
		if (chance <= 0) return false;
		if (chance >= 1) return true;
		return Math.random() < chance;
	}
}
