package com.vykna.importer.source.defs

data class NpcLite(
    val id: Int,
    val name: String,
    val examine: String?,
    /** 5 action slots (Talk-to, Attack, etc). Null/blank means none. */
    val actions: Array<String?>,
    val models: IntArray,
    val standAnim: Int,
    val walkAnim: Int,
    /**
     * Extra movement/turn sequences found in many formats.
     * NOTE: These are not always attack/death/block in 667; they are exposed for browsing.
     */
    val turn180Anim: Int,
    val turn90RightAnim: Int,
    val walkBackAnim: Int,
    val walkLeftAnim: Int,
    val walkRightAnim: Int,
    val combatLevel: Int,
    /** NPC size (tile size). Commonly opcode 12 in npc.dat. */
    val size: Int,
    /** Transform / morph children IDs from opcode 106/118 (varbit/varp based). */
    val childrenIDs: IntArray
)
