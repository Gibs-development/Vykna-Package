package com.vykna.importer.source

import com.vykna.importer.core.Log
import com.vykna.importer.defs.seq.ByteStream
import com.vykna.importer.defs.seq.Seq667
import com.vykna.importer.defs.seq.Seq667Decoder
import java.io.File
import kotlin.math.min

object Seq667DatLoader {

    /**
     * Supports the common "combined" format:
     *  u2 count
     *  u2 size[count]
     *  data blobs concatenated
     *
     * Falls back to sequential parsing if size-table validation fails.
     */
    fun load(seqDat: File): Map<Int, Seq667> {
        val data = seqDat.readBytes()
        if (data.size < 4) return emptyMap()

        fun u2(off: Int): Int {
            if (off + 1 >= data.size) return 0
            return ((data[off].toInt() and 0xFF) shl 8) or (data[off + 1].toInt() and 0xFF)
        }

        val count = u2(0)
        if (count <= 0) return emptyMap()

        // Try size-table parse
        val sizesStart = 2
        val sizesBytes = count * 2
        val blobsStart = sizesStart + sizesBytes

        var canUseSizeTable = blobsStart <= data.size
        var total = 0

        val sizes = IntArray(count)
        if (canUseSizeTable) {
            var p = sizesStart
            for (i in 0 until count) {
                sizes[i] = u2(p)
                total += sizes[i]
                p += 2
                if (total < 0) { // overflow guard (paranoia)
                    canUseSizeTable = false
                    break
                }
            }
            if (blobsStart + total > data.size) canUseSizeTable = false
        }

        fun score(map: Map<Int, Seq667>): Int {
            var s = 0
            for (seq in map.values) {
                if (seq.frameIds.isNotEmpty() && seq.frameIds[0] != -1) s += 5
                if (seq.frameIds.size > 1) s += 2
                // reward having plausible deps
                var nonZeroFiles = 0
                for (packed in seq.frameIds) {
                    val file = (packed ushr 16) and 0xFFFF
                    if (file != 0 && file != 0xFFFF) nonZeroFiles++
                }
                s += min(nonZeroFiles, 10)
            }
            return s
        }

        fun stub(id: Int) = Seq667(id = id, frameCount = 1, frameIds = intArrayOf(-1), frameLengths = intArrayOf(-1))

        // Attempt size-table parse (if feasible)
        val sizeTableOut: Map<Int, Seq667>? = if (canUseSizeTable) {
            val out = HashMap<Int, Seq667>(count)
            var off = blobsStart
            for (id in 0 until count) {
                val sz = sizes[id]
                val end = min(off + sz, data.size)
                val slice = if (sz > 0 && off < end) data.copyOfRange(off, end) else ByteArray(0)
                off += sz

                val seq = try {
                    Seq667Decoder.decode(ByteStream(slice), id) { oldFileId -> oldFileId }
                } catch (t: Throwable) {
                    stub(id)
                }
                out[id] = seq
            }
            out
        } else null

        // Attempt sequential parse (works for classic formats without a size-table)
        val sequentialOut = HashMap<Int, Seq667>(count)
        val stream = ByteStream(data)
        stream.pos = 2
        for (id in 0 until count) {
            val seq = try {
                Seq667Decoder.decode(stream, id) { oldFileId -> oldFileId }
            } catch (_: Throwable) {
                break
            }
            sequentialOut[id] = seq
        }

        // Decide which parse produced the most usable sequences.
        val sizeScore = sizeTableOut?.let { score(it) } ?: -1
        val seqScore = score(sequentialOut)

        val chosen = if (sizeTableOut != null && sizeScore >= seqScore) {
            Log.info("Seq667: chose size-table parse (score=$sizeScore) over sequential (score=$seqScore). count=$count")
            sizeTableOut
        } else {
            if (sizeTableOut != null) {
                Log.warn("Seq667: chose sequential parse (score=$seqScore) over size-table (score=$sizeScore). count=$count")
            } else {
                Log.warn("Seq667: size-table not usable; using sequential parse (score=$seqScore). count=$count")
            }
            sequentialOut
        }

        // Ensure all ids exist for UI list.
        val out = HashMap<Int, Seq667>(count)
        for (id in 0 until count) out[id] = chosen[id] ?: stub(id)
        return out
    }
}
