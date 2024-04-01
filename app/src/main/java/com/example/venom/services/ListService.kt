package com.example.venom.services

import com.example.venom.dataClasses.List
import retrofit2.Call
import retrofit2.http.GET

interface ListService {

    @GET("lists")
    fun getLists(): Call<ArrayList<List>>
}