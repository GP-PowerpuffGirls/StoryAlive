package com.example.storyalive.network

import com.example.storyalive.model.AuthResponse
import com.example.storyalive.model.UserLoginRequest
import com.example.storyalive.model.UserSignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/auth/register")
    suspend fun signup(
        @Body request: UserSignupRequest
    ): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(
        @Body request: UserLoginRequest
    ): Response<AuthResponse>
}