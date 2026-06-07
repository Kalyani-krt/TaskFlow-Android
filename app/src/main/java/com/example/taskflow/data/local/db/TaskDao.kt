package com.example.taskflow.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
   @Query ("select * from Task ")
   fun observeTasks() : Flow<List<Task>>

   @Upsert
   suspend fun upsertTask(task: Task)

   @Query("select * from Task where id=:id")
   fun getTaskById(id:String):Flow<Task?>

   @Query("DELETE FROM Task WHERE id = :id")
   suspend fun deleteTaskById(id: String)

}


//The main difference in NOTED app and this current one is that it was using Livedata+XML
//this has compose+flow
// nothing is wrong both are valid