package com.example.mlmuistikirja

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.google.mlkit.vision.text.Text

class TextGraphic internal constructor(overlay: GraphicOverlay, private val element: Text.Element?) : GraphicOverlay.Graphic(overlay) {
    private val rectPaint: Paint = Paint()
    private val textPaint: Paint

    // Piirtää tekstilohkomerkinnät
    override fun draw(canvas: Canvas?) {
        Log.d(TAG, "Piirretty")
        checkNotNull(element) { "Yritetään piirtää tyhjä teksti." }

        // Piirtää rajoittavan ruudun TextBlockin ympärille.
        val rect = RectF(element.boundingBox)
        canvas!!.drawRect(rect, rectPaint)

        // Renderöi tekstin ruudun alaosassa.
        canvas.drawText(element.text, rect.left, rect.bottom, textPaint)
    }

    companion object {
        private const val TAG = "softa"
        private const val TEXT_COLOR = Color.RED
        private const val RECT_COLOR = Color.TRANSPARENT
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

    init {
        rectPaint.color = RECT_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        // Piirrä peite uudelleen, kun tämä kuva on lisätty
        postInvalidate()
    }
}
