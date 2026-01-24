package com.vykna.importer.preview

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

object GzipUtil {
    private fun readAll(input: java.io.InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        while (true) {
            val n = input.read(buf)
            if (n <= 0) break
            out.write(buf, 0, n)
        }
        return out.toByteArray()
    }

    /** Strict gzip decode (throws if not gzip). */
    fun gunzip(bytes: ByteArray): ByteArray {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { gz ->
            return readAll(gz)
        }
    }

    /** zlib/deflate decode (throws if not zlib/deflate). */
    fun inflate(bytes: ByteArray): ByteArray {
        InflaterInputStream(ByteArrayInputStream(bytes)).use { inf ->
            return readAll(inf)
        }
    }

    /**
     * Dumps are inconsistent: sometimes raw model bytes, sometimes gzip/zlib,
     * and sometimes a small prefix before the gzip header.
     *
     * This returns a list of candidate payloads to try decoding.
     */
    fun expandCandidates(bytes: ByteArray): List<ByteArray> {
        val out = ArrayList<ByteArray>(6)
        out.add(bytes)

        fun tryGzipAt(off: Int) {
            if (off < 0 || off >= bytes.size - 2) return
            if ((bytes[off].toInt() and 0xFF) != 0x1F) return
            if ((bytes[off + 1].toInt() and 0xFF) != 0x8B) return
            try {
                out.add(gunzip(bytes.copyOfRange(off, bytes.size)))
            } catch (_: Throwable) {
                // ignore
            }
        }

        fun tryZlibAt(off: Int) {
            if (off < 0 || off >= bytes.size - 2) return
            val b0 = bytes[off].toInt() and 0xFF
            val b1 = bytes[off + 1].toInt() and 0xFF
            // Common zlib headers: 0x78 0x9C, 0x78 0xDA, 0x78 0x01
            if (b0 != 0x78) return
            if (b1 != 0x9C && b1 != 0xDA && b1 != 0x01) return
            try {
                out.add(inflate(bytes.copyOfRange(off, bytes.size)))
            } catch (_: Throwable) {
                // ignore
            }
        }

        // Try direct gzip/zlib first
        tryGzipAt(0)
        tryZlibAt(0)

        // Try common small prefixes (2/4 bytes) and also scan the first 32 bytes for a header.
        tryGzipAt(2)
        tryGzipAt(4)
        tryZlibAt(2)
        tryZlibAt(4)
        val scan = minOf(32, bytes.size - 2)
        for (i in 0 until scan) {
            tryGzipAt(i)
            tryZlibAt(i)
        }

        // If we decoded something that is *still* gz, try one nested pass.
        val nested = out.toList()
        for (cand in nested) {
            if (cand.size > 2 && (cand[0].toInt() and 0xFF) == 0x1F && (cand[1].toInt() and 0xFF) == 0x8B) {
                try {
                    out.add(gunzip(cand))
                } catch (_: Throwable) {}
            }
        }

        return out.distinctBy { it.size }.distinctBy { it.take(16).toByteArray().contentHashCode() }
    }
}
