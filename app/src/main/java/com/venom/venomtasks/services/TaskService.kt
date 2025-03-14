package com.venom.venomtasks.services

import com.venom.venomtasks.classes.CreateTaskRequestBody
import com.venom.venomtasks.classes.StandupResponse
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.classes.TaskReorderBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {

    @PUT("tasks/{id}")
    fun updateTask(@Path("id") id: Int, @Body task: Task): Call<Unit>

    @POST("tasks")
    fun createTask(@Body createTaskRequestBody: CreateTaskRequestBody): Call<Unit>

    @GET("/tasks/today")
    fun getTodaysTasks(@Query("today") today: String?): Call<ArrayList<Task>>

    @GET("/tasks/completed")
    fun getCompletedTasks(): Call<ArrayList<Task>>

    @DELETE("/tasks/completed")
    fun deleteCompletedTasks(): Call<Unit>

    @GET("/tasks/upcoming")
    fun getUpcomingTasks(@Query("today") today: String?): Call<ArrayList<Task>>

    @PUT("/tasks/reorder")
    fun reorderTasks(@Body reorderTasksBody: TaskReorderBody): Call<Unit>

    @GET("/tasks/standup")
    fun getStandupTasks(@Query("today") today: String?): Call<StandupResponse>
}