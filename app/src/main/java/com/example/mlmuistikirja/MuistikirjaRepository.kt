package com.example.mlmuistikirja

import androidx.lifecycle.LiveData

class MuistikirjaRepository(private val muistikirjaDao: MuistikirjaDao) {
    val getMuistikirjat = muistikirjaDao.getMuistikirjat()

    suspend fun insert(muistikirja: Muistikirja){
        muistikirjaDao.insert(muistikirja)
    }

    suspend fun update(muistikirja: Muistikirja) {
        muistikirjaDao.insert(muistikirja)
    }
}