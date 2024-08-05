package com.venom.venom.services

import com.venom.venom.classes.List
import com.venom.venom.classes.ListCreationRequestBody
import com.venom.venom.classes.ReorderListsBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ListService {

    @GET("lists")
    fun getLists(): Call<ArrayList<List>>

    @POST("lists")
    fun createList(@Body listRequestBody: ListCreationRequestBody): Call<Unit>

    @DELETE("lists/{id}")
    fun deleteList(@Path("id") id: Int): Call<Unit>

    @PUT("lists/reorder")
    fun reorderLists(@Body reorderListsBody: ReorderListsBody): Call<Unit>
}