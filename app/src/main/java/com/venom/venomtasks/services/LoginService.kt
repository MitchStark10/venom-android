package com.venom.venomtasks.services

import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>
}

