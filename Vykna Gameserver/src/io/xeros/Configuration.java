package io.xeros;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import io.xeros.content.evil_tree.EvilTree;
import io.xeros.content.koranian_event.KoranianEventBossHandler;
import io.xeros.content.revenant_event.RevenantEventBossHandler;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.content.world_boss_events.EventBossHandler;
import io.xeros.content.world_event.WorldEvent;
import io.xeros.content.world_event_galvek.GalvekEventBossHandler;
import io.xeros.content.world_event_solak.SolakEventBossHandler;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

public class Configuration {


	public static final ServerState SERVER_STATE = ServerState.TEST;


    /**
	 * True, to show in-depth debugging information.
	 */
	public static boolean DEBUG_MODE = false;

	/**
	 * Determines whether or not basic information should be benchmarked or not.
	 */
	public static boolean BENCHMARK = false;

	public static final int CLIENT_VERSION = 230;//230
	public static String SERVER_NAME = "RuneRogue";
	public static final int PORT_DEFAULT = 43594;//52777
	public static final int PORT_TEST = 43595;

	public static final String PACKAGE = "io.xeros";
	public static final String DATA_FOLDER = "etc";

	public static final String EVERYTHINGRS_KEY_DONATIONS = "FsTYmJhtKYqM0mcmIllxFQZHav4cIvNLk6KH7JFGNMccF4lA92X1OI78QnTtoOUk1XYMuHyG";


	public static final String EVERYTHINGRS_KEY = "M4BPiEwv5J4X5lu8VrGEgLfMoYDcHSXXcYRpCOiFEjf8mgCGFZuMkx2ecNEhMsG0pR2klx59";// Only for the Vote/Highscore system

	public static final String WEBSITE = "https://www.runerogue.co.uk/";
	public static final String FORUM_TOPIC_URL = "https://discord.gg/XwAzYXrW3t";
	public static final String VOTE_LINK = "https://runerogue.co.uk/vote/";
	public static final String HISCORES_LINK = "https://runerogue.everythingrs.com/services/hiscores/";
	public static final String STORE_LINK = "https://runerogue.co.uk/store/";
	public static final String DISCORD_INVITE = "https://discord.gg/XwAzYXrW3t";
	public static final String RULES_LINK = "https://discord.gg/XwAzYXrW3t";
	public static final String GUIDES_LINK = "https://discord.gg/XwAzYXrW3t";
	public static final String DONATOR_BENEFITS_LINK = "https://discord.gg/XwAzYXrW3t";
	public static final String PRICE_GUIDE = "https://discord.gg/XwAzYXrW3t";

	public static final LocalDate XEROS_V1_LAUNCH_DATE = LocalDate.of(2022, Month.AUGUST, 29);
	public static final LocalDate XEROS_V2_LAUNCH_DATE = LocalDate.of(2022, Month.AUGUST, 29);
	public static final LocalDate RECLAIM_DONATIONS_START_DATE = LocalDate.of(2021, Month.JULY, 12);

	/**
	 * Hours between backup of player files for current {@link ServerState}.
	 */
	public static long PLAYER_SAVE_TIMER_MILLIS = TimeUnit.MINUTES.toMillis(15);
	public static int PLAYER_SAVE_BACKUP_EVERY_X_SAVE_TICKS = 2;

	/**
	 * Amount of days to keep a player backup file.
	 */
	public static int PLAYER_SAVE_BACKUPS_DELETE_AFTER_DAYS = 7;
	public static int PLAYER_SAVE_BACKUPS_EXTERNAL_DELETE_AFTER_DAYS = 1;

	public static final boolean LOWERCASE_CAPTCHA = false;

	public static boolean DISABLE_DISCORD_MESSAGING = false;

	public static boolean DISABLE_CONNECTION_REQUEST_LIMIT = false;
	public static boolean DISABLE_CAPTCHA_EVERY_LOGIN = true;
	public static boolean DISABLE_CHANGE_ADDRESS_CAPTCHA = true;
	public static boolean DISABLE_NEW_ACCOUNT_CAPTCHA = true;
	public static boolean DISABLE_REGISTRATION = false;
	public static boolean DISABLE_ADDRESS_VERIFICATION = true;
	public static boolean DISABLE_FRESH_LOGIN = false;
	public static boolean DISABLE_NEW_MAC = false;

	public static boolean DISABLE_PRESETS = false;
	public static boolean DISABLE_FOE = false;
	public static boolean DISABLE_SHOP_SELL = false;
	public static boolean DISABLE_SHOP_BUY = false;
	public static boolean DISABLE_DISPLAY_NAMES = false;
	public static boolean DISABLE_LOGIN_THROTTLE = false;
	public static boolean DISABLE_CC_MESSAGE = false;
	public static boolean DISABLE_FLOWER_POKER = false;
	public static boolean DISABLE_HC_LOSS_ON_DEATH = false;


	// Logging
	public static boolean DISABLE_PACKET_LOG = true;


	/**
	 * Time to restrict new players from using trade, duel, trading post.
	 */
	public static final long NEW_PLAYER_RESTRICT_TIME_MIN = 15;
	public static final long NEW_PLAYER_RESTRICT_TIME_TICKS = Misc.toCycles(NEW_PLAYER_RESTRICT_TIME_MIN, TimeUnit.MINUTES);


	/**
	 * Cycle time.
	 */
	public static final int CYCLE_TIME = 600;

	public static int PLAYERMODIFIER;

	/**
	 * The max amount of packets that will be processed per cycle per player, everything else is discarded.
	 */
	public static final int MAX_PACKETS_PROCESSED_PER_CYCLE = 100;
	public static final int KICK_PLAYER_AFTER_PACKETS_PER_CYCLE = 150;
	
	/**
	 * Controls where characters will be saved                                          
	 */
	private static final LocalDateTime LAUNCH_TIME = LocalDateTime.now();
	public static String USER_FOLDER = System.getProperty("user.home");
	public static final String SAVE_DIRECTORY = "./save_files/";

	public static final String LOG_DIRECTORY = "./logs/";
	public static final String ERROR_LOG_DIRECTORY = LOG_DIRECTORY + "error_logs/";
	public static String ERROR_LOG_FILE = ("error_log_" + Misc.createFileNameSmallDate(LAUNCH_TIME) + ".txt");


	public static final String CONSOLE_LOG_DIRECTORY = LOG_DIRECTORY + "console_logs/";
	public static String CONSOLE_FILE = ("console_log_" + Misc.createFileNameSmallDate(LAUNCH_TIME) + ".txt");

	/**
	 * Combat applications
	 */
	public static boolean BOUNTY_HUNTER_ACTIVE = true;
	public static boolean NEW_DUEL_ARENA_ACTIVE = true;
	public static boolean VOTE_PANEL_ACTIVE = true;

	/**
	 * The highest amount ID. Change is not needed here unless loading items higher
	 * than the 667 revision.
	 */
	public static final int ITEM_LIMIT = 60_000;

	/**
	 * The size of a players bank.
	 */
	public static final int BANK_CAPACITY = 1_000;

	/**
	 * The max amount of players until your server is full.
	 */
	public static final int MAX_PLAYERS = 1100;

	/**
	 * The maximum number of npcs allowed in the local player's list.
	 */
	public static final int MAX_NPCS_IN_LOCAL_LIST = 150;
	public static final int MAX_PLAYERS_IN_LOCAL_LIST = 140;

	/**
	 * How many accounts are allowed to be online at once from the same computer.
	 */
	public static final int SAME_COMPUTER_CONNECTIONS_ALLOWED = 2;

	public static final int IN_COMBAT_TIMER = 10_000;

	/**
	 * Stand/idle animation while in combat-state.
	 * Set this to the animation id you want to use for “combat active” idle.
	 * If you set it to -1, the server won’t override stand animation.
	 */
	public static final int COMBAT_ACTIVE_STAND_ANIMATION = 9817; // <-- change to your desired anim id

	/**
	 * dragon ids for the dragon hunter crossbow
	 */
	public static final int[] DRAG_IDS = { 137, 139, 239, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252,
	            253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273,
	            274, 275, 1871, 1872, 2641, 2642, 2918, 2919, 4000, 4385, 5194, 5872, 5873, 5878, 5879, 5880, 5881, 5882,
	            6500, 6501, 6502, 6593, 7039, 7253, 7254, 7255, 7273, 7274, 7275, 8028, 465, 8030, 8031, 7550, 7551, 7552, 7553,
	            7554, 7555, 8615, 8619, 8620, 8621, 8622, 9033, 8609, 4804, 8611, 8095, 8027};

	/**
	 * Revenant ids for the amulet of averice
	 */
	public static final int[] REV_IDS = { 7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940};

	public static final int[] LEAF_IDS = { 427, 428, 429, 430, 410, 411, 10397, 7405};

	public static final int[] RAIMENTS_OF_THE_EYE_PEICES = { 26850, 26852, 26854, 26856, 26858, 26860, 26862, 26864, 26866, 26868, 26870, 26872, 26874};// Hat of the eye, Robe top of the eye, Robe bottom of the eye, Robe boots of the eye + (red, green, blue dyed versions in order)

	/**
	 * Items that cannot be dropped.
	 */
	public static final int[] TOURNAMENT_ITEMS_DROPPABLE = { 3144, 385, 11936, 13441 };

	/**
	 * undead npcs.
	 */
	public static final int[] UNDEAD_NPCS = {
			2,3,4,5,6,7,2514,2515,2516,2517,2518,2519,6608,7257,7296,7864,414,120,448,449,450,451,452,453,454,455,456,
			457,945,946,5622,5623,5624,5625,5626,5627,85,86,87,88,89,90,717,718,719,720,721,722,723,724,725,726,727,728,
			7881,7931,7932,7933,7934,7935,7936,7937,7938,7939,7940,5633,6740,7258,70,71,72,73,74,75,76,77,78,79,80,872,878,
			879,2999,2993,4421,3969,3970,3971,8026,8058,8059,8060,8061,4500,4505,882,5353,5354,5355,3616,547,1163,8359,6611,
			6612,1672,1673,1674,1675,1676,1677,1042,1043,1044,1045,1046,1047,1048,1049,1050,1051,26,27,28,29,30,31,32,33,34,
			35,8062,8063,866,867,868,869,870,871,873,874,4312,4318,4319,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,
			54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,81,82,83,91,92,93,94,95,96,97,130,875,876,877,880,920,924,949,950,
			951,952,953,1538,1539,1541,1685,1686,1687,1688,1784,1785,1786,2501,2502,2503,2504,2505,2506,2507,2508,2509,2520,2521,
			2522,2523,2524,2525,2526,2527,2528,2529,2530,2531,2532,2533,2534,2992,3516,3565,3584,3617,3625,3972,3973,3974,3975,
			3976,3977,3978,3979,3980,3981,5237,5342,5343,5344,5345,5346,5347,5348,5349,5350,5351,5370,5506,5507,5568,5571,5574,
			5583,5647,6441,6442,6443,6444,6445,6446,6447,6448,6449,6450,6451,6452,6453,6454,6455,6456,6457,6458,6459,6460,6461, 2145,
			6462,6463,6464,6465,6466,6467,6468,6596,6597,6598,6741, Npcs.SKELETON_HELLHOUND, 7413, Npcs.PESTILENT_BLOAT, 10812, 10813,
			11184, 7604, 7605, 7606
	};
	/**
	 * NPCs that represent demons for the Arclight
	 */
	public static final int[] DEMON_IDS = { 1531, 3134, 2006, 2026, 7244, 1432, 415, 7410, 135, 3133, 484, 1619, 7276,
			3138, 7397, 7398, 11, 7278, 7144, 7145, 7146, 3129, 3132, 3130, 3131, 7286, 5890, 7286, 5862, 5863, 5866 };

	/**
	 * The level in which you can not teleport in the wild, and higher.
	 */
	public static final int NO_TELEPORT_WILD_LEVEL = 20;
	/**
	 * The level in which you can not teleport above 30 wildy using glory.
	 */
	public static final int JEWELERY_TELEPORT_MAX_WILD_LEVEL = 30;

	/**
	 * The time, in game cycles that the skull above a player should exist for.
	 * Every game cycle is 600ms, every minute has 60 seconds. Therefor there are
	 * 100 cycles in 1 minute. .600 * 100 = 60.
	 */
	public static final int SKULL_TIMER = 2000;

	/**
	 * The maximum time for a player skull with an extension in the length.
	 */
	public static final int EXTENDED_SKULL_TIMER = 6000;

	/**
	 * Single and multi player killing zones.
	 */
	public static final boolean SINGLE_AND_MULTI_ZONES = true;

	/**
	 * Wilderness levels and combat level differences. Used when attacking players.
	 */
	public static final boolean COMBAT_LEVEL_DIFFERENCE = true;

	/**
	 * Skill names and id's
	 */
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
			"Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter" };

	public static final int DEFAULT_COMBAT_EXPERIENCE_RATE = 250;

	public static String QUESTION = "";
	public static String ANSWER = "";
	public static boolean BONUS_WEEKEND;
	public static boolean BONUS_PC = true;
	public static boolean DOUBLE_DROPS;
	public static boolean CYBER_MONDAY;
	public static boolean DOUBLE_PKP;
	public static boolean DOUBLE_VOTE_INCENTIVES;
	public static boolean wildyPursuit;

	/**
	 * Looting bag and rune pouch
	 */
	public static boolean BAG_AND_POUCH_PERMITTED = true;

	/**
	 * How fast the special attack bar refills.
	 */
	public static final int INCREASE_SPECIAL_AMOUNT = 31000;

	/**
	 * If you need a certain magic level to use a certain spell.
	 */
	public static final boolean MAGIC_LEVEL_REQUIRED = true;

	/**
	 * How long the god charge spell lasts.
	 */
	public static final int GOD_SPELL_CHARGE = 300000;

	/**
	 * If you need runes to use magic spells.
	 */
	public static final boolean RUNES_REQUIRED = true;

	/**
	 * If you need correct arrows to use with bows.
	 */
	public static final boolean CORRECT_ARROWS = true;

	/**
	 * If the crystal bow degrades.
	 */
	public static final boolean CRYSTAL_BOW_DEGRADES = true;

	/**
	 * How often the server saves data.
	 */
	public static final int SAVE_TIMER = 60; // Saves every one minute.

	/**
	 * How far NPCs can walk.
	 */
	public static final int NPC_RANDOM_WALK_DISTANCE = 8; // 5x5 square, NPCs
															// would be able to
															// walk 25 squares
															// around.

	/**
	 * The starting location of your server.
	 */
	public static final int START_LOCATION_X = 3101;
	public static final int START_LOCATION_Y = 3509;
	public static final Position START_POSITION = new Position(START_LOCATION_X, START_LOCATION_Y);

	/**
	 * The re-spawn point of when someone dies.
	 */
	public static final int RESPAWN_X = 3101;
	public static final int RESPAWN_Y = 3509;
	public static final int RESPAWN_Z = 0;

	/**
	 * The re-spawn point of when a duel ends.
	 */
	public static final int DUELING_RESPAWN_X = 3362;
	public static final int DUELING_RESPAWN_Y = 3263;

	/**
	 * Glory locations.
	 */
	public static final int EDGEVILLE_X = 3091;
	public static final int EDGEVILLE_Y = 3490;

	public static final int AL_KHARID_X = 3293;
	public static final int AL_KHARID_Y = 3176;

	public static final int KARAMJA_X = 2925;
	public static final int KARAMJA_Y = 3173;

	public static final int DRAYNOR_X = 3079;
	public static final int DRAYNOR_Y = 3250;


	/*
	 * Modern spells
	 */
	public static final int VARROCK_X = 3210;
	public static final int VARROCK_Y = 3424;

	public static final int LUMBY_X = 3222;
	public static final int LUMBY_Y = 3218;

	public static final int FALADOR_X = 2964;
	public static final int FALADOR_Y = 3378;

	public static final int CAMELOT_X = 2757;
	public static final int CAMELOT_Y = 3477;

	public static final int ARDOUGNE_X = 2662;
	public static final int ARDOUGNE_Y = 3305;

	public static final int WATCHTOWER_X = 2549;
	public static final int WATCHTOWER_Y = 3112;

	public static final int TROLLHEIM_X = 2888;
	public static final int TROLLHEIM_Y = 3676;

	/*
	 * Ancient spells
	 */
	public static final int PADDEWWA_X = 3098;
	public static final int PADDEWWA_Y = 9884;

	public static final int SENNTISTEN_X = 3322;
	public static final int SENNTISTEN_Y = 3336;

	public static final int KHARYRLL_X = 3492;
	public static final int KHARYRLL_Y = 3471;

	public static final int LASSAR_X = 3006;
	public static final int LASSAR_Y = 3471;

	public static final int DAREEYAK_X = 2966;
	public static final int DAREEYAK_Y = 3695;

	public static final int CARRALLANGAR_X = 3156;
	public static final int CARRALLANGAR_Y = 3666;

	public static final int ANNAKARL_X = 3288;
	public static final int ANNAKARL_Y = 3886;

	public static final int GHORROCK_X = 2977;
	public static final int GHORROCK_Y = 3873;

	/**
	 * Buffer size.
	 */
	public static final int BUFFER_SIZE = 512;

	public static final Position ONYX_ZONE_TELEPORT = new Position(2142, 2849, 0);
	public static long DOUBLE_DROPS_TIMER;

	public static boolean BONUS_PC_WOGW = false;
	public static boolean DOUBLE_BARROWS = false;
	public static boolean BONUS_XP_WOGW = false;

	public static int BONUS_SKILL = -1;
	public static long BONUS_SKILL_TIMER = 3000;

	public final static String TEXT_SEPERATOR = "#=#";


	public static int getMainCurrencyId() {
		return 995;
	}
	public static String getMainCurrencyName() {
		return ItemAssistant.getItemName(getMainCurrencyId());
	}

	/**
	 * The slot identity of the head in the equipment tab.
	 */
	public static final int HEAD_SLOT = 0;

	/**
	 * The slot identity of the cape in the equipment tab.
	 */
	public static final int CAPE_SLOT = 1;

	/**
	 * The slot identity of the amulet in the equipment tab.
	 */
	public static final int AMULET_SLOT = 2;

	/**
	 * The slot identity of the weapon in the equipment tab.
	 */
	public static final int WEAPON_SLOT = 3;

	/**
	 * The slot identity of the torso in the equipment tab.
	 */
	public static final int BODY_SLOT = 4;

	/**
	 * The slot identity of the shield in the equipment tab.
	 */
	public static final int SHIELD_SLOT = 5;

	/**
	 * The slot identity of the leg in the equipment tab.
	 */
	public static final int LEG_SLOT = 7;

	/**
	 * The slot identity of the hand in the equipment tab.
	 */
	public static final int HAND_SLOT = 9;

	/**
	 * The slot identity of the feet in the equipment tab.
	 */
	public static final int FEET_SLOT = 10;

	/**
	 * The slot identity of the ring in the equipment tab.
	 */
	public static final int RING_SLOT = 12;

	/**
	 * The slot identity of the arrow in the equipment tab.
	 */
	public static final int ARROW_SLOT = 13;

	/**
	 * Voting claims
	 */
	public static int VOTES_CLAIMED = 0;
	public static boolean BONUS_VOTES = false;//To be added for bonusvoting


	public static boolean isOwner(Player p) {
		return p.getAttributes().get("ownerkey") != null || p.getLoginName().equalsIgnoreCase("Trebble") || p.getLoginName().equalsIgnoreCase("Rogue") || p.getLoginName().equalsIgnoreCase("Rogue test");
	}

	public static long REV_EVENT_TIMER = RevenantEventBossHandler.generateTime();
	public static long SOLAK_EVENT_TIMER = SolakEventBossHandler.generateTime();
	public static long NIGHTMARE_EVENT_TIMER = KoranianEventBossHandler.generateTime();
	public static long GLOD_EVENT_TIMER = EventBossHandler.generateTime();
	public static long GALVEK_EVENT_TIMER = GalvekEventBossHandler.generateTime();//
	public static long STAR_EVENT_TIMER = ShootingStar.generateTime();
	public static long TREE_EVENT_TIMER = EvilTree.generateTime();
	public static long WILD_EVENT_TIMER = WorldEvent.generateTime();
}