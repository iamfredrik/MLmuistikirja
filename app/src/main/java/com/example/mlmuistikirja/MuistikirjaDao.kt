package com.example.mlmuistikirja

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MuistikirjaDao {
    @Query("SELECT * FROM muistikirja_table ORDER by created_at ASC")
    fun getMuistikirjat(): LiveData<List<Muistikirja>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(muistikirja: Muistikirja)

    @Update
    suspend fun update(muistikirja: Muistikirja)

    @Query("DELETE FROM muistikirja_table")
    suspend fun deleteAll()
}