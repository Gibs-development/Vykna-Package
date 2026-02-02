# Quest Setup Notes

This file lists NPCs and items you need to spawn / ensure exist for the custom quests.

## Imp Catcher
- Start/turn-in NPC: Wizard Mizgog
  - NPC IDs: 7746 / 7747 (from `etc/cfg/npc/npc_definitions.json`)
- Required items:
  - Red bead: 1470
  - Yellow bead: 1472
  - Black bead: 1474
  - White bead: 1476
- Reward item:
  - Amulet of Accuracy: 1478

## Courier's Favor (delivery quest)
- Quest ID: `courier_favor`
- Start/finish NPC: Engineering assistant
  - NPC ID: 1413 (from `etc/cfg/npc/npc_definitions.json`)
- Delivery NPC: Banker
  - NPC ID: 766 (from `etc/cfg/npc/npc_definitions.json`)
- Delivery item:
  - Research package: 290 (from `etc/cfg/item/item_definitions.yaml`)

### Flow
1) Talk to Engineering assistant to start and receive the research package.
2) Deliver the package to the Banker.
3) Return to the Engineering assistant for a coin reward.

## One Small Favour (very long delivery chain)
- Quest ID: `one_small_favour`
- Start NPC: ID 9 (Twig) — change as needed
- Chain NPCs (50 total, in order):
  1) 9 Twig
  2) 359 Suspect
  3) 673 Grubfoot
  4) 851 Penguin
  5) 1078 Runa
  6) 1222 Justin Servil
  7) 1375 Svidi
  8) 1577 Adventurer (easy)
  9) 1669 Penance Runner
  10) 1901 Art Critic Jacques
  11) 2128 Snakeling
  12) 2346 Dromund's cat
  13) 2438 Dwarven Miner
  14) 2668 Combat dummy
  15) 2880 Zaff
  16) 3097 Estate agent
  17) 3309 Magic Instructor
  18) 3506 Civilian
  19) 3642 Nisha
  20) 3831 Cat
  21) 3955 Jungle forester
  22) 4118 Eadgar
  23) 4269 Man
  24) 4463 Calin
  25) 4626 Cook
  26) 4771 Sir Amik Varze
  27) 4966 Remsai
  28) 5191 Captain Lawgof
  29) 5385 Sandy
  30) 5533 Dashing kebbit
  31) 5767 Penance Healer
  32) 5975 Flying Book
  33) 6418 Layleen
  34) 6652 Prince Black Dragon
  35) 6801 Pieve
  36) 6969 Banker
  37) 7083 Armourer (tier 1)
  38) 7300 Kazgar
  39) 7450 Rock Golem
  40) 7645 Rock Golem
  41) 7787 Mattimeo
  42) 8023 Gnosi
  43) 8208 Mysterious Stranger
  44) 8405 Askeladden
  45) 8584 Flower
  46) 8757 Crystal impling
  47) 8891 Rebel Warrior
  48) 9065 Thingol
  49) 9156 Celyn
  50) 9329 Trader Crewmember

### Items (one per step, in order)
1) 1 Tool kit
2) 8 Cannon stand
3) 14 Railing
4) 20 Cog
5) 26 Fishing trophy
6) 32 Lit black candle
7) 38 Black candle
8) 44 Rune arrowtips
9) 50 Shortbow (u)
10) 56 Oak longbow (u)
11) 62 Maple longbow (u)
12) 68 Yew shortbow (u)
13) 74 Khazard helmet
14) 84 Staff of armadyl
15) 90 Child's blanket
16) 96 Tarromin potion(unf)
17) 102 Irit potion(unf)
18) 108 Cadantine potion(unf)
19) 114 Strength potion(4)
20) 120 Strength potion(1)
21) 126 Attack potion(1)
22) 132 Restore potion(1)
23) 138 Defence potion(1)
24) 144 Prayer potion(1)
25) 150 Super attack(1)
26) 156 Fishing potion(1)
27) 162 Super strength(1)
28) 168 Super defence(1)
29) 174 Ranging potion(1)
30) 180 Antipoison(1)
31) 186 Superantipoison(1)
32) 192 Zamorak brew(2)
33) 198 Poison chalice
34) 209 Grimy irit
35) 220 Torstol
36) 226 Limpwurt root
37) 232 Snape grass
38) 238 Unicorn horn
39) 244 Blue dragon scale
40) 250 Clean guam
41) 256 Clean harralander
42) 262 Clean avantoe
43) 268 Clean dwarf weed
44) 274 Poisoned fish food
45) 280 Sheep bones (1)
46) 286 Orange goblin mail
47) 293 A key
48) 299 Mithril seeds
49) 305 Big fishing net
50) 311 Harpoon

### Flow
1) Start with NPC #1 to receive item #1.
2) Deliver each item to the next NPC in the list.
3) After NPC #50 gives you the final item, return to NPC #1 to complete.

## Desert Treasure (replica, Ancient Magicks unlock)
- Quest ID: `desert_treasure`
- Start NPC: Eblis
  - NPC IDs: 688 / 689 (from `etc/cfg/npc/npc_definitions.json`)
- Ring of visibility source: Rasolo
  - NPC ID: 679
- Malak (requires ring to see)
  - NPC ID: 686
- Final NPC: Azzanadra
  - NPC ID: 730

### Bosses (spawn these where you want the fights)
- Dessous: 3459 (Blood diamond)
- Fareed: 3456 (Smoke diamond)
- Kamil: 3458 (Ice diamond)
- Damis: 682 (Shadow diamond)

### Required items
- Ring of visibility: 4657
- Blood diamond: 4670
- Ice diamond: 4671
- Smoke diamond: 4672
- Shadow diamond: 4673

### Objects you must spawn
- Ancient altar (used with all 4 diamonds to free Azzanadra)
  - Object ID: 6552 (standard ancient altar)

### Flow
1) Talk to Eblis to start.
2) Talk to Rasolo to receive a Ring of visibility.
3) With the ring, talk to Malak to learn about the diamonds.
4) Kill Dessous, Fareed, Kamil, Damis to obtain the four diamonds.
5) Use all 4 diamonds on the Ancient altar to break the seal.
6) Talk to Azzanadra to complete and unlock Ancient Magicks.

## Waterfall Quest
- Quest ID: `waterfall_quest`
- Start NPC: Almera
  - NPC ID: 4181
- Hudon (flavor / support)
  - NPC ID: 4182 (alt: 12 "Hudo")
- Golrie (cave NPC)
  - NPC ID: 4183 (alt: 892)

### Required items
- Rope: 954
- Glarial's pebble: 294
- Glarial's amulet: 295
- Glarial's urn: 296

### Objects you must spawn (IDs in code placeholders — update as needed)
- Rope rock (use rope on this)
  - Object ID: 1999 (`WaterfallQuestHandler.OBJECT_ROPE_ROCK_ID`)
- Statue/Chest for Glarial's amulet (use pebble on this)
  - Object ID: 2000 (`WaterfallQuestHandler.OBJECT_PEBBLE_STATUE_ID`)
- Waterfall cave entrance (requires wearing amulet)
  - Object ID: 2001 (`WaterfallQuest.OBJECT_WATERFALL_ENTRANCE_ID`)

### Flow
1) Talk to Almera to start and receive Glarial's pebble.
2) Use a rope on the rock near the waterfall.
3) Use Glarial's pebble on the statue/chest to obtain Glarial's amulet.
4) Wear the amulet and click the waterfall cave entrance to enter.
5) Talk to Golrie inside the cave to obtain Glarial's urn.
6) Return the urn to Almera to complete.
