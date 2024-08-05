package com.venom.venom.services

import com.venom.venom.classes.LoginResponse
import com.venom.venom.classes.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("users/login")
    fun login(@Body user: User): Call<LoginResponse>
}

