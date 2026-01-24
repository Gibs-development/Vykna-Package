package com.vykna.importer

import com.vykna.importer.core.Log
import com.vykna.importer.defs.seq.Seq667
import com.vykna.importer.source.*
import com.vykna.importer.source.defs.ItemLite
import com.vykna.importer.source.defs.NpcLite
import com.vykna.importer.source.defs.SpotAnimLite
import java.io.File

class AppController {

    data class Loaded(
        val fileIndex: FileIndex,
        val npcs: List<NpcLite>,
        val items: List<ItemLite>,
        val spotanims: List<SpotAnimLite>,
        val seqs: Map<Int, Seq667>
    )


    fun loadSource667(rootDir: File): Loaded {
        val layout = Source667Layout.detect(rootDir)
        Log.info("Detected 667 layout at ${layout.root.absolutePath}")

        val files = FileIndex.build(layout)
        Log.info("Indexed models=${files.models.size} frameFiles=${files.frames.size}")

        val npcTable = DatIdx.load(layout.npcDat, layout.npcIdx)
        val npcs = ArrayList<NpcLite>(npcTable.count())
        for (id in 0 until npcTable.count()) {
            val bytes = npcTable.slice(id)
            if (bytes.isEmpty()) continue
            try {
                npcs.add(Decoders.decodeNpcLite(id, bytes))
            } catch (e: InStream.EOF) {
                Log.warn("NPC decode hit EOF (likely opcode/size mismatch) id=$id bytes=${bytes.size} — skipping")
            } catch (t: Throwable) {
                Log.warn("NPC decode failed id=$id bytes=${bytes.size}: ${t.message}")
            }
        }


        val objTable = DatIdx.load(layout.objDat, layout.objIdx)
        val items = ArrayList<ItemLite>(objTable.count())
        var itemWarns = 0
        for (id in 0 until objTable.count()) {
            val bytes = objTable.slice(id)
            if (bytes.isEmpty()) continue
            try {
                items.add(Decoders.decodeItemLite(id, bytes))
            } catch (e: InStream.EOF) {
                if (itemWarns++ < 25) Log.warn("Item decode hit EOF id=$id bytes=${bytes.size} — skipping")
            } catch (t: Throwable) {
                if (itemWarns++ < 25) Log.warn("Item decode failed id=$id bytes=${bytes.size}: ${t.message}")
            }
        }


        val spotanims = Decoders.decodeSpotAnimList(layout.spotAnimDat.readBytes())

        val seqs = Seq667DatLoader.load(layout.seqDat)
        Log.info("Loaded seqs=${seqs.size}")

        Log.info("Loaded npcs=${npcs.size} items=${items.size} spotanims=${spotanims.size}")
        return Loaded(files, npcs, items, spotanims, seqs)
    }
}
