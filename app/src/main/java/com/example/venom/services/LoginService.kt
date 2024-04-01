package com.example.venom.services

import com.example.venom.classes.LoginResponse
import com.example.venom.classes.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>
}

