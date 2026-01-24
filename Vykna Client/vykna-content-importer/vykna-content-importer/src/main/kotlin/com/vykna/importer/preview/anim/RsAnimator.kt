package com.vykna.importer.preview.anim

import com.vykna.importer.preview.RsModel

object RsAnimator {

    private var cx = 0
    private var cy = 0
    private var cz = 0

    fun apply(model: RsModel, frame: AnimFrame) {
        val vGroups = model.vertexGroups() ?: return

        model.resetVertices()

        cx = 0; cy = 0; cz = 0

        for (t in 0 until frame.count) {
            val idx = frame.indices[t]
            val type = frame.base.transformTypes[idx]
            val boneGroups = frame.base.groups[idx] // values are groupIds into vertexGroups
            transform(model, vGroups, type, boneGroups, frame.x[t], frame.y[t], frame.z[t])
        }
    }

    private fun transform(
        model: RsModel,
        vGroups: Array<IntArray>,
        type: Int,
        groupIds: IntArray,
        j: Int, k: Int, l: Int
    ) {
        if (type == 0) {
            var total = 0
            var sx = 0
            var sy = 0
            var sz = 0

            for (g in groupIds) {
                if (g !in vGroups.indices) continue
                val verts = vGroups[g]
                total += verts.size
                for (v in verts) {
                    sx += model.vx[v]
                    sy += model.vy[v]
                    sz += model.vz[v]
                }
            }

            if (total > 0) {
                cx = sx / total + j
                cy = sy / total + k
                cz = sz / total + l
            } else {
                cx = j; cy = k; cz = l
            }
            return
        }

        if (type == 1) {
            for (g in groupIds) {
                if (g !in vGroups.indices) continue
                for (v in vGroups[g]) {
                    model.vx[v] += j
                    model.vy[v] += k
                    model.vz[v] += l
                }
            }
            return
        }

        if (type == 2) {
            val angX = (j and 0xFF) * 8
            val angY = (k and 0xFF) * 8
            val angZ = (l and 0xFF) * 8

            val sinZ = Trig.SINE[angZ]; val cosZ = Trig.COSINE[angZ]
            val sinX = Trig.SINE[angX]; val cosX = Trig.COSINE[angX]
            val sinY = Trig.SINE[angY]; val cosY = Trig.COSINE[angY]

            for (g in groupIds) {
                if (g !in vGroups.indices) continue
                for (v in vGroups[g]) {
                    var x = model.vx[v] - cx
                    var y = model.vy[v] - cy
                    var z = model.vz[v] - cz

                    if (angZ != 0) {
                        val nx = (y * sinZ + x * cosZ) shr 16
                        y = (y * cosZ - x * sinZ) shr 16
                        x = nx
                    }

                    if (angX != 0) {
                        val ny = (y * cosX - z * sinX) shr 16
                        z = (y * sinX + z * cosX) shr 16
                        y = ny
                    }

                    if (angY != 0) {
                        val nx = (z * sinY + x * cosY) shr 16
                        z = (z * cosY - x * sinY) shr 16
                        x = nx
                    }

                    model.vx[v] = x + cx
                    model.vy[v] = y + cy
                    model.vz[v] = z + cz
                }
            }
            return
        }

        if (type == 3) {
            for (g in groupIds) {
                if (g !in vGroups.indices) continue
                for (v in vGroups[g]) {
                    model.vx[v] = cx + ((model.vx[v] - cx) * j / 128)
                    model.vy[v] = cy + ((model.vy[v] - cy) * k / 128)
                    model.vz[v] = cz + ((model.vz[v] - cz) * l / 128)
                }
            }
        }
    }
}
