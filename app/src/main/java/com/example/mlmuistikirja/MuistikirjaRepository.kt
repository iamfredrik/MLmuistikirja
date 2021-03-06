package com.example.mlmuistikirja

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class MuistikirjaRepository(private val muistikirjaDao: MuistikirjaDao) {
    val getMuistikirjat: Flow<List<Muistikirja>> = muistikirjaDao.getMuistikirjat()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(muistikirja: Muistikirja){
        muistikirjaDao.insertWithTimestamp(muistikirja)
    }

    fun update(muistikirja: Muistikirja) {
        muistikirjaDao.updateWithTimestamp(muistikirja)
    }
}