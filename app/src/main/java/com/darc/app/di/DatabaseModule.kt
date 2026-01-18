package com.darc.app.di

import android.content.Context
import androidx.room.Room
import com.darc.app.data.DarcDatabase
import com.darc.app.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DarcDatabase {
        return Room.databaseBuilder(
            context,
            DarcDatabase::class.java,
            "darc_db"
        ).fallbackToDestructiveMigration() // For v1/dev only
         .build()
    }

    @Provides
    fun providePlayerDao(db: DarcDatabase): PlayerDao = db.playerDao()

    @Provides
    fun provideRoutineDao(db: DarcDatabase): RoutineDao = db.routineDao()

    @Provides
    fun provideTaskDao(db: DarcDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideLogDao(db: DarcDatabase): LogDao = db.logDao()

    @Provides
    fun provideStatsDao(db: DarcDatabase): StatsDao = db.statsDao()

    @Provides
    fun provideStreakDao(db: DarcDatabase): StreakDao = db.streakDao()
}
