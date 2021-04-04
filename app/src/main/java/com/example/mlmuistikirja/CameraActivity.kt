package com.example.mlmuistikirja

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.MediaActionSound
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mlmuistikirja.databinding.ActivityCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@RequiresApi(Build.VERSION_CODES.R)
class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pyydä kameran käyttöoikeuksia
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, CameraActivity.REQUIRED_PERMISSIONS, CameraActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        var layout = binding.layout
        val cameraBtn  = binding.cameraCaptureButton

        // asetetaan nappulalle tooltip
        cameraBtn.tooltipText = "Osoita kuvattava teksti"

        // Määritä kuuntelija kamera painikkeelle
        cameraBtn.setOnClickListener {
            takePhoto()
            it.isClickable = false // inaktivoidaan painiketta
            val shutter = MediaActionSound()
            shutter.play(MediaActionSound.SHUTTER_CLICK) // Kamera ääni
            binding.progressBar.visibility = View.VISIBLE // Näytä progressBar
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun analyzeCallback(text: String) {
        // Callback TextAnalyzer luokasta
        // Näytetään alert dialogi jossa kuvattu teksti
        val builder = AlertDialog.Builder(this)
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Tallenna") { _, _ ->
                    // lähetetään reply intent main activitylle
                    val replyIntent = Intent()
                    replyIntent.putExtra(EXTRA_REPLY, text)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
                .setNegativeButton("Keskeytä") { dialog, _ ->
                    binding.cameraCaptureButton.isClickable = true // aktivoidaan kamera painike
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
        binding.progressBar.visibility = View.GONE
    }

    private val imageAnalyzer by lazy {
        val point = Point()
        val size = display?.getRealSize(point)
        ImageAnalysis.Builder()
                .setTargetResolution(Size(point.x, point.y))
                .build()
                .also {
                    it.setAnalyzer(
                            cameraExecutor,
                            TextAnalyzer(this, binding, ::analyzeCallback)
                    )
                }
    }

    private fun takePhoto() {
        capture = true
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Käytetään kameran elinkaaren sitomiseen elinkaaren omistajaan
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Esukatselu
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewView.surfaceProvider)
                    }

            imageCapture = ImageCapture.Builder()
                    .build()


            // Valitse takakamera oletuksena
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Vapauta käyttö ennen sidontaa
                cameraProvider.unbindAll()

                // Sido kameran käyttö
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalyzer, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Kameran sidonta epäonnistui", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        // Käyttöoikeus juttuja
        ContextCompat.checkSelfPermission(
                baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera() // Otetaan kamera käyttöön jos käyttöoikeus on myönnetty
            } else {
                Toast.makeText(
                        this,
                        "Käyttäjä ei myöntänyt käyttöoikeuksia.",
                        Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "softa"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val EXTRA_REPLY = "com.example.android.mlmuistikirja.EXTRA_REPLY"
        private var capture : Boolean = false
    }


    private class TextAnalyzer(private val context: Context?, private var binding: ActivityCameraBinding, private val analyzerCallback: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val layout = binding.layout

        @SuppressLint("UnsafeExperimentalUsageError")
       override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                )
                // Käsittele kuva ML Kit Vision API:lla
                recognizeImageText(image, imageProxy)
            }
        }

        private fun recognizeImageText(image: InputImage, imageProxy: ImageProxy) {
            TextRecognition.getClient()
                    .process(image)
                    .addOnSuccessListener { visionText ->
                        processImageText(visionText)
                        imageProxy.close()
                    }
                    .addOnFailureListener { error ->
                        Log.d(TAG, "tekstin tunnistus epäonnistui")
                        error.printStackTrace()
                        imageProxy.close()
                    }
        }

        private fun processImageText(visionText: Text){
            val resultString = StringBuilder()
            for ((i, block) in visionText.textBlocks.withIndex()) {
                // Log.d(TAG, block.text)
                resultString.appendLine(block.text)
                if (i == visionText.textBlocks.lastIndex) {
                    //Log.d(TAG, resultString.toString())
                    if (capture) {
                        analyzerCallback(resultString.toString())
                        capture = false
                    }
                }
                for (line in block.lines) {
                    for (element in line.elements) {
                        if(layout.childCount > 1) {
                            layout.removeViewAt(1)
                        }
                        val elem = Draw(context, element.boundingBox, element.text)
                        layout.addView(elem, 1)
                    }
                }
            }
        }
    }

    private class Draw(context: Context?, var rect: Rect?, var text: String) : View(context) {
        lateinit var paint: Paint
        lateinit var textPaint: Paint

        init {
            init()
        }

        private fun init() {
            paint = Paint()
            paint.color = Color.MAGENTA
            paint.strokeWidth = 10f
            paint.style = Paint.Style.STROKE

            textPaint = Paint()
            textPaint.color = Color.WHITE
            textPaint.style = Paint.Style.FILL
            textPaint.textSize = 80f
        }

        override fun onDraw(canvas: Canvas){
            super.onDraw(canvas)
            //canvas.drawText(text, rect!!.centerX().toFloat(),rect!!.centerY().toFloat(), textPaint)
            canvas.drawRect(rect!!.left.toFloat(), rect!!.top.toFloat(), rect!!.right.toFloat(), rect!!.bottom.toFloat(), paint)
        }
    }
}