package com.darc.app.data.repository

import com.darc.app.data.dao.PlayerDao
import com.darc.app.data.dao.RoutineDao
import com.darc.app.data.dao.StatsDao
import com.darc.app.data.dao.StreakDao
import com.darc.app.data.dao.TaskDao
import com.darc.app.data.entity.PlayerEntity
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.StatsEntity
import com.darc.app.data.entity.StreakEntity
import com.darc.app.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val playerDao: PlayerDao,
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao,
    private val statsDao: StatsDao,
    private val streakDao: StreakDao
) {
    fun getPlayer(): Flow<PlayerEntity?> = playerDao.getPlayer()

    suspend fun updatePlayer(player: PlayerEntity) {
        playerDao.updatePlayer(player)
    }

    suspend fun createPlayer(name: String, focus: String) {
        val player = PlayerEntity(
            id = 1,
            name = name,
            level = 1,
            currentExp = 0,
            expToNextLevel = 100,
            rank = "E",
            title = "Novice"
        )
        playerDao.insertPlayer(player)

        // Initialize stats
        statsDao.insertStats(StatsEntity())

        // Initialize streak
        streakDao.insertStreak(StreakEntity())

        // Create starter routine based on focus
        createStarterRoutine(focus)
    }

    private suspend fun createStarterRoutine(focus: String) {
        val routine = when (focus) {
            "gym" -> RoutineEntity(name = "Gym Routine", description = "Your daily workout routine")
            "study" -> RoutineEntity(name = "Study Routine", description = "Your learning routine")
            "skills" -> RoutineEntity(name = "Skill Building", description = "Practice and improve your skills")
            else -> RoutineEntity(name = "Daily Routine", description = "Your daily habits")
        }
        val routineId = routineDao.insertRoutine(routine)

        // Add starter tasks based on focus
        val starterTasks = when (focus) {
            "gym" -> listOf(
                TaskEntity(routineId = routineId, title = "Warm Up", type = "Daily", difficulty = 2, target = "10 mins", expReward = 10),
                TaskEntity(routineId = routineId, title = "Main Workout", type = "Daily", difficulty = 6, target = "45 mins", expReward = 50),
                TaskEntity(routineId = routineId, title = "Cool Down", type = "Daily", difficulty = 2, target = "5 mins", expReward = 10)
            )
            "study" -> listOf(
                TaskEntity(routineId = routineId, title = "Read", type = "Daily", difficulty = 3, target = "30 mins", expReward = 20),
                TaskEntity(routineId = routineId, title = "Take Notes", type = "Daily", difficulty = 4, target = "15 mins", expReward = 25),
                TaskEntity(routineId = routineId, title = "Practice Problems", type = "Daily", difficulty = 5, target = "30 mins", expReward = 35)
            )
            "skills" -> listOf(
                TaskEntity(routineId = routineId, title = "Practice Session", type = "Daily", difficulty = 5, target = "1 hour", expReward = 40),
                TaskEntity(routineId = routineId, title = "Review Progress", type = "Weekly", difficulty = 3, target = "15 mins", expReward = 20)
            )
            else -> listOf(
                TaskEntity(routineId = routineId, title = "Morning Routine", type = "Daily", difficulty = 3, target = "30 mins", expReward = 20),
                TaskEntity(routineId = routineId, title = "Evening Review", type = "Daily", difficulty = 2, target = "15 mins", expReward = 15)
            )
        }

        starterTasks.forEach { task ->
            taskDao.insertTask(task)
        }
    }
}
