package com.darc.app.domain

import com.darc.app.data.dao.LogDao
import com.darc.app.data.dao.StatsDao
import com.darc.app.data.dao.TaskDao
import com.darc.app.data.entity.StatsEntity
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gamification Engine handles all game mechanics:
 * - EXP calculations
 * - Level progression
 * - Rank system
 * - Stat calculations based on task completion patterns
 */
@Singleton
class GamificationEngine @Inject constructor(
    private val logDao: LogDao,
    private val taskDao: TaskDao,
    private val statsDao: StatsDao
) {
    // EXP Formulas
    fun calculateExpForTask(difficulty: Int, streak: Int = 0): Int {
        val baseExp = when {
            difficulty <= 3 -> difficulty * 10
            difficulty <= 6 -> difficulty * 12
            difficulty <= 8 -> difficulty * 15
            else -> difficulty * 20
        }

        // Streak bonus: +5% per day, max 50%
        val streakMultiplier = 1.0 + (streak.coerceAtMost(10) * 0.05)
        return (baseExp * streakMultiplier).toInt()
    }

    fun calculateExpToNextLevel(level: Int): Long {
        // EXP curve: 100 * level^1.5
        return (100 * Math.pow(level.toDouble(), 1.5)).toLong()
    }

    // Rank System
    fun calculateRank(level: Int): String {
        return when {
            level < 5 -> "E"
            level < 10 -> "D"
            level < 20 -> "C"
            level < 35 -> "B"
            level < 50 -> "A"
            else -> "S"
        }
    }

    fun getRankColor(rank: String): Long {
        return when (rank) {
            "E" -> 0xFF888888
            "D" -> 0xFF4ECDC4
            "C" -> 0xFF4CAF50
            "B" -> 0xFFFFE66D
            "A" -> 0xFFFFA500
            "S" -> 0xFFFF6B6B
            else -> 0xFFBB86FC
        }
    }

    // Stat Calculation based on task completion patterns
    suspend fun recalculateStats(): StatsEntity {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        val recentLogs = logDao.getLogsSince(thirtyDaysAgo).first()
        val allTasks = taskDao.getAllTasks().first()

        val taskMap = allTasks.associateBy { it.id }
        val completedLogs = recentLogs.filter { it.isCompleted }

        // Calculate stats based on completed task types and difficulty
        var strengthPoints = 0
        var intelligencePoints = 0
        var disciplinePoints = 0
        var willpowerPoints = 0

        completedLogs.forEach { log ->
            val task = taskMap[log.taskId]
            task?.let {
                val points = it.difficulty

                // Categorize by routine/task characteristics
                // For now, distribute based on difficulty
                when {
                    it.difficulty >= 7 -> {
                        willpowerPoints += points
                        strengthPoints += points / 2
                    }
                    it.difficulty >= 4 -> {
                        intelligencePoints += points
                        disciplinePoints += points / 2
                    }
                    else -> {
                        disciplinePoints += points
                    }
                }
            }
        }

        // Calculate consistency bonus for discipline
        val uniqueDays = completedLogs.map {
            Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.DAY_OF_YEAR)
        }.toSet().size
        disciplinePoints += uniqueDays * 2

        // Convert points to stats (1-100 scale)
        val stats = StatsEntity(
            strength = (strengthPoints / 5).coerceIn(1, 100),
            intelligence = (intelligencePoints / 5).coerceIn(1, 100),
            discipline = (disciplinePoints / 5).coerceIn(1, 100),
            willpower = (willpowerPoints / 5).coerceIn(1, 100)
        )

        statsDao.insertStats(stats)
        return stats
    }

    // Title assignment based on stats and level
    fun calculateTitle(level: Int, stats: StatsEntity): String {
        val dominantStat = listOf(
            "strength" to stats.strength,
            "intelligence" to stats.intelligence,
            "discipline" to stats.discipline,
            "willpower" to stats.willpower
        ).maxByOrNull { it.second }?.first ?: "discipline"

        return when {
            level < 5 -> "Novice"
            level < 10 -> when (dominantStat) {
                "strength" -> "Warrior Initiate"
                "intelligence" -> "Scholar Initiate"
                "willpower" -> "Ascetic Initiate"
                else -> "Disciple"
            }
            level < 20 -> when (dominantStat) {
                "strength" -> "Warrior"
                "intelligence" -> "Scholar"
                "willpower" -> "Ascetic"
                else -> "Adept"
            }
            level < 35 -> when (dominantStat) {
                "strength" -> "Champion"
                "intelligence" -> "Sage"
                "willpower" -> "Monk"
                else -> "Master"
            }
            level < 50 -> when (dominantStat) {
                "strength" -> "Gladiator"
                "intelligence" -> "Archmage"
                "willpower" -> "Hierophant"
                else -> "Grandmaster"
            }
            else -> when (dominantStat) {
                "strength" -> "Titan"
                "intelligence" -> "Omniscient"
                "willpower" -> "Transcendent"
                else -> "Legend"
            }
        }
    }
}
