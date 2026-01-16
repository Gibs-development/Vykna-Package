package io.xeros.script.npc;

import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.player.Player;
import io.xeros.script.Script;
import io.xeros.script.ScriptType;


/*
 * NPC manifest.
 * 
 * @author Adil
 * @version 1.0 - 07/03/2017
 */
public abstract class AstuteNPC extends NPC implements Script {



	protected int x;
	protected int y;

	protected int attack;
	protected int defence;

	/**
	 * Constructs a blank {@link AstuteNPC} for script instantiation.
	 */
	public AstuteNPC() {
		this(1, new Location(0, 0));
	}
	
	/**
	 * Constructs a new {@link AstuteNPC} {@code Object}.
	 * 
	 * @param npcId
	 * 				the NPC index.
	 * @param position
	 * 				The npc {@link Position}
	 * @param hp
	 * 		The boss hitpoints.
	 * @param maxHit
	 * 			The max hit of the {@link NPC}.
	 * @param attack
	 * 			The attack level.
	 * @param defence
	 * 			The defence level.
	 */
	public AstuteNPC(int npcId, Location position) {
		super(npcId, npcId, NpcDef.forId(npcId));
		x = position.getX();
		y = position.getY();
		setX(position.getX());
		setY(position.getY());
		setHeight(position.getZ());
		walkingType = getMovementType();
		getHealth().setMaximumHealth(getHealthCapacity());
		getHealth().reset();
		maxHit = getHitCapacity();
		attack = getAttackConstant();
		defence = getDefenceConstant();
		teleport(position.getX(), position.getY(), position.getZ());
	}
	
	@Override
	public Object start(Object... args) {
		return null;
	}
	
	/**
	 * The identifier key for mapping.
	 */
	public abstract Object getIdentifier();

	public abstract int getMovementType();
	
	public abstract int getHitCapacity();

	public abstract int  getHealthCapacity();
	
	public abstract int  getAttackConstant();
	
	public abstract int  getDefenceConstant();
	
	public abstract void attack();
	
	@Override
	public ScriptType getType() {
		return ScriptType.NPC;
	}
	
	/**
	 * Death delegate of the boss.
	 * 
	 * @param killer
	 * 			The killing {@link Player}.
	 */
	public void uponDeath(Player killer) { }

}
