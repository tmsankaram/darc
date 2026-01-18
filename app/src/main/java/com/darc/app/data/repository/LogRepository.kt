package com.darc.app.data.repository

import com.darc.app.data.dao.LogDao
import com.darc.app.data.dao.PlayerDao
import com.darc.app.data.dao.TaskDao
import com.darc.app.data.entity.LogEntity
import com.darc.app.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val logDao: LogDao,
    private val taskDao: TaskDao,
    private val playerDao: PlayerDao
) {
    fun getLogsForTask(taskId: Long): Flow<List<LogEntity>> = logDao.getLogsForTask(taskId)

    fun getTodayLogs(): Flow<List<LogEntity>> {
        val startOfDay = getStartOfDay()
        return logDao.getLogsSince(startOfDay)
    }

    suspend fun completeTask(task: TaskEntity, notes: String = ""): LogEntity {
        val log = LogEntity(
            taskId = task.id,
            timestamp = System.currentTimeMillis(),
            isCompleted = true,
            notes = notes,
            expEarned = task.expReward
        )
        logDao.insertLog(log)

        // Update player EXP
        val player = playerDao.getPlayer().first()
        player?.let {
            val newExp = it.currentExp + task.expReward
            if (newExp >= it.expToNextLevel) {
                // Level up!
                val remainingExp = newExp - it.expToNextLevel
                val newLevel = it.level + 1
                val newExpToNext = calculateExpToNextLevel(newLevel)
                playerDao.updatePlayer(
                    it.copy(
                        level = newLevel,
                        currentExp = remainingExp,
                        expToNextLevel = newExpToNext,
                        rank = calculateRank(newLevel)
                    )
                )
            } else {
                playerDao.updatePlayer(it.copy(currentExp = newExp))
            }
        }

        return log
    }

    suspend fun skipTask(task: TaskEntity, notes: String = ""): LogEntity {
        val log = LogEntity(
            taskId = task.id,
            timestamp = System.currentTimeMillis(),
            isCompleted = false,
            notes = notes,
            expEarned = 0
        )
        logDao.insertLog(log)
        return log
    }

    suspend fun isTaskCompletedToday(taskId: Long): Boolean {
        val startOfDay = getStartOfDay()
        return logDao.getTaskLogForDate(taskId, startOfDay) != null
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun calculateExpToNextLevel(level: Int): Long {
        // EXP curve: 100 * level^1.5
        return (100 * Math.pow(level.toDouble(), 1.5)).toLong()
    }

    private fun calculateRank(level: Int): String {
        return when {
            level < 5 -> "E"
            level < 10 -> "D"
            level < 20 -> "C"
            level < 35 -> "B"
            level < 50 -> "A"
            else -> "S"
        }
    }
}
