package com.darc.app.data.repository

import com.darc.app.data.dao.LogDao
import com.darc.app.data.dao.PlayerDao
import com.darc.app.data.dao.StreakDao
import com.darc.app.data.entity.StreakEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class StreakCheckResult(
    val isStreakMaintained: Boolean,
    val currentStreak: Int,
    val streakBroken: Boolean,
    val daysMissed: Int,
    val expPenalty: Int
)

@Singleton
class StreakRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val logDao: LogDao,
    private val playerDao: PlayerDao
) {
    fun getStreak(): Flow<StreakEntity?> = streakDao.getStreak()

    suspend fun checkAndUpdateStreak(): StreakCheckResult {
        val streak = streakDao.getStreak().first() ?: StreakEntity()
        val today = getStartOfDay()
        val yesterday = today - (24 * 60 * 60 * 1000)

        // Check if user logged anything today
        val todayLogs = logDao.getLogsSince(today).first()
        val hasLoggedToday = todayLogs.any { it.isCompleted }

        // Check if streak was already updated today
        if (streak.lastLogDate >= today) {
            return StreakCheckResult(
                isStreakMaintained = true,
                currentStreak = streak.currentStreak,
                streakBroken = false,
                daysMissed = 0,
                expPenalty = 0
            )
        }

        // Check for streak break
        val daysSinceLastLog = if (streak.lastLogDate > 0) {
            ((today - streak.lastLogDate) / (24 * 60 * 60 * 1000)).toInt()
        } else {
            0
        }

        return when {
            // First log ever or continuing streak from yesterday
            streak.lastLogDate == 0L || daysSinceLastLog <= 1 -> {
                if (hasLoggedToday) {
                    val newStreak = streak.currentStreak + 1
                    val updatedStreak = streak.copy(
                        currentStreak = newStreak,
                        bestStreak = maxOf(streak.bestStreak, newStreak),
                        lastLogDate = today
                    )
                    streakDao.insertStreak(updatedStreak)

                    StreakCheckResult(
                        isStreakMaintained = true,
                        currentStreak = newStreak,
                        streakBroken = false,
                        daysMissed = 0,
                        expPenalty = 0
                    )
                } else {
                    StreakCheckResult(
                        isStreakMaintained = false,
                        currentStreak = streak.currentStreak,
                        streakBroken = false,
                        daysMissed = 0,
                        expPenalty = 0
                    )
                }
            }
            // Streak broken
            else -> {
                val expPenalty = calculateExpPenalty(streak.currentStreak, daysSinceLastLog)

                // Apply penalty to player
                applyExpPenalty(expPenalty)

                val updatedStreak = if (hasLoggedToday) {
                    streak.copy(
                        currentStreak = 1, // Reset to 1
                        lastLogDate = today
                    )
                } else {
                    streak.copy(currentStreak = 0)
                }
                streakDao.insertStreak(updatedStreak)

                StreakCheckResult(
                    isStreakMaintained = false,
                    currentStreak = updatedStreak.currentStreak,
                    streakBroken = true,
                    daysMissed = daysSinceLastLog,
                    expPenalty = expPenalty
                )
            }
        }
    }

    suspend fun recordDailyActivity() {
        val streak = streakDao.getStreak().first() ?: StreakEntity()
        val today = getStartOfDay()

        if (streak.lastLogDate < today) {
            val daysSinceLastLog = if (streak.lastLogDate > 0) {
                ((today - streak.lastLogDate) / (24 * 60 * 60 * 1000)).toInt()
            } else {
                0
            }

            val newStreak = if (daysSinceLastLog <= 1) {
                streak.currentStreak + 1
            } else {
                1 // Streak broken, start fresh
            }

            val updatedStreak = streak.copy(
                currentStreak = newStreak,
                bestStreak = maxOf(streak.bestStreak, newStreak),
                lastLogDate = today
            )
            streakDao.insertStreak(updatedStreak)
        }
    }

    suspend fun addRevivalToken() {
        val streak = streakDao.getStreak().first() ?: return
        streakDao.insertStreak(streak.copy(revivalTokens = streak.revivalTokens + 1))
    }

    suspend fun useRevivalToken(): Boolean {
        val streak = streakDao.getStreak().first() ?: return false
        if (streak.revivalTokens <= 0) return false

        // Restore streak
        val yesterday = getStartOfDay() - (24 * 60 * 60 * 1000)
        streakDao.insertStreak(
            streak.copy(
                revivalTokens = streak.revivalTokens - 1,
                lastLogDate = yesterday // Pretend we logged yesterday
            )
        )
        return true
    }

    private suspend fun applyExpPenalty(penalty: Int) {
        val player = playerDao.getPlayer().first() ?: return
        val newExp = (player.currentExp - penalty).coerceAtLeast(0)
        playerDao.updatePlayer(player.copy(currentExp = newExp))
    }

    private fun calculateExpPenalty(streakLength: Int, daysMissed: Int): Int {
        // Penalty formula: (streak * 10) * days missed, capped at 500
        val basePenalty = streakLength * 10
        return (basePenalty * daysMissed).coerceAtMost(500)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Streak milestones for revival token rewards
    fun shouldAwardRevivalToken(newStreak: Int): Boolean {
        return newStreak > 0 && newStreak % 7 == 0 // Award token every 7-day streak
    }
}
