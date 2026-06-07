package com.example.taskflow.data.remote

import com.example.taskflow.data.local.db.Task


//API model
data class TaskDto(
    val id: String,
    val title: String,
    val completed: Boolean

)
