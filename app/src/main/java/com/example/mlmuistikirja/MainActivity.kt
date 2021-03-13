package com.example.mlmuistikirja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private val cameraActivityRequestCode = 1
    private val muistikirjaViewModel: MuistikirjaViewModel by viewModels {
        MuistikirjaViewModelFactory((application as MuistikirjaApplication).repository)
    }

    private val updateCallback = object: MuistikirjaListAdapter.UpdateCallbackInterface {
        override fun updateCallback(muistikirja: Muistikirja) {
            muistikirjaViewModel.update(muistikirja)
        }
    }

    private val readCallback = object: MuistikirjaListAdapter.ReadCallbackInterface {
        override fun readCallback(muistikirja: Muistikirja) {
            val intent = Intent(this@MainActivity, ReadActivity::class.java)
            intent.putExtra("EXTRA_TEXT", muistikirja.muistikirja)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MuistikirjaListAdapter(updateCallback, readCallback, viewModel = muistikirjaViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lisää erotin kohteiden välille
        recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

        muistikirjaViewModel.muistikirjat.observe(this) { muistikirjat ->
            muistikirjat?.let { adapter.submitList(it) }
        }


        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(adapter))
        itemTouchHelper.attachToRecyclerView((recyclerView))

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
                val mkirja = Muistikirja(reply)
                try {
                    muistikirjaViewModel.insert(mkirja)
                    Toast.makeText(
                        applicationContext,
                        "Muistikirja tallennettu",
                        Toast.LENGTH_LONG
                    ).show()
                } catch(e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        "Tallennus epäonnistui",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}