package com.example.mlmuistikirja

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muistikirja_table")
data class Muistikirja(
    @ColumnInfo(name="muistikirja") val muistikirja: String,
    @ColumnInfo(name="created_at") var created_at: Long = 0,
    @ColumnInfo(name="updated_at") var updated_at: Long = 0,
    @ColumnInfo(name="read_status") var read_status: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)