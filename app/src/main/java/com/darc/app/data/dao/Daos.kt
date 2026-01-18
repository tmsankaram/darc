package com.darc.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darc.app.data.entity.LogEntity
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.StatsEntity
import com.darc.app.data.entity.StreakEntity
import com.darc.app.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player WHERE id = 1")
    fun getPlayer(): Flow<PlayerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineById(id: Long): Flow<RoutineEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE routineId = :routineId")
    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}

@Dao
interface LogDao {
    @Insert
    suspend fun insertLog(log: LogEntity)

    @Query("SELECT * FROM logs WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogsForTask(taskId: Long): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getLogsSince(since: Long): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE taskId = :taskId AND timestamp >= :since LIMIT 1")
    suspend fun getTaskLogForDate(taskId: Long, since: Long): LogEntity?
}

@Dao
interface StatsDao {
    @Query("SELECT * FROM stats WHERE id = 1")
    fun getStats(): Flow<StatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: StatsEntity)
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE id = 1")
    fun getStreak(): Flow<StreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)
}
