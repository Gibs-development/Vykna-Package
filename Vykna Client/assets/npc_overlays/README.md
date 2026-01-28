# NPC Overhead Overlay Sprites

This folder documents the NPC overhead overlay icons. Binary assets are not stored in this repo.

## Required sprite filenames
Weakness icons:
- `weakness_melee.png`
- `weakness_ranged.png`
- `weakness_air.png`
- `weakness_water.png`
- `weakness_earth.png`
- `weakness_fire.png`

Status icons:
- `status_snare.png`
- `status_freeze.png`
- `status_poison.png`
- `status_venom.png`
- `status_salve.png`
- `status_demon_undead.png`

## Target dimensions
- 12x12px (transparent PNG)

## Sprite names (cache sprites/healthbars)
Weakness names:
- `npc_weakness_melee.png`
- `npc_weakness_ranged.png`
- `npc_weakness_air.png`
- `npc_weakness_water.png`
- `npc_weakness_earth.png`
- `npc_weakness_fire.png`

Status names:
- `npc_status_snare.png`
- `npc_status_freeze.png`
- `npc_status_poison.png`
- `npc_status_venom.png`
- `npc_status_salve.png`
- `npc_status_demon_undead.png`

## Placement + packing
1. Create the PNGs in your client cache sprite folder:
   - `<cache_dir>/sprites/healthbars/`
2. Ensure the filenames match the names listed above.

The client loads these sprites directly from the cache sprite folder at runtime.
