package com.vykna.importer.core

class IdAllocator(
    private val startInclusive: Int,
    private val endInclusive: Int,
    initialNext: Int = startInclusive
) {
    private var next = initialNext

    @Synchronized
    fun nextId(): Int {
        if (next > endInclusive) throw IllegalStateException("ID range exhausted: $startInclusive..$endInclusive")
        return next++
    }

    fun peek(): Int = next
}
