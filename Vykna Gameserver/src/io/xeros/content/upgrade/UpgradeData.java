package io.xeros.content.upgrade;


import io.xeros.model.items.GameItem;


/**
 * Author C.T for RuneRogue
 *
 */

public enum UpgradeData {

	/*
	 * Weapons
	 */
	ABYSSAL_TENTACLE(1, 113093, UpgradeType.WEAPON, 12006, 100, false, 0, -1, new GameItem(4151, 1), new GameItem(12004, 1)),
	BLESSED_SARADOMIN_SWORD(1, 113094, UpgradeType.WEAPON, 12809, 100, false, 0, -1, new GameItem(11838, 1), new GameItem(12804, 1)),
	GRANITE_MAUL(1, 113095, UpgradeType.WEAPON, 12848, 100, false, 0, -1, new GameItem(4153, 1), new GameItem(12849, 1)),
	FROZEN_ABYSSAL_WHIP(1, 113096, UpgradeType.WEAPON, 12774, 100, false, 0, -1, new GameItem(4151, 1), new GameItem(12769, 1)),
	LUCKY_ARMADYL_GODSWORD(1, 113097, UpgradeType.WEAPON, 20368, 50, false, 0, -1, new GameItem(11802, 1), new GameItem(11830, 1), new GameItem(11828, 1), new GameItem(11826, 1)),
	LUCKY_BANDOS_GODSWORD(1, 113098, UpgradeType.WEAPON, 20370, 50, false, 0, -1, new GameItem(11804, 1), new GameItem(11832, 1), new GameItem(11834, 1), new GameItem(11836, 1)),
	LUCKY_SARADOMIN_GODSWORD(1, 113099, UpgradeType.WEAPON, 20372, 50, false, 0, -1, new GameItem(11806, 1), new GameItem(11785, 1), new GameItem(24111, 1)),
	LUCKY_ZAMORAK_GODSWORD(1, 113100, UpgradeType.WEAPON, 20374, 50, false, 0, -1, new GameItem(11808, 1), new GameItem(24158, 1)),
	VOLCANIC_ABYSSAL_WHIP(1, 113101, UpgradeType.WEAPON, 12773, 100, false, 0, -1, new GameItem(4151, 1), new GameItem(12771, 1)),
	VORKATH_BLOWPIPE(1, 113102, UpgradeType.WEAPON, 24107, 33, false, 0, 12924, new GameItem(12924, 1), new GameItem(2425, 1), new GameItem(12934, 20000)),
	ZILYANAS_GODBOW(1, 113103, UpgradeType.WEAPON, 24111, 20, false, 0, -1, new GameItem(995, 15000000), new GameItem(11048, 1)),
	TWISTED_CROSSBOW(1, 113104, UpgradeType.WEAPON, 23145, 33, false, 0, 20997, new GameItem(20997, 1), new GameItem(22386, 1), new GameItem(995, 150000000)),
	ABYSSAL_TENTICLE_OR(1, 113105, UpgradeType.WEAPON, 26484, 3, false, 0, -1, new GameItem(12006, 1), new GameItem(12004, 1), new GameItem(2425, 1), new GameItem(995, 11000000)),
	BOW_OF_FAERDHINEN_C(1, 113106, UpgradeType.WEAPON, 25867, 35, false, 0, 20999, new GameItem(23832, 1), new GameItem(20999, 1), new GameItem(995, 5000000)),



	/*
	 * Armour
	 */
	AVAS_ASSEMBLER(1, 113093, UpgradeType.ARMOR, 22109, 100, false, 1, -1, new GameItem(10499, 1), new GameItem(2425)),
	MALEDICTION_WARD(1, 113094, UpgradeType.ARMOR, 12806, 100, false, 1, -1, new GameItem(11924, 1), new GameItem(995, 5000000)),
	ODIUM_WARD(1, 113095, UpgradeType.ARMOR, 12807, 100, false, 1, -1, new GameItem(11926, 1), new GameItem(995, 6000000)),
	INFERNAL_CAPE(1, 113096, UpgradeType.ARMOR, 22319, 5, false, 1, -1, new GameItem(21295, 1)),

	/*
	 * Tools
	 */

	INFERNAL_AXE(1, 113093, UpgradeType.TOOL, 13241, 100, false, 0, -1, new GameItem(6739, 1), new GameItem(13233, 1)),
	INFERNAL_PICKAXE(1, 113094, UpgradeType.TOOL, 13243, 100, false, 0, -1, new GameItem(11920, 1), new GameItem(13233, 1)),
	SUPERIOR_CRYSTAL_KEY(1, 113095, UpgradeType.TOOL, 23951, 55, false, 0, -1, new GameItem(990, 10)),

	SEERS_RING(1, 113096, UpgradeType.TOOL, 11770, 50, false, 0, -1, new GameItem(6731, 1)),
	ARCHERS_RING(1, 113097, UpgradeType.TOOL, 11771, 50, false, 0, -1, new GameItem(6733, 1)),
	WARRIORS_RING(1, 113098, UpgradeType.TOOL, 11772, 50, false, 0, -1, new GameItem(6735, 1)),
	BERSERKER_RING(1, 113099, UpgradeType.TOOL, 11773, 50, false, 0, -1, new GameItem(6737, 1)),
	ELDER_MULTI_AXE(1, 113100, UpgradeType.TOOL, 23582, 20, false, 0, -1, new GameItem(11920, 1), new GameItem(6739, 1)),

	SLAYER_KEY_TIER_1(1, 113101, UpgradeType.TOOL, 21055, 85, false, 0, -1, new GameItem(13248, 5)),

	SLAYER_KEY_TIER_2(1, 113102, UpgradeType.TOOL, 21054, 75, false, 0, -1, new GameItem(21055, 5)),

	SLAYER_KEY_TIER_3(1, 113103, UpgradeType.TOOL, 21053, 55, false, 0, -1, new GameItem(21054, 5)),
	;

	private final UpgradeType type;

	public final int buttonId;

	public final int clickId;

	private int resultItem;

	private float successRate;

	private boolean otherCurrency;

	private int currencyAmount;

	private GameItem[] ingredients;

	private int safeItem;
	
	
	
	UpgradeData(int buttonId, int clickId, UpgradeType type, int resultItem, float successRate, boolean otherCurrency, int currencyAmount, int safeItem, GameItem... ingredients) {
		this.buttonId = buttonId;
		this.clickId = clickId;
		this.type = type;
		this.resultItem = resultItem;
		this.successRate = successRate;
		this.otherCurrency = otherCurrency;
		this.currencyAmount = currencyAmount;
		this.ingredients = ingredients;
		this.safeItem = safeItem;
	}

	
	public int getButtonId() {
		return buttonId;
	}
	
	public int getClickId() {
		return clickId;
	}
	
	public UpgradeType getType() {
		return type;
	}
	
	public int getResultItem() {
		return resultItem;
	}
	
	public float getSuccessRate() {
		return successRate;
	}
	
	public boolean getOtherCurrency() {
		return otherCurrency;
	}
	
	public int getCurrencyAmount() {
		return currencyAmount;
	}
	
	public GameItem[] getIngredients() {
		return ingredients;
	}
	
	public int getSafeItem() {
		return safeItem;
	}
	

}