package com.example.mlmuistikirja

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Muistikirja::class), version = 1, exportSchema = false)
public abstract class MuistikirjaDatabase : RoomDatabase() {

    abstract fun muistikirjaDao(): MuistikirjaDao

    private class MuistikirjaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var muistikirjaDao = database.muistikirjaDao()

                    // Poista kaikki
                    muistikirjaDao.deleteAll()

                    var muistikirja = Muistikirja("Hello")
                    muistikirjaDao.insert(muistikirja)
                    muistikirja = Muistikirja("World")
                    muistikirjaDao.insert(muistikirja)
                }
            }
        }
    }

    companion object {
        // estetään useamman tietokannan avaamisia samanaikaisesti.
        @Volatile
        private var INSTANCE: MuistikirjaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MuistikirjaDatabase {
            // palauta jos tietokanta löytyy, muutoin luo uusi
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MuistikirjaDatabase::class.java,
                    "muistikirja_database"
                )
                    .addCallback((MuistikirjaDatabaseCallback(scope)))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}