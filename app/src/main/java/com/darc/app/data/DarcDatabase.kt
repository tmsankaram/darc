package com.darc.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darc.app.data.dao.*
import com.darc.app.data.entity.*

@Database(
    entities = [
        PlayerEntity::class,
        RoutineEntity::class,
        TaskEntity::class,
        LogEntity::class,
        StatsEntity::class,
        StreakEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DarcDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun routineDao(): RoutineDao
    abstract fun taskDao(): TaskDao
    abstract fun logDao(): LogDao
    abstract fun statsDao(): StatsDao
    abstract fun streakDao(): StreakDao
}
