package com.vykna.importer.preview

object RsModelMerger {

    fun merge(parts: List<RsModel>): RsModel {
        require(parts.isNotEmpty()) { "No models to merge" }
        if (parts.size == 1) return parts[0]

        val totalV = parts.sumOf { it.vertexCount }
        val totalF = parts.sumOf { it.faceCount }

        val vx = IntArray(totalV)
        val vy = IntArray(totalV)
        val vz = IntArray(totalV)

        val fx = IntArray(totalF)
        val fy = IntArray(totalF)
        val fz = IntArray(totalF)

        val skin = IntArray(totalV) { -1 }

        var vOff = 0
        var fOff = 0

        for (m in parts) {
            // vertices
            System.arraycopy(m.vx, 0, vx, vOff, m.vertexCount)
            System.arraycopy(m.vy, 0, vy, vOff, m.vertexCount)
            System.arraycopy(m.vz, 0, vz, vOff, m.vertexCount)

            // vertex skin/groups if present
            if (m.vertexSkin.isNotEmpty() && m.vertexSkin.size == m.vertexCount) {
                System.arraycopy(m.vertexSkin, 0, skin, vOff, m.vertexCount)
            }

            // faces (shift indices)
            for (i in 0 until m.faceCount) {
                fx[fOff + i] = m.fx[i] + vOff
                fy[fOff + i] = m.fy[i] + vOff
                fz[fOff + i] = m.fz[i] + vOff
            }

            vOff += m.vertexCount
            fOff += m.faceCount
        }

        // If no skin data anywhere, drop it so animator knows there are no groups
        val anySkin = skin.any { it >= 0 }
        val finalSkin = if (anySkin) skin else IntArray(0)

        return RsModel(vx, vy, vz, fx, fy, fz, finalSkin)
    }
}
