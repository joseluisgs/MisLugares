package com.joseluisgs.mislugares.Utilidades

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Clase de trasformación para las imagen Picasso
 */
class CirculoTransformacion : Transformation {
    var mCircleSeparator = false
    var color = "#ffffff"

    /**
     * Constructor
     */
    constructor()

    /**
     * Constructor con color
     *
     * @param color Color
     */
    constructor(color: String) {
        this.color = color
    }

    /**
     * Transformación
     *
     * @param circleSeparator Separador
     */
    constructor(circleSeparator: Boolean) {
        mCircleSeparator = circleSeparator
    }

    /**
     * Transmormación
     *
     * @param source Fuernte
     * @return Destino
     */
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.shader = shader
        val r = size / 2f
        canvas.drawCircle(r, r, r - 1, paint)
        val paintBorder = Paint()
        paintBorder.style = Paint.Style.STROKE
        paintBorder.color = Color.argb(84, 0, 0, 0)
        paintBorder.isAntiAlias = true
        paintBorder.strokeWidth = 1F
        canvas.drawCircle(r, r, r - 1, paintBorder)
        if (mCircleSeparator) {
            val paintBorderSeparator = Paint()
            paintBorderSeparator.style = Paint.Style.STROKE
            paintBorderSeparator.color = Color.parseColor(color)
            paintBorderSeparator.isAntiAlias = true
            paintBorderSeparator.strokeWidth = 4F
            canvas.drawCircle(r, r, r + 1, paintBorderSeparator)
        }
        squaredBitmap.recycle()
        return bitmap
    }

    /**
     * Key
     *
     * @return
     */
    override fun key(): String {
        return "circle"
    }
}
