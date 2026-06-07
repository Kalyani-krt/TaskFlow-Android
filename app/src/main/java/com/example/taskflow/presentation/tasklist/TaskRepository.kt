package com.example.taskflow.presentation.tasklist

import com.example.taskflow.data.local.db.Task
import com.example.taskflow.data.local.db.TaskDao
import com.example.taskflow.data.remote.RetrofitInstance.api
import kotlinx.coroutines.flow.Flow
import com.example.taskflow.data.mapper.toTask

class TaskRepository(private val taskDao: TaskDao){

    fun observeTaskRepo()=taskDao.observeTasks()
//    fun observeTaskRepo() :Flow<List<Task>>{
//        return taskDao.observeTasks()
//    }

   suspend fun upsertTaskRepo(task: Task){
        taskDao.upsertTask(task)
    }

    fun getTaskByIDRepo(id: String):Flow<Task?>{
        return taskDao.getTaskById(id)
    }

    suspend fun deleteRepo(id: String){
        taskDao.deleteTaskById(id)
    }

    suspend fun syncTasks(): Result<Unit> {
        return try{
            val remoteTasks = api.getTask()

            val localTasks = remoteTasks.map {   //converted API data into local db format
                it.toTask()
            }
            localTasks.forEach {            //stored each task to room(local) db
                taskDao.upsertTask(it)
            }
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)

        }}
}
