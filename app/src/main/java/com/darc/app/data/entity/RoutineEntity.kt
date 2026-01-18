package com.darc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
