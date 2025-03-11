package com.venom.venomtasks.services

import com.venom.venomtasks.classes.EditSettingsBody
import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.RequestPasswordResetBody
import com.venom.venomtasks.classes.SettingsResponse
import com.venom.venomtasks.classes.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("users/createUser")
    fun createUser(@Body user: User): Call<LoginResponse>

    @GET("settings")
    fun getSettings(): Call<SettingsResponse>

    @PUT("settings")
    fun editSettings(@Body editSettingsBody: EditSettingsBody): Call<Unit>

    @POST("users/request_password_reset")
    fun requestPasswordResetEmail(@Body requestPasswordResetBody: RequestPasswordResetBody): Call<Unit>
}

