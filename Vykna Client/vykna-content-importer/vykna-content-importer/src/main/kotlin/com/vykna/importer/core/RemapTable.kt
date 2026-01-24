package com.vykna.importer.core

class RemapTable {
    private val map = HashMap<Int, Int>()
    fun getOrPut(oldId: Int, allocator: () -> Int): Int = map.getOrPut(oldId) { allocator() }
    fun get(oldId: Int): Int? = map[oldId]
    fun entries(): Map<Int, Int> = map.toMap()
}
