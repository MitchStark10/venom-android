package com.venom.venomtasks.services

import com.venom.venomtasks.classes.Tag
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface TagService {
    @GET("/tags")
    fun getTags(): Call<ArrayList<Tag>>

    @DELETE("/tags/{id}")
    fun deleteTag(@Path("id") id: Int): Call<Unit>
}