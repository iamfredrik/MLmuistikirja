package com.example.mlmuistikirja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val cameraActivityRequestCode = 1
    private val muistikirjaViewModel: MuistikirjaViewModel by viewModels {
        MuistikirjaViewModelFactory((application as MuistikirjaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MuistikirjaListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        muistikirjaViewModel.muistikirjat.observe(this) { muistikirjat ->
            muistikirjat?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivityForResult(intent, cameraActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == cameraActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(CameraActivity.EXTRA_REPLY)?.let { reply ->
                //val word = Word(reply)
                // wordViewModel.insert(word)
                val mkirja = Muistikirja("heissan")
                muistikirjaViewModel.insert(mkirja)
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Muistikirja ei tallennettu koska tyhjä",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}