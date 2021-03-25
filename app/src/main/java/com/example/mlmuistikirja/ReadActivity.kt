package com.example.mlmuistikirja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        // Tämä aktiviteetti näyttää klikatun tekstin
        val textReader = findViewById<TextView>(R.id.readView)

        if(intent.extras != null) {
            textReader.text = intent.getStringExtra("EXTRA_TEXT")
        }
    }
}