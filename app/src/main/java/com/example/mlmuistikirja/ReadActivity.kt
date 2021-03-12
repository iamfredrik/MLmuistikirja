package com.example.mlmuistikirja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        val textreader = findViewById<TextView>(R.id.readView)

        if(intent.extras != null) {
            textreader.text = intent.getStringExtra("EXTRA_TEXT")
        }
    }
}