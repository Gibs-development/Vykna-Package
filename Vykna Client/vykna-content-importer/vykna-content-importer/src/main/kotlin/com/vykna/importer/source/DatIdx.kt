package com.vykna.importer.source

import java.io.File
import kotlin.math.min

class DatIdx(
    private val dat: ByteArray,
    private val offsets: IntArray
) {
    fun count(): Int = offsets.size

    fun slice(id: Int): ByteArray {
        val start = offsets[id]
        val end = if (id + 1 < offsets.size) offsets[id + 1] else dat.size
        if (start >= dat.size || end <= start) return ByteArray(0)
        return dat.copyOfRange(start, min(end, dat.size))
    }

    companion object {
        private fun readU2(arr: ByteArray, off: Int): Int {
            if (off + 1 >= arr.size) return 0
            return ((arr[off].toInt() and 0xFF) shl 8) or (arr[off + 1].toInt() and 0xFF)
        }

        fun load(datFile: File, idxFile: File): DatIdx {
            val dat = datFile.readBytes()
            val idx = idxFile.readBytes()

            var p = 0
            fun u2(): Int {
                val v = readU2(idx, p)
                p += 2
                return v
            }

            val count = u2()
            val sizes = IntArray(count) { u2() }

            // Heuristic:
            // Some dumps have DAT starting with u2 count, others start immediately.
            // If dat[0..1] looks like the same count as idx, start at 2, else 0.
            val datHeaderCount = readU2(dat, 0)
            val datStart = if (datHeaderCount == count) 2 else 0

            val offsets = IntArray(count)
            var off = datStart
            for (i in 0 until count) {
                offsets[i] = off
                off += sizes[i]
            }

            return DatIdx(dat, offsets)
        }
    }
}
