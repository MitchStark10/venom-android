package com.example.venom.services

import com.example.venom.classes.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskService {

    @PUT("tasks/{id}")
    fun updateTask(@Path("id") id: Int, @Body task: Task): Call<Unit>
}