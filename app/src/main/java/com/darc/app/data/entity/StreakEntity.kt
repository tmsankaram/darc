package com.darc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val id: Int = 1, // Global streak for v1
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastLogDate: Long = 0,
    val revivalTokens: Int = 0
)
