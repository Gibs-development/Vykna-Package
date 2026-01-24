package com.vykna.importer.preview

import com.vykna.importer.preview.rs.ModelLoader667
import com.vykna.importer.preview.rs.PreviewModel
import kotlin.math.abs

object RsModelDecoder {

    fun decode(rawModelBytes: ByteArray): RsModel {
        val n = rawModelBytes.size
        require(n > 8) { "Model too small" }

        val b1 = rawModelBytes[n - 2].toInt()
        val b2 = rawModelBytes[n - 1].toInt()

        // Some dumps don't reliably keep the format marker bytes at the tail,
        // and some models are in newer type1/2/3 while others are old-format.
        // So: try the expected decoder first, validate the result, then fall back.

        val preferred: List<(PreviewModel) -> Unit> = when {
            b2 == -3 && b1 == -1 -> listOf({ pm -> ModelLoader667.decodeType3(pm, rawModelBytes) })
            b2 == -2 && b1 == -1 -> listOf({ pm -> ModelLoader667.decodeType2(pm, rawModelBytes) })
            b2 == -1 && b1 == -1 -> listOf({ pm -> ModelLoader667.decodeType525(pm, rawModelBytes) }, { pm -> ModelLoader667.decodeType1(pm, rawModelBytes) })
            else -> listOf({ pm -> ModelLoader667.decodeOldFormat(pm, rawModelBytes) })
        }

        val fallbacks: List<(PreviewModel) -> Unit> = listOf(
            { pm -> ModelLoader667.decodeType525(pm, rawModelBytes) },
            { pm -> ModelLoader667.decodeType3(pm, rawModelBytes) },
            { pm -> ModelLoader667.decodeType2(pm, rawModelBytes) },
            { pm -> ModelLoader667.decodeType1(pm, rawModelBytes) },
            { pm -> ModelLoader667.decodeOldFormat(pm, rawModelBytes) },
        )

        val tried = LinkedHashSet<String>()
        // Keep order, avoid obvious duplicates
        val attempts = (preferred + fallbacks)

        var lastErr: Throwable? = null
        for (fn in attempts) {
            val pm = PreviewModel()
            try {
                fn(pm)
                val model = toRsModel(pm)
                if (isSane(model)) return model
                // Decoder returned without throwing, but geometry is clearly wrong.
                // Remember this so callers don't see "Last=null".
                lastErr = IllegalArgumentException("Decoder produced invalid geometry")
            } catch (t: Throwable) {
                lastErr = t
                tried.add(t::class.java.simpleName + ":" + (t.message ?: ""))
            }
        }

        throw IllegalArgumentException("Could not decode model (tried ${attempts.size} formats). Last=${lastErr?.message}", lastErr)
    }

    private fun toRsModel(pm: PreviewModel): RsModel {
        val vx = pm.verticesX ?: intArrayOf()
        val vy = pm.verticesY ?: intArrayOf()
        val vz = pm.verticesZ ?: intArrayOf()
        val fx = pm.trianglesX ?: intArrayOf()
        val fy = pm.trianglesY ?: intArrayOf()
        val fz = pm.trianglesZ ?: intArrayOf()
        val skin = pm.vertexData ?: IntArray(0)
        return RsModel(vx, vy, vz, fx, fy, fz, skin)

    }

    /** Lightweight validation so we don't show a single dot / needleball when a decoder is wrong. */
    private fun isSane(m: RsModel): Boolean {
        if (m.vx.isEmpty() || m.fx.isEmpty()) return false
        if (m.vx.size != m.vy.size || m.vx.size != m.vz.size) return false
        if (m.fx.size != m.fy.size || m.fx.size != m.fz.size) return false

        val vCount = m.vx.size
        var bad = 0
        val check = minOf(m.fx.size, 2000) // sample for speed
        for (i in 0 until check) {
            val a = m.fx[i]
            val b = m.fy[i]
            val c = m.fz[i]
            if (a !in 0 until vCount || b !in 0 until vCount || c !in 0 until vCount) bad++
        }
        if (bad > check / 4) return false

        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE
        var minZ = Int.MAX_VALUE
        var maxZ = Int.MIN_VALUE
        for (i in m.vx.indices) {
            val x = m.vx[i]; val y = m.vy[i]; val z = m.vz[i]
            if (x < minX) minX = x; if (x > maxX) maxX = x
            if (y < minY) minY = y; if (y > maxY) maxY = y
            if (z < minZ) minZ = z; if (z > maxZ) maxZ = z
        }
        val spanX = abs((maxX - minX).toLong())
        val spanY = abs((maxY - minY).toLong())
        val spanZ = abs((maxZ - minZ).toLong())
        // if it's basically a point, it's not a real model (or we decoded wrong)
        if (spanX + spanY + spanZ < 10) return false

        return true
    }
}
