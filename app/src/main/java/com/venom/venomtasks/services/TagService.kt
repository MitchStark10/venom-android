package com.venom.venomtasks.services

import com.venom.venomtasks.classes.Tag
import retrofit2.Call
import retrofit2.http.GET

interface TagService {
    @GET("/tags")
    fun getTags(): Call<ArrayList<Tag>>
}