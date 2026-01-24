package com.vykna.importer.defs.seq

data class Seq667(
    var id: Int = -1,
    var frameCount: Int = 0,
    var frameIds: IntArray = intArrayOf(),     // packed file<<16|frame
    var frameLengths: IntArray = intArrayOf(), // ticks (667 often UByte)
)

object Seq667Decoder {

    fun decode(stream: ByteStream, id: Int, frameFileRemap: (oldFileId: Int) -> Int): Seq667 {
        val seq = Seq667(id = id)

        while (true) {
            val opcode = stream.u1()
            if (opcode == 0) break

            when (opcode) {
                1 -> {
                    val count = stream.u2()
                    seq.frameCount = count
                    // 667/OSRS dumps vary a lot. Try the most common layouts:
                    //  A) packed dword per frame (file<<16|frame), then u1 lengths
                    //  B) u2 lengths[], u2 frames[], u2 files[]
                    //  C) u2 frames[], u2 files[], u2 lengths[]
                    seq.frameIds = IntArray(count)
                    seq.frameLengths = IntArray(count)

                    val startPos = stream.pos

                    data class Candidate(val ids: IntArray, val lens: IntArray, val endPos: Int)

                    fun plausible(ids: IntArray, lens: IntArray): Int {
                        if (ids.isEmpty()) return -9999
                        var score = 0
                        var nonZero = 0
                        var goodLen = 0
                        for (i in ids.indices) {
                            val packed = ids[i]
                            val file = (packed ushr 16) and 0xFFFF
                            val frame = packed and 0xFFFF
                            if (file != 0 && file != 0xFFFF && frame != 0xFFFF) nonZero++
                            val l = lens.getOrElse(i) { 1 }
                            if (l in 1..255) goodLen++
                        }
                        score += nonZero * 3
                        score += goodLen
                        return score
                    }

                    fun tryA(): Candidate? {
                        stream.pos = startPos
                        val need = count * 4 + count
                        if (stream.remaining() < need) return null
                        val ids = IntArray(count)
                        val lens = IntArray(count)
                        for (i in 0 until count) {
                            val packed = stream.u4()
                            val file = (packed ushr 16) and 0xFFFF
                            val frame = packed and 0xFFFF
                            val newFile = frameFileRemap(file)
                            ids[i] = (newFile shl 16) or frame
                        }
                        for (i in 0 until count) lens[i] = stream.u1()
                        return Candidate(ids, lens, stream.pos)
                    }

                    fun tryB(): Candidate? {
                        stream.pos = startPos
                        val need = count * 2 * 3
                        if (stream.remaining() < need) return null
                        val lens = IntArray(count)
                        val frames = IntArray(count)
                        val files = IntArray(count)
                        for (i in 0 until count) lens[i] = stream.u2()
                        for (i in 0 until count) frames[i] = stream.u2()
                        for (i in 0 until count) files[i] = stream.u2()
                        val ids = IntArray(count)
                        for (i in 0 until count) {
                            val newFile = frameFileRemap(files[i])
                            ids[i] = (newFile shl 16) or frames[i]
                        }
                        return Candidate(ids, lens, stream.pos)
                    }

                    fun tryC(): Candidate? {
                        stream.pos = startPos
                        val need = count * 2 * 3
                        if (stream.remaining() < need) return null
                        val frames = IntArray(count)
                        val files = IntArray(count)
                        val lens = IntArray(count)
                        for (i in 0 until count) frames[i] = stream.u2()
                        for (i in 0 until count) files[i] = stream.u2()
                        for (i in 0 until count) lens[i] = stream.u2()
                        val ids = IntArray(count)
                        for (i in 0 until count) {
                            val newFile = frameFileRemap(files[i])
                            ids[i] = (newFile shl 16) or frames[i]
                        }
                        return Candidate(ids, lens, stream.pos)
                    }

                    val candidates = listOfNotNull(tryA(), tryB(), tryC())
                    val best = candidates.maxByOrNull { plausible(it.ids, it.lens) }

                    if (best != null) {
                        seq.frameIds = best.ids
                        seq.frameLengths = best.lens
                        stream.pos = best.endPos
                    } else {
                        // fall back to a minimal stub
                        seq.frameCount = 1
                        seq.frameIds = intArrayOf(-1)
                        seq.frameLengths = intArrayOf(-1)
                    }
                }
                12 -> stream.u4() // ignore dword (matches many 667 snippets)
                else -> {
                    // TODO: add other opcode handlers as needed
                }
            }
        }

        if (seq.frameCount == 0) {
            seq.frameCount = 1
            seq.frameIds = intArrayOf(-1)
            seq.frameLengths = intArrayOf(-1)
        }

        return seq
    }
}

class ByteStream(private val data: ByteArray) {
    var pos: Int = 0

    fun remaining(): Int = data.size - pos
    fun hasRemaining(): Boolean = pos < data.size

    fun u1(): Int {
        if (pos >= data.size) throw EOF()
        return data[pos++].toInt() and 0xFF
    }

    fun u2(): Int {
        if (pos + 1 >= data.size) throw EOF()
        val v = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
        pos += 2
        return v
    }

    fun u4(): Int {
        if (pos + 3 >= data.size) throw EOF()
        val v = ((data[pos].toInt() and 0xFF) shl 24) or
                ((data[pos + 1].toInt() and 0xFF) shl 16) or
                ((data[pos + 2].toInt() and 0xFF) shl 8) or
                (data[pos + 3].toInt() and 0xFF)
        pos += 4
        return v
    }

    class EOF : RuntimeException()
}

