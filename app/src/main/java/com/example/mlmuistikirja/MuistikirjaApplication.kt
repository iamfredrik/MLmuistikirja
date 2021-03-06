package com.example.mlmuistikirja

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MuistikirjaApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { MuistikirjaDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { MuistikirjaRepository(database.muistikirjaDao())}
}