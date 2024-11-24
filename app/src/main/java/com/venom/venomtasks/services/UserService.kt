package com.venom.venomtasks.services

import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.SettingsResponse
import com.venom.venomtasks.classes.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("users/createUser")
    fun createUser(@Body user: User): Call<LoginResponse>

    @GET("settings")
    fun getSettings(): Call<SettingsResponse>
}

