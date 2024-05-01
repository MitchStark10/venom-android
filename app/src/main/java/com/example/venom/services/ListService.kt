package com.example.venom.services

import com.example.venom.classes.List
import com.example.venom.classes.ListCreationRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ListService {

    @GET("lists")
    fun getLists(): Call<ArrayList<List>>

    @POST("lists")
    fun createList(@Body listRequestBody: ListCreationRequestBody): Call<Unit>
}