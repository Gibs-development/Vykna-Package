# Unified NPC definition data

The server now supports unified NPC definitions that override legacy data sources. To add or update an NPC, drop a single JSON file into:

```
Vykna Gameserver/data/NpcDefData/
```

## File naming

Each file is named `{id}_{name}.json` where:
- `{id}` is the NPC id (this is the primary key).
- `{name}` is cosmetic and helps humans find files.

Examples:

```
1092_Starter_Gulega.json
3407_Elemental_balance.json
```

## JSON schema overview

Each file contains one object with:
- `identity`: name, examine (optional), size (optional).
- `combat`: hp, combatLevel, attackSpeed, attackStyle(s), aggression, immunities, levels, bonuses.
- `metadata`: weakness (MELEE/RANGED/AIR_MAGIC/WATER_MAGIC/EARTH_MAGIC/FIRE_MAGIC), type (NONE/DEMON/UNDEAD/DRAGON/…).

When present, unified NPC data overrides the legacy `npc_stats.json`, `npc_combat_defs.json`, and `npc_definitions.json`. When missing, the server logs a legacy fallback warning so you can migrate gradually.

## Adding a new NPC

1. Create a new JSON file in `data/NpcDefData` named `{id}_{name}.json`.
2. Set the `id` in the JSON to match the filename id.
3. Fill in `identity`, `combat`, and `metadata` as needed.
4. Restart the server so the loader picks up the new file.

You can generate a baseline set of unified files from existing data with:

```
java io.xeros.util.NpcDefDataExporter
```
