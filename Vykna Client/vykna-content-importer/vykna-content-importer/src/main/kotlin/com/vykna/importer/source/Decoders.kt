package com.vykna.importer.source

import com.vykna.importer.source.defs.ItemLite
import com.vykna.importer.source.defs.NpcLite
import com.vykna.importer.source.defs.SpotAnimLite
import java.nio.charset.Charset

class InStream(private val data: ByteArray) {
    var pos = 0
    private val len = data.size

    fun hasRemaining(): Boolean = pos < len

    fun u1(): Int {
        if (pos >= len) throw EOF()
        return data[pos++].toInt() and 0xFF
    }

    fun u2(): Int {
        if (pos + 1 >= len) throw EOF()
        val v = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
        pos += 2
        return v
    }

    /**
     * RuneScape strings are usually CP-1252, but some dumps are exported as UTF-8.
     * We decode both and choose the "least garbled" result, then strip non-printable control chars.
     */
    fun str(): String {
        if (pos >= len) return ""
        val start = pos
        while (pos < len && data[pos].toInt() != 0) pos++
        val raw = if (pos > start) data.copyOfRange(start, pos) else ByteArray(0)
        if (pos < len && data[pos].toInt() == 0) pos++ // consume null if present
        if (raw.isEmpty()) return ""

        val cp = String(raw, Charset.forName("Cp1252"))
        val utf = String(raw, Charsets.UTF_8)

        fun score(s: String): Int {
            // Penalize common mojibake patterns when UTF-8 bytes are mis-decoded as CP1252.
            val mojibakePenalty = listOf('Ã', 'Â', 'â', '�').sumOf { ch -> s.count { it == ch } } * 10
            val ctrlPenalty = s.count { it.code in 0..8 || it.code in 11..12 || it.code in 14..31 } * 5
            val printableBonus = s.count { it == '\n' || it == '\r' || it == '\t' || it.code >= 32 }
            return printableBonus - mojibakePenalty - ctrlPenalty
        }

        val chosen = if (score(utf) > score(cp)) utf else cp

        // Strip control chars (keep \n/\r/\t).
        return buildString(chosen.length) {
            for (c in chosen) {
                if (c == '\n' || c == '\r' || c == '\t' || c.code >= 32) append(c)
            }
        }
    }

    fun skip(n: Int) {
        pos = (pos + n).coerceAtMost(len)
    }

    class EOF : RuntimeException()
}


object Decoders {

    fun decodeNpcLite(id: Int, bytes: ByteArray): NpcLite {
        val s = InStream(bytes)

        var name = "npc_$id"
        var examine: String? = null
        val actions: Array<String?> = arrayOfNulls(5)
        var models = intArrayOf()
        var stand = -1
        var walk = -1
        var turn180 = -1
        var turn90R = -1
        var walkBack = -1
        var walkLeft = -1
        var walkRight = -1
        var combat = 0
        var size = 1
        var children = intArrayOf()

        // read until opcode 0 OR until we run out of bytes
        while (s.hasRemaining()) {
            val op = try { s.u1() } catch (e: InStream.EOF) { break }
            if (op == 0) break

            when (op) {
                1 -> {
                    val count = try { s.u1() } catch (e: InStream.EOF) { break }
                    val tmp = IntArray(count)
                    for (i in 0 until count) {
                        tmp[i] = try { s.u2() } catch (e: InStream.EOF) { 0 }
                    }
                    models = tmp
                }
                2 -> {
                    // str() is already EOF-safe in your patched InStream
                    name = s.str()
                }
                3 -> {
                    // examine/description (common in most revisions)
                    examine = s.str()
                }
                13 -> stand = try { s.u2() } catch (e: InStream.EOF) { stand }
                14 -> walk  = try { s.u2() } catch (e: InStream.EOF) { walk }

                // Common movement variants in most RS formats.
                15 -> turn180 = try { s.u2() } catch (e: InStream.EOF) { turn180 }
                16 -> turn90R = try { s.u2() } catch (e: InStream.EOF) { turn90R }

                17 -> {
                    // walk variants (walk, back, left, right)
                    val a = try { s.u2() } catch (_: InStream.EOF) { -1 }
                    val b = try { s.u2() } catch (_: InStream.EOF) { -1 }
                    val c = try { s.u2() } catch (_: InStream.EOF) { -1 }
                    val d = try { s.u2() } catch (_: InStream.EOF) { -1 }
                    if (a >= 0) walk = a
                    walkBack = b
                    walkLeft = c
                    walkRight = d
                }

                95 -> combat = try { s.u2() } catch (e: InStream.EOF) { combat }

                in 30..34 -> {
                    val idx = op - 30
                    if (idx in 0..4) actions[idx] = s.str() else s.str()
                }

                12 -> {
                    // size (u1)
                    size = try { s.u1() } catch (e: InStream.EOF) { size }
                }

                40 -> {
                    val c = try { s.u1() } catch (e: InStream.EOF) { break }
                    s.skip(c * 4) // recolor pairs
                }

                60 -> {
                    val c = try { s.u1() } catch (e: InStream.EOF) { break }
                    s.skip(c * 2) // extra models
                }

                106, 118 -> {
                    // transforms
                    val varbit = try { s.u2() } catch (e: InStream.EOF) { break }
                    val varp = try { s.u2() } catch (e: InStream.EOF) { break }

                    // optional extra child for opcode 118
                    val extraChild = if (op == 118) {
                        try { s.u2() } catch (e: InStream.EOF) { 65535 }
                    } else 65535

                    val count = try { s.u1() } catch (e: InStream.EOF) { break }
                    val tmp = IntArray(count + 1 + if (op == 118) 1 else 0)
                    // children 0..count
                    for (i in 0..count) {
                        val v = try { s.u2() } catch (e: InStream.EOF) { 65535 }
                        tmp[i] = if (v == 65535) -1 else v
                    }
                    if (op == 118) {
                        tmp[count + 1] = if (extraChild == 65535) -1 else extraChild
                    }

                    children = tmp
                    // (varbit/varp are useful later for export; keep for now as "known but unused")
                    @Suppress("UNUSED_VARIABLE")
                    val _varbit = if (varbit == 65535) -1 else varbit
                    @Suppress("UNUSED_VARIABLE")
                    val _varp = if (varp == 65535) -1 else varp
                }

                93, 97, 98, 99, 100, 101, 102, 103, 107, 109 -> {
                    // Skip common fields safely.
                    // Note: this is still a "best effort" list; we expand when needed.
                    try {
                        if (op == 93 || op == 99 || op == 107 || op == 109) s.u1() else s.u2()
                    } catch (_: InStream.EOF) {
                        break
                    }
                }

                else -> {
                    // Unknown opcode for this dump - stop reading THIS npc safely.
                    // (We can add opcode support later if we need those fields.)
                    break
                }
            }
        }

        return NpcLite(
            id = id,
            name = name,
            examine = examine,
            actions = actions,
            models = models,
            standAnim = stand,
            walkAnim = walk,
            turn180Anim = turn180,
            turn90RightAnim = turn90R,
            walkBackAnim = walkBack,
            walkLeftAnim = walkLeft,
            walkRightAnim = walkRight,
            combatLevel = combat,
            size = size,
            childrenIDs = children
        )
    }


    fun decodeItemLite(id: Int, bytes: ByteArray): ItemLite {
        val s = InStream(bytes)

        var name = "item_$id"
        var model = -1

        while (s.hasRemaining()) {
            val op = try { s.u1() } catch (e: InStream.EOF) { break }
            if (op == 0) break

            when (op) {
                1 -> model = try { s.u2() } catch (e: InStream.EOF) { model }
                2 -> name = s.str()

                // "mostly numeric" ops - best effort skip
                4, 5, 6, 7, 8, 11, 12, 13, 14, 16,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                78, 79,
                90, 91, 92, 93, 94, 95, 96 -> {
                    // some of these are u1 in certain formats,
                    // but u2 skip is "good enough" for lite browsing
                    try { s.u2() } catch (_: InStream.EOF) { break }
                }

                in 30..34 -> s.str() // ground actions
                in 35..39 -> s.str() // inventory actions

                40 -> { // recolor
                    val c = try { s.u1() } catch (e: InStream.EOF) { break }
                    s.skip(c * 4)
                }

                41 -> { // retexture
                    val c = try { s.u1() } catch (e: InStream.EOF) { break }
                    s.skip(c * 4)
                }

                else -> {
                    // Unknown opcode for this dump - stop reading THIS item safely.
                    break
                }
            }
        }

        return ItemLite(id, name, model)
    }


    fun decodeSpotAnimList(bytes: ByteArray): List<SpotAnimLite> {
        val s = InStream(bytes)
        val count = s.u2()
        val out = ArrayList<SpotAnimLite>(count)

        for (id in 0 until count) {
            var model = -1
            var seq = -1

            while (true) {
                val op = s.u1()
                if (op == 0) break
                when (op) {
                    1 -> model = s.u2()
                    2 -> seq = s.u2()
                    4,5,6 -> s.u2()
                    7,8 -> s.u1()
                    40 -> { val c = s.u1(); s.skip(c * 4) }
                    41 -> { val c = s.u1(); s.skip(c * 4) }
                    else -> break
                }
            }

            out.add(SpotAnimLite(id, model, seq))
        }

        return out
    }
}
