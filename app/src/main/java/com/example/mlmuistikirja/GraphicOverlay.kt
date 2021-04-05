package com.example.mlmuistikirja

import android.content.Context
import android.graphics.Canvas
import android.hardware.camera2.CameraCharacteristics
import android.util.AttributeSet
import android.view.View
import java.util.*

open class GraphicOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val lock = Any()
    private var previewWidth = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight = 0
    private var heightScaleFactor = 1.0f
    private var facing = CameraCharacteristics.LENS_FACING_BACK
    private val graphics: MutableSet<Graphic> = HashSet()

    // Perusluokka mukautetulle grafiikkaobjektille, joka hahmonnetaan graafisen peittokuvan sisällä.
    abstract class Graphic(private val overlay: GraphicOverlay) {
        abstract fun draw(canvas: Canvas?)

        // Säätää toimitetun arvon vaaka-arvon esikatselusta skaalasta näkymän mittakaavaan.
        fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        // Säätää toimitetun arvon pystysuuntaisen arvon esikatselusta skaalasta näkymän asteikoksi.
        fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        // Palauttaa sovelluksen sovelluskontekstin.
        val applicationContext: Context
            get() = overlay.context.applicationContext

        // Säätää x-koordinaatin esikatselun koordinaatistosta näkymän koordinaatistoon.
        fun translateX(x: Float): Float {
            return if (overlay.facing == CameraCharacteristics.LENS_FACING_FRONT) {
                overlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        // Säätää y-koordinaatin esikatselun koordinaatistosta näkymän koordinaatistoon.
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    // Poistaa kaikki grafiikat peittokuvasta.
    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    // Lisää grafiikan peittokuvaan.
    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
        postInvalidate()
    }

    // Poistaa grafiikan peittokuvasta.
    fun remove(graphic: Graphic) {
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }

    // Asettaa kameran määritteet koon ja kasvosuunnan mukaan
    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(lock) {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }

    // Piirtää peittokuvan siihen liittyvillä graafisilla esineillä.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleFactor = canvas.width.toFloat() / previewWidth.toFloat()
                heightScaleFactor = canvas.height.toFloat() / previewHeight.toFloat()
            }
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }
}