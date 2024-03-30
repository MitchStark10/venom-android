package com.example.venom.services

import com.example.venom.dataClasses.LoginResponse
import com.example.venom.dataClasses.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>
}

