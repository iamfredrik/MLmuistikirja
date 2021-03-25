package com.example.mlmuistikirja

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class ReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        // Tämä aktiviteetti näyttää klikatun tekstin
        val textReader = findViewById<TextView>(R.id.readView)

        if(intent.extras != null) {
            textReader.text = intent.getStringExtra("EXTRA_TEXT")
        }

        // Pitkäpainallus listener jonka avulla kopioidaan teksti leikepöydälle
        textReader.setOnLongClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("teksti", textReader!!.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                applicationContext,
                "Teksti kopioitu",
                Toast.LENGTH_LONG
            ).show()

            return@setOnLongClickListener true
        }
    }
}