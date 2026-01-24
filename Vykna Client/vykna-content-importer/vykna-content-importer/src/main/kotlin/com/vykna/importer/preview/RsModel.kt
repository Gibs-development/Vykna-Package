package com.vykna.importer.preview

data class RsModel(
    val vx: IntArray,
    val vy: IntArray,
    val vz: IntArray,
    val fx: IntArray,
    val fy: IntArray,
    val fz: IntArray,
    val vertexSkin: IntArray = IntArray(0) // group id per vertex
) {
    val vertexCount get() = vx.size
    val faceCount get() = fx.size

    // keep originals for animation reset
    private val ox = vx.clone()
    private val oy = vy.clone()
    private val oz = vz.clone()

    fun resetVertices() {
        System.arraycopy(ox, 0, vx, 0, vx.size)
        System.arraycopy(oy, 0, vy, 0, vy.size)
        System.arraycopy(oz, 0, vz, 0, vz.size)
    }

    fun vertexGroups(): Array<IntArray>? {
        if (vertexSkin.isEmpty()) return null
        var max = -1
        for (g in vertexSkin) if (g > max) max = g
        if (max < 0) return null

        val buckets = Array(max + 1) { mutableListOf<Int>() }
        for (i in vertexSkin.indices) {
            val g = vertexSkin[i]
            if (g >= 0) buckets[g].add(i)
        }
        return Array(buckets.size) { idx -> buckets[idx].toIntArray() }
    }
}
