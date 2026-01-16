package io.xeros.achievements;

import io.xeros.Configuration;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.AchievementType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.xeros.model.Items.*;

/**
 * List of all achievements.
 * 
 * @author C.T for runerogue
 *
 */
public enum AchievementList {

	/* Easy Achievements */
	ANSWER_15_TRIVIABOTS_CORRECTLY("Answer 15 TriviaBots correctly", 15, AchievementHandler.AchievementDifficulty.EASY, new GameItem(995, 5000000)),//500K CASH
	CLAIM_5_VOTES("Claim 12 hours of votes x5", 5, AchievementHandler.AchievementDifficulty.EASY, new GameItem(1464, 20)),//20 VOTE TICKETS
	COMPLETE_25_EASY_CLUES("Complete 25 Easy clue scrolls", 25, AchievementHandler.AchievementDifficulty.EASY, new GameItem(995, 10000000)),//10M CASH
	COMPLETE_25_NORMAL_CLUES("Complete 25 Normal clue scrolls", 25, AchievementHandler.AchievementDifficulty.EASY, new GameItem(995, 20000000)),//20M DONE
	ADVANCED_OPTIONS("Explore the Advanced options", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(21046 , 1)),//CHEST RATE BONUS
	SKILLING_ISLAND("Teleport to Skilling Island", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(30000, 5)),//RESOURCE BOX SMALL X5
	PARTICIPATE_IN_1_TOURNAMENT("Join a tournament", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(2528, 2)),//XP LAMPS x2
	MONEY_POUCH("Add coins into your money pouch", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(21046 , 1)),//CHEST RATE BONUS
	CREATE_A_RAID_PARTY("Create a raid party at Cox", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(21046 , 1)),//CHEST RATE BONUS
	CHANGING_PRAYERS("Change your magic book", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(2528, 1)),//XP LAMPS x1
	MONKEY_MADNESS("Complete the Monkey madness quest", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(6199, 1)),//MYSTERY BOX x1
	UPGRADE_1("Successfully upgrade an item on the table", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(989, 3)),//CRYSTAL KEY X3
	FLOWER_POKER_1("Flower poker gamble with another player", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(10833, 2)),//COIN BAG X2
	DONATE_WELL("Donate to the well", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem(2528, 2)),//XP LAMPS x2
	ENTER_THE_LOTTERY_5_TIMES("Enter the lottery 5 times", 5, AchievementHandler.AchievementDifficulty.EASY, new GameItem(21046 , 5)),//CHEST RATE BONUS
    REVENANT_HUNTER_I("Revenant hunter I", "Kill 250 revenants.", 250, AchievementHandler.AchievementDifficulty.EASY, new GameItem(ANCIENT_EMBLEM,2)),//ANCIENT EMBLEM X1
    DRAGON_SLAYER_I("Dragon slayer I", "Kill 250 green dragons.", 250, AchievementHandler.AchievementDifficulty.EASY, new GameItem(DRAGON_BONES,350)),//DRAGON BONES X350
    VETION_I("Vet'ion I", "Kill 25 Vet'ions.", 25, AchievementHandler.AchievementDifficulty.EASY, new GameItem(2996, 400)),//PK POINTS X400
	NEX_I("Nex I", "Kill 25 Nex.", 25, AchievementHandler.AchievementDifficulty.EASY, new GameItem (11942, 10)),//ECU KEY X10
	REV_MALEDICTUS_I("Revenant maledictus I", "Kill 1 Enranged revenant maledictus.", 1, AchievementHandler.AchievementDifficulty.EASY, new GameItem (21807, 2)),//ancient emblem X1
	RECHARGE_GLORYI("Amulet of glory I", "Recharge 50 amulet of glory's", 50, AchievementHandler.AchievementDifficulty.EASY, new GameItem (995, 5000000)),//5m gold coins


	/* Medium Achievements */
	BURN_200_LOGS("Burn 200 logs", 200, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(995 , 5000000)),//CASH 5M
	KILL_GALVEK("Kill 1 Galvek", 1, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(2996 , 300)),//PK POINTS X300
	COMPLETE_25_HARD_CLUES("Complete 25 Hard clue scrolls", 25, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(6828 , 1)),//SUPER MYSTERY BOX X1
	CRAFT_FEROCIOUS_GLOVES("Craft Ferocious gloves from Hydra leather", 1, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(691 , 1)),//10k FOE POINTS
	WIN_A_TOURNAMENT("Win a single tournament", 1, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(6828 , 1)),//SUPER MYSTERY BOX X1
	DRAGON_DEFENDER("Obtain a Dragon defender", 1, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(691 , 1)),//10k FOE POINTS
	DAILY_TASK("Complete 10 daily tasks", 10, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(13346 , 1)),//ULTRA MYSTERY BOX X1
    REVENANT_HUNTER_II("Revenant hunter II", "Kill 500 revenants.", 500, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(AMULET_OF_AVARICE, 1)),//AMULET OF AVARICE X1
    DRAGON_SLAYER_II("Dragon slayer II", "Kill 50 king black dragons.", 50, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(KBD_HEADS, 1)),//KBD HEADS
    VETION_II("Vet'ion II", "Kill 75 Vet'ions.", 75, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem(2996, 850)),//PK POINTS X850
	NEX_II("Nex II", "Kill 50 Nex.", 50, AchievementHandler.AchievementDifficulty.MEDIUM , new GameItem (23951, 6)),//Crystalline key x6
	REV_MALEDICTUS_II("Revenant maledictus II", "Kill 10 Enranged revenant maledictus.", 10, AchievementHandler.AchievementDifficulty.MEDIUM , new GameItem (12756, 1)),//arhiach emblem tier 10 x1
	RECHARGE_GLORYII("Amulet of glory II", "Recharge 250 amulet of glory's", 250, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem (6828, 1)),//Super m box x1

	/* Hard Achievements */
	COMPLETE_5_INFERNO("Complete Inferno 5 times", 5, AchievementHandler.AchievementDifficulty.HARD, new GameItem(6529 , 200000)),//TOKKUL 200K
	COMPLETE_25_MASTER_CLUES("Complete 25 Master clue scrolls", 25, AchievementHandler.AchievementDifficulty.HARD, new GameItem(3464 , 2)),//RARE RAIDS KEY X2
	WIN_10_TOURNAMENTS("Win 10 tournaments", 10, AchievementHandler.AchievementDifficulty.HARD, new GameItem(13346 , 3)),//ULTRA MYSTERY BOX X3
	KILL_25_KRATOS("Kill Kratos 25 times", 25, AchievementHandler.AchievementDifficulty.HARD, new GameItem(7302 , 10)),//KRATOS KEY X10
	KILL_50_VOTE_BOSS("Kill Vote boss 50 times", 50, AchievementHandler.AchievementDifficulty.HARD, new GameItem(8167 , 1)),//FOR MYSTERY CHEST X1
	ANSWER_80_TRIVIABOTS_CORRECTLY("Answer 80 TriviaBots correctly", 80, AchievementHandler.AchievementDifficulty.HARD, new GameItem(13346 , 2)),//ULTRA MYSTERY BOX X3
    REVENANT_HUNTER_III("Revenant hunter III", "Kill 2500 revenants.", 2500,  AchievementHandler.AchievementDifficulty.HARD, new GameItem(ANCIENT_STATUETTE,2)),//ANCIENT STATUETTE X2
    REVENANT_HUNTER_IV("Revenant hunter IV", "Kill 10000 revenants.", 10000,  AchievementHandler.AchievementDifficulty.HARD, new GameItem(CRAWS_BOW, 1)),//CRAWS BOWS X1
    DRAGON_SLAYER_III("Dragon slayer III", "Kill 500 king black dragons.", 500, AchievementHandler.AchievementDifficulty.HARD, new GameItem(ANCIENT_WYVERN_SHIELD, 1)),//ANCIENT WYVEN SHIELD X1
    DRAGON_SLAYER_IV("Dragon slayer IV", "Kill 200 rune dragons.", 200, AchievementHandler.AchievementDifficulty.HARD, new GameItem(DRAGONFIRE_WARD, 1)),//DRAGON FIRE WARD X1
    VETION_III("Vet'ion III", "Kill 150 Vet'ions.", 150, AchievementHandler.AchievementDifficulty.HARD, new GameItem (RING_OF_THE_GODS_I, 1)),//RING OF THE GODS I
	NEX_III("Nex III", "Kill 100 Nex.", 100, AchievementHandler.AchievementDifficulty.HARD , new GameItem (21307, 3)),//ROGUE CRATE x3
	REV_MALEDICTUS_III("Revenant maledictus III", "Kill 25 Enranged revenant maledictus.", 25, AchievementHandler.AchievementDifficulty.HARD , new GameItem (22542, 1)),//Vig chain mace x1
	ETERNAL_GLORY("Eternal glory", "Receive the amulet of eternal glory from the fountain of rune.", 1, AchievementHandler.AchievementDifficulty.HARD , new GameItem (2841, 3)),//bonus xp scroll
	RECHARGE_GLORYIII("Amulet of glory III", "Recharge 500 amulet of glory's.", 500, AchievementHandler.AchievementDifficulty.HARD, new GameItem (6828, 3)),//Super m box x3
	PET_SHARK("Pet shark", "Obtain the pet shark from the fishing caskets.", 1, AchievementHandler.AchievementDifficulty.HARD, new GameItem (6828, 5)),//Super m box x5

	MAX("Maxed skills", "Achieve level 99 in all skills on "+ Configuration.SERVER_NAME+"", 1, AchievementHandler.AchievementDifficulty.HARD, new GameItem(13346, 1), new GameItem(691, 1), new GameItem(995, 10000000)),

    //Pvp
    PVP_I("PVP I", "Kill 100 players in the wilderness.",100, AchievementHandler.AchievementDifficulty.EASY, new GameItem (BANDOS_GODSWORD, 1)),//BANDOS GODSWORD X1
    PVP_II("PVP II", "Kill 500 players in the wilderness.",500, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem (ARMADYL_GODSWORD, 1)),//ARMADYL GODSWORD X1
    PVP_III("PVP III", "Kill 1.000 players in the wilderness.",1000, AchievementHandler.AchievementDifficulty.HARD, new GameItem (VESTAS_LONGSWORD, 1)),//VESTA LONGSWORD

	COMPLETIONIST("Completionist", "Complete all Achievements besides this one.", 1,AchievementHandler.AchievementDifficulty.HARD, new GameItem(2396, 1)),//50$ donation scroll
   // BOUNTY_HUNTER_I("Bounty hunter I", "Kill 50 targets.", 50, AchievementHandler.AchievementDifficulty.EASY, new GameItem (PRIMORDIAL_BOOTS, 1)),
   // BOUNTY_HUNTER_II("Bounty hunter II", "Kill 100 targets.", 100, AchievementHandler.AchievementDifficulty.MEDIUM, new GameItem (NEITIZNOT_FACEGUARD, 1)),
   // BOUNTY_HUNTER_III("Bounty hunter III", "Kill 300 targets.", 300, AchievementHandler.AchievementDifficulty.EASY, new GameItem (AMULET_OF_TORTURE, 1)),
    ;


	public static List<AchievementList> asList(AchievementHandler.AchievementDifficulty difficulty) {
		return Arrays.stream(values()).filter(a -> a.getDifficulty() == difficulty).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
	}

	private final String name;
	private final String description;
	private int completeAmount;
	private final GameItem[] rewards;

	private final AchievementHandler.AchievementDifficulty difficulty;


	private AchievementList(String name, String description, int completeAmount, AchievementHandler.AchievementDifficulty difficulty,  GameItem... rewards) {
		this.name = name;
		this.description = description;
		this.completeAmount = completeAmount;
		this.difficulty = difficulty;
		this.rewards = rewards;
		for (GameItem b : rewards) if (b.getAmount() == 0) b.setAmount(1);
	}
	
	private AchievementList(String name, int completeAmount, AchievementHandler.AchievementDifficulty difficulty,  GameItem... rewards) {
		this.name = name;
		this.description = name;
		this.completeAmount = completeAmount;
		this.difficulty = difficulty;
		this.rewards = rewards;
		for (GameItem b : rewards) if (b.getAmount() == 0) b.setAmount(1);
	}

	public int getCompleteAmount() {
		return completeAmount;
	}



	public String getDescription() {
		return description;
	}

	public AchievementHandler.AchievementDifficulty getDifficulty() {
		return difficulty;
	}

	public String getName() {
		return name;
	}

	public static AchievementList getAchievement(String name) {
		for (AchievementList achievements : AchievementList.values())
			if (achievements.getName().equalsIgnoreCase(name))
				return achievements;
		return null;
	}

	public int getReward() {

		switch (difficulty) {
		case MEDIUM:
			return 2;
		case HARD:
			return 3;
		case EASY:

		default:
			return 1;
		}
	}



	public GameItem[] getRewards() {
		return rewards;
	}

	public static void addReward(Player player, AchievementList achievement) {
		for (GameItem item : achievement.getRewards()) {
			player.getItems().addItemToBankOrDrop(item.getId(), item.getAmount());
		}
	}


	public static int getTotal() {
		return values().length;
	}


}
