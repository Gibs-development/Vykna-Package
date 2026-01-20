package io.xeros.content.achievement.vykna;

public enum VyknaAchievement implements AchievementDefinition {
	CHOP_NORMAL_LOGS(1001, "CHOP_NORMAL_LOGS", 5, AchievementListType.TASKS, TaskSubcategory.LUMBRIDGE,
			"Lumbridge: Chop normal logs", "Chop logs from any tree in Lumbridge.", 50),
	CATCH_SHRIMP(1002, "CATCH_SHRIMP", 5, AchievementListType.TASKS, TaskSubcategory.LUMBRIDGE,
			"Lumbridge: Catch shrimp", "Net shrimp in the river south of Lumbridge.", 25),
	BUY_RUNE(1101, "BUY_RUNE", 10, AchievementListType.TASKS, TaskSubcategory.VARROCK,
			"Varrock: Buy a rune", "Purchase a rune from Aubury in the rune shop.", 5),
	USE_GRAND_EXCHANGE(1102, "USE_GRAND_EXCHANGE", 15, AchievementListType.TASKS, TaskSubcategory.VARROCK,
			"Varrock: Use the GE", "Place a buy offer on the Grand Exchange.", 1),
	CHOP_OAK_LOGS(2001, "CHOP_OAK_LOGS", 10, AchievementListType.SKILLING, SkillingSubcategory.WOODCUTTING,
			"Woodcutting: Chop oak logs", "Chop oak logs anywhere.", 50),
	CATCH_LOBSTER(2002, "CATCH_LOBSTER", 10, AchievementListType.SKILLING, SkillingSubcategory.FISHING,
			"Fishing: Catch lobster", "Catch lobster at any lobster spot.", 50),
	MINE_IRON_ORE(2101, "MINE_IRON_ORE", 10, AchievementListType.SKILLING, SkillingSubcategory.MINING,
			"Mining: Mine iron", "Mine iron ore in any mine.", 100),
	COMPLETE_SLAYER_TASKS(3001, "COMPLETE_SLAYER_TASKS", 15, AchievementListType.COMBAT, CombatSubcategory.SLAYER,
			"Slayer: Complete tasks", "Complete Slayer tasks from any master.", 10),
	KILL_GOBLINS(3002, "KILL_GOBLINS", 5, AchievementListType.COMBAT, CombatSubcategory.SLAYER,
			"Slayer: Kill goblins", "Kill goblins anywhere in Gielinor.", 25),
	KILL_GIANT_MOLE(3101, "KILL_GIANT_MOLE", 25, AchievementListType.COMBAT, CombatSubcategory.BOSSES,
			"Bossing: Giant Mole", "Defeat the Giant Mole.", 1),
	KILL_GENERAL_GRAARDOR(3102, "KILL_GENERAL_GRAARDOR", 25, AchievementListType.COMBAT, CombatSubcategory.BOSSES,
			"Bossing: General Graardor", "Defeat General Graardor.", 1),
	DEFEAT_CHAOS_ELEMENTAL(3201, "DEFEAT_CHAOS_ELEMENTAL", 20, AchievementListType.COMBAT, CombatSubcategory.WILDERNESS,
			"Wilderness: Chaos Elemental", "Defeat the Chaos Elemental.", 1);

	private final int uid;
	private final String tag;
	private final int points;
	private final AchievementListType listType;
	private final AchievementSubcategory subcategory;
	private final String name;
	private final String description;
	private final int target;

	VyknaAchievement(int uid, String tag, int points, AchievementListType listType, AchievementSubcategory subcategory,
			String name, String description, int target) {
		this.uid = uid;
		this.tag = tag;
		this.points = points;
		this.listType = listType;
		this.subcategory = subcategory;
		this.name = name;
		this.description = description;
		this.target = target;
	}

	@Override
	public int getUid() {
		return uid;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public AchievementListType getListType() {
		return listType;
	}

	@Override
	public AchievementSubcategory getSubcategory() {
		return subcategory;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getTarget() {
		return target;
	}
}
