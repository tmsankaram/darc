package com.darc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val id: Int = 1, // Single player for v1
    val name: String,
    val level: Int = 1,
    val currentExp: Long = 0,
    val expToNextLevel: Long = 100,
    val rank: String = "E",
    val title: String = "Novice",
    val joinedDate: Long = System.currentTimeMillis()
)
