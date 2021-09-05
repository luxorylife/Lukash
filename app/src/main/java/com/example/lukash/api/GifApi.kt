package com.example.lukash.api

import com.example.lukash.entityes.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GifApi {

    @GET("/{section}/{page}")
    fun get(@Path("section") section:String, @Path("page") page: Int, @Query("json") json: Boolean): Call<ApiResponse>

}