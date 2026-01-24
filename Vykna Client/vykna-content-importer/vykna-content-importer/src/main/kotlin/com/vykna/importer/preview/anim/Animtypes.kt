package com.vykna.importer.preview.anim

data class AnimBase(
    val transformTypes: IntArray,
    val groups: Array<IntArray>
)

data class AnimFrame(
    val base: AnimBase,
    val count: Int,
    val indices: IntArray,
    val x: IntArray,
    val y: IntArray,
    val z: IntArray
)

data class FrameGroup(
    val base: AnimBase,
    val frames: Map<Int, AnimFrame>
)
