package com.example.venom.services

import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    companion object {
        val retrofit: Retrofit = Builder()
            .baseUrl("https://venom-backend-wmmm.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}