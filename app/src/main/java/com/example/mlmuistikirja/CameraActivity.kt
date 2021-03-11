package com.example.mlmuistikirja

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaActionSound
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        // Määritä listener kamera painikkeelle
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
            it.isClickable = false
            val shutter = MediaActionSound()
            shutter.play(MediaActionSound.SHUTTER_CLICK)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun analyze(imageProxy: ImageProxy) {
        val bitmapImage = imageProxy.convertImageProxyToBitmap()
        val image = InputImage.fromBitmap(
            bitmapImage,
            imageProxy.imageInfo.rotationDegrees
        )
        // Käsittele kuva ML Kit Vision API:lla
        recognizeImageText(image, imageProxy)
    }

    fun ImageProxy.convertImageProxyToBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
        var i = 0
        var resultString = StringBuilder()
        for (block in visionText.textBlocks) {
            // Log.d(TAG, block.text)
            resultString.appendLine(block.text)

            if (i++ == visionText.textBlocks.lastIndex) {
                Log.d(TAG, resultString.toString())


                val builder = AlertDialog.Builder(this@CameraActivity)
                builder.setMessage(resultString.toString())
                    .setCancelable(false)
                    .setPositiveButton("Tallenna") { _, _ ->
                        val replyIntent = Intent()
                        replyIntent.putExtra(EXTRA_REPLY, resultString.toString())
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                    .setNegativeButton("Keskeytä") {dialog, _ ->
                        binding.cameraCaptureButton.isClickable = true
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()


            }
        }
    }

    private fun takePhoto() {
        // hae viite muokattavaan kuvakaappaukseen
        val imageCapture = imageCapture ?: return

        // Määritä listener kuvankaappaukselle, joka käynnistyy kun kuva on otettu
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)
                    Log.d(TAG, "Kuvankaappaus onnistui")
                    analyze(imageProxy)
                    imageProxy.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Kuvankaappaus epäonnistui: ${exception.message}", exception)
                }
            })
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
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Kameran sitominen epäonnistui", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
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
                startCamera()
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
        private const val TAG = "logitagi"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val EXTRA_REPLY = "com.example.android.mlmuistikirja.EXTRA_REPLY"
    }
}