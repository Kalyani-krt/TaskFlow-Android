package com.example.taskflow.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface TaskApiInterface {
    @GET("todos")
    suspend fun getTask(): List<TaskDto>

    //suspend fun getTask(): Response<List<TaskDto>>
}