package com.example.mlmuistikirja

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MuistikirjaDao {
    @Query("SELECT * FROM muistikirja_table ORDER by created_at ASC")
    abstract fun getMuistikirjat(): Flow<List<Muistikirja>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(muistikirja: Muistikirja)

    suspend fun insertWithTimestamp(muistikirja: Muistikirja){
        insert(muistikirja.apply {
            created_at = System.currentTimeMillis()
            updated_at = System.currentTimeMillis()
        })
    }

    @Update
    abstract suspend fun update(muistikirja: Muistikirja)

    suspend fun updateWithTimestamp(muistikirja: Muistikirja){
        insert(muistikirja.apply {
            updated_at = System.currentTimeMillis()
        })
    }

    @Query("DELETE FROM muistikirja_table")
    abstract fun deleteAll()

}