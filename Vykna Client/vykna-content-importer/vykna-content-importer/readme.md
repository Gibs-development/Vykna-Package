# Vykna Content Importer (667 → Vykna cache)

Goal:
- browse NPCs/Items/SpotAnims/Sequences from a 667 dump
- click one, see dependencies (models / frames / seq / spotanim)
- export: pack only required files into your base cache and generate a report + client snippet

Curses quick IDs (common 602-era):
- Turmoil: GFX 2226, emote/anim 12565
- Soul Split: GFX 2263 (projectile), 2264 (heal)
  (Importer will let you confirm by previewing spotanims.)

Run:
- gradle run
