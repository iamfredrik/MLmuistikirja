package com.example.mlmuistikirja

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val cameraActivityRequestCode = 1
    private val muistikirjaViewModel : MuistikirjaViewModel by viewModels {
        MuistikirjaViewModel.MuistikirjaViewModelFactory((application as MuistikirjaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivityForResult(intent, cameraActivityRequestCode)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MuistikirjaListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        muistikirjaViewModel.muistikirjat.observe(this, { muistikirja ->
            muistikirja?.let { adapter.submitList(it) }
        })
    }

}