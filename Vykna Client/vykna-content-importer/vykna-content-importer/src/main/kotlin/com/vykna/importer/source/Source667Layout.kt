package com.vykna.importer.source

import java.io.File

data class Source667Layout(
    val root: File,
    val npcDat: File,
    val npcIdx: File,
    val objDat: File,
    val objIdx: File,
    val seqDat: File,
    val spotAnimDat: File,
    val idkDat: File?,

    val modelDirs: List<File>,
    val frameDir: File
) {
    companion object {
        fun detect(root: File): Source667Layout {
            fun req(name: String): File {
                val f = File(root, name)
                require(f.exists()) { "Missing $name in ${root.absolutePath}" }
                return f
            }

            val modelDirs = listOf(
                File(root, "667 Npc Models"),
                File(root, "667 Item Models"),
                File(root, "667 GFX Models"),
                File(root, "667 Player Chat Models"),
                File(root, "667 Npc Chat Models"),
                File(root, "667 Characters"),
            ).filter { it.exists() && it.isDirectory }

            val frames = File(root, "667 Animations")
            require(frames.exists() && frames.isDirectory) { "Missing 667 Animations folder" }

            return Source667Layout(
                root = root,
                npcDat = req("667npc.dat"),
                npcIdx = req("667npc.idx"),
                objDat = req("667obj.dat"),
                objIdx = req("667obj.idx"),
                seqDat = req("667seq.dat"),
                spotAnimDat = req("667spotanim.dat"),
                idkDat = File(root, "667idk.dat").takeIf { it.exists() },
                modelDirs = modelDirs,
                frameDir = frames
            )
        }
    }
}
