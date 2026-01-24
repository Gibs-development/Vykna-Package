package com.vykna.importer.preview.anim

class AnimStream(private val data: ByteArray) {
    var pos = 0

    fun u1(): Int {
        if (pos >= data.size) throw EOFException()
        return data[pos++].toInt() and 0xFF
    }

    fun u2(): Int {
        val hi = u1()
        val lo = u1()
        return (hi shl 8) or lo
    }

    fun s2(): Int {
        var v = u2()
        if (v > 32767) v -= 65536
        return v
    }

    // Matches your client Stream.readShort2()
    fun short2(): Int {
        var v = u2()
        if (v > 60000) v = -65535 + v
        return v
    }

    class EOFException : RuntimeException()
}

object FrameGroupDecoder {

    fun decode(raw: ByteArray): FrameGroup {
        val s = AnimStream(raw)

        // --- Base (Class18) ---
        val baseCount = s.u2()
        val types = IntArray(baseCount)
        val groups = Array(baseCount) { IntArray(0) }

        for (i in 0 until baseCount) types[i] = s.u2()
        for (i in 0 until baseCount) groups[i] = IntArray(s.u2())
        for (i in 0 until baseCount) {
            for (j in groups[i].indices) {
                groups[i][j] = s.u2()
            }
        }

        val base = AnimBase(types, groups)

        // --- Frames (Class36.load) ---
        val frameCount = s.u2()
        val out = HashMap<Int, AnimFrame>(frameCount)

        for (f in 0 until frameCount) {
            val frameId = s.u2()
            val transCount = s.u1()

            val indices = IntArray(transCount)
            val x = IntArray(transCount)
            val y = IntArray(transCount)
            val z = IntArray(transCount)

            var lastIndex = -1
            var used = 0

            for (i in 0 until transCount) {
                val flags = s.u1()
                if (flags <= 0) continue

                // Insert implicit zeros for missing type==0 entries (same as client)
                val idx = i
                if (base.transformTypes[idx] != 0) {
                    for (j in idx - 1 downTo lastIndex + 1) {
                        if (base.transformTypes[j] == 0) {
                            indices[used] = j
                            x[used] = 0
                            y[used] = 0
                            z[used] = 0
                            used++
                            break
                        }
                    }
                }

                indices[used] = idx
                val defaultVal = if (base.transformTypes[idx] == 3) 128 else 0
                x[used] = if ((flags and 1) != 0) s.short2() else defaultVal
                y[used] = if ((flags and 2) != 0) s.short2() else defaultVal
                z[used] = if ((flags and 4) != 0) s.short2() else defaultVal

                lastIndex = idx
                used++
            }

            out[frameId] = AnimFrame(
                base = base,
                count = used,
                indices = indices.copyOf(used),
                x = x.copyOf(used),
                y = y.copyOf(used),
                z = z.copyOf(used)
            )
        }

        return FrameGroup(base, out)
    }
}
