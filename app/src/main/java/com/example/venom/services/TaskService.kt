package com.example.venom.services

import com.example.venom.classes.CreateTaskRequestBody
import com.example.venom.classes.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskService {

    @PUT("tasks/{id}")
    fun updateTask(@Path("id") id: Int, @Body task: Task): Call<Unit>

    @POST("tasks")
    fun createTask(@Body createTaskRequestBody: CreateTaskRequestBody): Call<Unit>

    @GET("/tasks/today")
    fun getTodaysTasks(): Call<ArrayList<Task>>

    @GET("/tasks/completed")
    fun getCompletedTasks(): Call<ArrayList<Task>>

    @GET("/tasks/upcoming")
    fun getUpcomingTasks(): Call<ArrayList<Task>>
}