package com.venom.venomtasks.services

import com.venom.venomtasks.classes.ReorderTagsBody
import com.venom.venomtasks.classes.Tag
import com.venom.venomtasks.classes.TagCreationRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TagService {
    @GET("/tags")
    fun getTags(): Call<ArrayList<Tag>>

    @POST("/tags")
    fun createTags(@Body tagCreationBody: TagCreationRequestBody): Call<Unit>

    @PUT("/tags/{id}")
    fun updateTag(@Path("id") id: Int, @Body tag: Tag): Call<Unit>

    @PUT("/tags/reorder")
    fun reorderTags(@Body tags: ReorderTagsBody): Call<Unit>

    @DELETE("/tags/{id}")
    fun deleteTag(@Path("id") id: Int): Call<Unit>
}