package com.darc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean,
    val notes: String = "",
    val expEarned: Int = 0
)
