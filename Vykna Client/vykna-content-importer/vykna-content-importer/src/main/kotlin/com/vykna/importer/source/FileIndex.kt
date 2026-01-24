package com.vykna.importer.source

import java.io.File

class FileIndex(
    /** Primary model file chosen by directory priority. */
    val models: Map<Int, File>,

    /** All model file candidates found across the dump (some ids exist in multiple folders). */
    val modelCandidates: Map<Int, List<File>>,

    val frames: Map<Int, File>
) {

    fun hasModel(id: Int): Boolean = modelCandidates.containsKey(id)

    fun getModelCandidates(id: Int): List<File> = modelCandidates[id] ?: emptyList()

    companion object {
        fun build(layout: Source667Layout): FileIndex {
            val modelPrimary = HashMap<Int, File>()
            val modelCandidates = HashMap<Int, MutableList<File>>()

            fun parseLeadingInt(fileName: String): Int? {
                // Robust parsing for dump filenames like:
                //  - 58940.gz
                //  - 58940.dat.gz
                //  - 58940
                //  - 58940_someextra.gz
                val m = Regex("^(\\d+)").find(fileName) ?: return null
                return m.groupValues[1].toIntOrNull()
            }

            fun indexDirRecursive(dir: File) {
                if (!dir.exists() || !dir.isDirectory) return
                dir.walkTopDown().forEach { f ->
                    if (!f.isFile) return@forEach
                    val id = parseLeadingInt(f.name) ?: return@forEach
                    modelCandidates.getOrPut(id) { mutableListOf() }.add(f)
                    modelPrimary.putIfAbsent(id, f)
                }
            }

            // Keep directory order as priority for "primary", but also retain all candidates.
            for (dir in layout.modelDirs) {
                indexDirRecursive(dir)
            }

            val frameMap = HashMap<Int, File>()
            // Frames are usually flat, but some dumps nest them. Handle both.
            layout.frameDir.walkTopDown().forEach { f ->
                if (!f.isFile) return@forEach
                val id = parseLeadingInt(f.name) ?: return@forEach
                frameMap[id] = f
            }

            val frozenCandidates = modelCandidates.mapValues { it.value.toList() }
            return FileIndex(modelPrimary, frozenCandidates, frameMap)
        }
    }
}
