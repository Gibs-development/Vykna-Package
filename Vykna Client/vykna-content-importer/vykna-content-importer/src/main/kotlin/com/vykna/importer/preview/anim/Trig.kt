package com.vykna.importer.preview.anim

import kotlin.math.cos
import kotlin.math.sin

object Trig {
    val SINE = IntArray(2048)
    val COSINE = IntArray(2048)

    init {
        for (i in 0 until 2048) {
            SINE[i] = (sin(i * Math.PI / 1024.0) * 65536.0).toInt()
            COSINE[i] = (cos(i * Math.PI / 1024.0) * 65536.0).toInt()
        }
    }
}
