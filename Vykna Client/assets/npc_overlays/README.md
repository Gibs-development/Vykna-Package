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

## Sprite IDs (media_archive4)
Weakness IDs:
- MELEE: 4000
- RANGED: 4001
- AIR: 4002
- WATER: 4003
- EARTH: 4004
- FIRE: 4005

Status IDs:
- SNARE: 4010
- FREEZE: 4011
- POISON: 4012
- VENOM: 4013
- SALVE: 4014
- DEMON/UNDEAD: 4015

## Placement + packing
1. Create the PNGs in your client cache sprite source folder (e.g. `sprites/healthbar`).
2. Pack the PNGs into the client sprite archive **media_archive4** using your sprite packer tool,
   preserving the sprite IDs listed above.
3. Copy the packed files to the client cache:
   - `<cache_dir>/media_archives/media_archive4.dat`
   - `<cache_dir>/media_archives/media_archive4.idx`

The client loads these sprites via `SpriteLoader4` at runtime.
