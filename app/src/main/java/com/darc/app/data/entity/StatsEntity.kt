package com.darc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey val id: Int = 1, // Single stats entry for v1
    val strength: Int = 1,
    val intelligence: Int = 1,
    val discipline: Int = 1,
    val willpower: Int = 1
)
