package com.darc.app.data.repository

import com.darc.app.data.dao.RoutineDao
import com.darc.app.data.dao.TaskDao
import com.darc.app.data.entity.RoutineEntity
import com.darc.app.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao
) {
    // Routines
    fun getAllRoutines(): Flow<List<RoutineEntity>> = routineDao.getAllRoutines()

    fun getRoutineById(id: Long): Flow<RoutineEntity?> = routineDao.getRoutineById(id)

    suspend fun createRoutine(name: String, description: String = ""): Long {
        val routine = RoutineEntity(name = name, description = description)
        return routineDao.insertRoutine(routine)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutine(id: Long) {
        routineDao.deleteRoutine(id)
    }

    // Tasks
    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForRoutine(routineId)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    suspend fun addTask(
        routineId: Long,
        title: String,
        type: String,
        difficulty: Int,
        target: String,
        expReward: Int
    ) {
        val task = TaskEntity(
            routineId = routineId,
            title = title,
            type = type,
            difficulty = difficulty.coerceIn(1, 10),
            target = target,
            expReward = expReward
        )
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(id: Long) {
        taskDao.deleteTask(id)
    }
}
