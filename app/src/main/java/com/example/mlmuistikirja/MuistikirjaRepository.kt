package com.example.mlmuistikirja

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class MuistikirjaRepository(private val muistikirjaDao: MuistikirjaDao) {
    val getMuistikirjat: Flow<List<Muistikirja>> = muistikirjaDao.getMuistikirjat()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(muistikirja: Muistikirja){
        muistikirjaDao.insertWithTimestamp(muistikirja)
    }

    suspend fun update(muistikirja: Muistikirja) {
        muistikirjaDao.updateWithTimestamp(muistikirja)
    }
}