package com.blackspider.util.lib.remote

import com.blackspider.agramonia.data.remote.response.LoginResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiClient {
    @POST("api/login")
    fun login(@Query("email") email: String,
              @Query("password") password: String):
            Observable<LoginResponse>
}