package com.example.mlmuistikirja

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muistikirja_table")
data class Muistikirja(
    @ColumnInfo(name="muistikirja") val muistikirja: String,
    @ColumnInfo(name="created_at") var created_at: String = "CURRENT_TIMESTAMP",
    @ColumnInfo(name="updated_at") var updated_at: String = "CURRENT_TIMESTAMP",
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)