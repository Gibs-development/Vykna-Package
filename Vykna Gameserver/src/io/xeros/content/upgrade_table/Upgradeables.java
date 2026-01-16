package io.xeros.content.upgrade_table;

import java.util.EnumSet;
import java.util.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 *  The upgradables for the upgrade table .
 *
 * @author C.T for koranse
 * @on the 30/09/2020
 */
public enum Upgradeables {
	
	NECKLACEOFANGUISH(19547,22249,25),// Necklace of anguish - Necklace of anguish or
	ABYSSALWHIP(4151,12006,25),// Abyssal Whip - Abyssal tenticle
	RINGOFSUFFERING(19550,20657,30),// Ring of suffering - Ring of suffering (ri)
	AMULETOFTORTURE(19553,20366,30),// Amulet of torture - Amulet of torture (or)
	AMULETOFFURY(6585, 12436, 20),// Amulet of fury - Amulet of fury (or)
//	ARMADLYGODSWORD(11802, 20368, 20),// Armadyl godsword - Armadyl godsword (or)
//	BANDOSGODSWORD(11804, 20370, 20),// Bandos godsword - Bandos godsword (or)
//	SARADOMINGODSWORD(11806,20372,30),// Saradomin godsword - Saradomin godsword (or)
//	ZAMORAKGODSWORD(11808, 20374, 20),// Zamorak godsword - Zamorak godsword (or)
	RINGOFTHEGODS(12601, 13202, 20),// Ring of the gods - Ring of the gods (i)
	SEERSRING(6731, 11770, 20),// Seers ring - Seers ring (i)
	ARCHERSRING(6733,11771,30),// Archers ring - Archers ring (i)
	WARRIORRING(6735, 11772, 20),// Warrior ring - Warrior ring (i)
	BERSERKERING(6737, 11773, 20),// Berserker ring - Berserker ring (i)
	TYRANNICALRING(12603, 12691, 20),/// Tyrannical ring - Tyrannical ring (i)
	TREASONOUSRING(12605, 12692, 20);// Treasonous ring - Treasonous ring (i)
	//DRAGONSCIMITAR(4587, 20000, 20);// Dragon scimitar - Dragon scimitar (or)
	
	private final int oldItemId;
	
	private final int newItemId;
	
	private final int chanceToUpgrade;

	public int getOldItemId() {
		return oldItemId;
	}

	public int getNewItemId() {
		return newItemId;
	}
	
	public int getChanceToUpgrade() {
		return chanceToUpgrade;
	}

	private Upgradeables(int oldItemId, int newItemId, int chanceToUpgrade) {
		this.oldItemId = oldItemId;
		this.newItemId = newItemId;
		this.chanceToUpgrade = chanceToUpgrade;
	}
	
	private static final ImmutableSet<Upgradeables> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Upgradeables.class));
	
	public static Optional<Upgradeables> forID(final int oldItemId) {
		return VALUES.stream().filter(upgrade -> upgrade.oldItemId == oldItemId).findFirst();
	}

}
