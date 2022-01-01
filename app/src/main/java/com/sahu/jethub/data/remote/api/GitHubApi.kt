package com.sahu.jethub.data.remote.api

import com.sahu.jethub.data.remote.RemoteDataObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("/search/issues")
    suspend fun queryFilter(
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<RemoteDataObject>
}