package com.example.taskflow.data.mapper

import com.example.taskflow.data.local.db.Task
import com.example.taskflow.data.remote.TaskDto

fun TaskDto.toTask(): Task {
    return Task(
        id = id,
        title = title,
        status = if(completed) "Completed" else "Pending"
    )
}

//dto has completed variable