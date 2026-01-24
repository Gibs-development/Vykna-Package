package com.vykna.importer.preview

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import kotlin.math.*

object WireframeRenderer {

    fun render(
        canvas: Canvas,
        model: RsModel,
        yawDeg: Double,
        pitchDeg: Double,
        zoom: Double,
        flipY: Boolean = false,
        swapYZ: Boolean = false,
    ) {
        val g = canvas.graphicsContext2D
        val w = canvas.width
        val h = canvas.height

        // background + visible wire
        g.fill = Color.rgb(20, 20, 20)
        g.fillRect(0.0, 0.0, w, h)
        g.stroke = Color.rgb(220, 220, 220)
        g.lineWidth = 1.0

        if (model.vertexCount == 0 || model.faceCount == 0) {
            // draw a cross so you know renderer runs
            g.strokeLine(w/2 - 10, h/2, w/2 + 10, h/2)
            g.strokeLine(w/2, h/2 - 10, w/2, h/2 + 10)
            return
        }

        val yaw = Math.toRadians(yawDeg)
        val pitch = Math.toRadians(pitchDeg)
        val cy = cos(yaw);  val sy = sin(yaw)
        val cp = cos(pitch); val sp = sin(pitch)

        // Center model using bounds
        var minX = Int.MAX_VALUE; var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE; var maxY = Int.MIN_VALUE
        var minZ = Int.MAX_VALUE; var maxZ = Int.MIN_VALUE
        for (i in 0 until model.vertexCount) {
            val x = model.vx[i]; val y = model.vy[i]; val z = model.vz[i]
            if (x < minX) minX = x; if (x > maxX) maxX = x
            if (y < minY) minY = y; if (y > maxY) maxY = y
            if (z < minZ) minZ = z; if (z > maxZ) maxZ = z
        }
        val cx0 = (minX + maxX) / 2.0
        val cy0 = (minY + maxY) / 2.0
        val cz0 = (minZ + maxZ) / 2.0

        // Rotate all vertices, track rotated bounds
        val rx = DoubleArray(model.vertexCount)
        val ry = DoubleArray(model.vertexCount)
        val rz = DoubleArray(model.vertexCount)

        var rMinX = Double.POSITIVE_INFINITY
        var rMaxX = Double.NEGATIVE_INFINITY
        var rMinY = Double.POSITIVE_INFINITY
        var rMaxY = Double.NEGATIVE_INFINITY

        for (i in 0 until model.vertexCount) {
            var x = model.vx[i] - cx0
            var y = model.vy[i] - cy0
            var z = model.vz[i] - cz0

            if (swapYZ) {
                val tmp = y
                y = z
                z = tmp
            }
            if (flipY) {
                y = -y
            }

            // yaw (Y)
            val x1 = x * cy + z * sy
            val z1 = -x * sy + z * cy
            x = x1; z = z1

            // pitch (X)
            val y1 = y * cp - z * sp
            val z2 = y * sp + z * cp
            y = y1; z = z2

            rx[i] = x
            ry[i] = y
            rz[i] = z

            if (x < rMinX) rMinX = x
            if (x > rMaxX) rMaxX = x
            if (y < rMinY) rMinY = y
            if (y > rMaxY) rMaxY = y
        }

        val spanX = (rMaxX - rMinX).coerceAtLeast(1.0)
        val spanY = (rMaxY - rMinY).coerceAtLeast(1.0)

        // Auto-fit: scale so model fits 80% of canvas
        val fit = 0.80
        val scaleX = (w * fit) / spanX
        val scaleY = (h * fit) / spanY
        val scale = min(scaleX, scaleY) * zoom

        val sx = DoubleArray(model.vertexCount)
        val syy = DoubleArray(model.vertexCount)

        val cx = w / 2.0
        val cyy = h / 2.0

        for (i in 0 until model.vertexCount) {
            sx[i] = cx + (rx[i] * scale)
            syy[i] = cyy - (ry[i] * scale) // invert Y for screen
        }

        // Draw wireframe triangles
        for (f in 0 until model.faceCount) {
            val a = model.fx[f]
            val b = model.fy[f]
            val c = model.fz[f]
            if (a !in sx.indices || b !in sx.indices || c !in sx.indices) continue

            g.strokeLine(sx[a], syy[a], sx[b], syy[b])
            g.strokeLine(sx[b], syy[b], sx[c], syy[c])
            g.strokeLine(sx[c], syy[c], sx[a], syy[a])
        }

        // Tiny debug marker center
        g.stroke = Color.rgb(120, 120, 255)
        g.strokeLine(cx - 6, cyy, cx + 6, cyy)
        g.strokeLine(cx, cyy - 6, cx, cyy + 6)
    }
}
