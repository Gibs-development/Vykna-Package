package io.xeros.content.vykna_teleports.model;

/**
 * Server-side teleport row definition (data only).
 */
public final class TeleportDefinition {

    private final int id; // stable row id (server-side)
    private final TeleportCategory category;

    private final String name;
    private final String description;

    private final TeleportRequirement requirements;
    private final String questName; // nullable

    // NPC preview / info
    private final int npcId;
    private final int combatLevel;
    private final int hitpoints;
    private final boolean aggressive;

    // Client sprite-map head index (0..63) for RowHeads atlas
    private final int headIconIndex;

    private final TeleportDestination destination;

    public TeleportDefinition(int id,
                              TeleportCategory category,
                              String name,
                              String description,
                              TeleportRequirement requirements,
                              String questName,
                              int npcId,
                              int combatLevel,
                              int hitpoints,
                              boolean aggressive,
                              int headIconIndex,
                              TeleportDestination destination) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.questName = questName;
        this.npcId = npcId;
        this.combatLevel = combatLevel;
        this.hitpoints = hitpoints;
        this.aggressive = aggressive;
        this.headIconIndex = headIconIndex;
        this.destination = destination;
    }

    public int getId() { return id; }
    public TeleportCategory getCategory() { return category; }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public TeleportRequirement getRequirements() { return requirements; }
    public String getQuestName() { return questName; }

    public int getNpcId() { return npcId; }
    public int getCombatLevel() { return combatLevel; }
    public int getHitpoints() { return hitpoints; }
    public boolean isAggressive() { return aggressive; }

    public int getHeadIconIndex() { return headIconIndex; }

    public TeleportDestination getDestination() { return destination; }
}
