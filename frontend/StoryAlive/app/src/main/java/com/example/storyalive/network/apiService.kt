package com.example.storyalive.network

import android.R
import com.example.storyalive.model.AuthResponse
import com.example.storyalive.model.EditSentenceRequest
import com.example.storyalive.model.IdObject
import com.example.storyalive.model.PagedResponse
import com.example.storyalive.model.PagedResponses
import com.example.storyalive.model.StoryRequestDTO
import com.example.storyalive.model.StoryResponseDTO
import com.example.storyalive.model.UserLoginRequest
import com.example.storyalive.model.UserResponse
import com.example.storyalive.model.UserSignupRequest
import com.example.storyalive.model.VoiceActorRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {

    @POST("/auth/register")
    suspend fun signup(
        @Body request: UserSignupRequest
    ): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(
        @Body request: UserLoginRequest
    ): Response<AuthResponse>

    @POST("auth/logout")
    @Headers("No-Auth: true")
    suspend fun logout(@Header("Authorization") refreshToken: String): Response<String>

    @Multipart
    @POST("/stories/create-story")
    suspend fun createStory(
        @Part file: MultipartBody.Part,
        @Part("storyRequestDTO") storyRequestDTO: RequestBody
    ):  StoryResponseDTO

    @GET("/voice-actors")
    suspend fun getVoiceActors(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PagedResponse<VoiceActorRequest>>

    @GET("voice-actors/private")
    suspend fun getPrivateVoiceActors(
        @Query("pageNumber") page: Int,
        @Query("pageSize") size: Int
    ): Response<PagedResponse<VoiceActorRequest>>

    @GET("voice-actors/all-user-available")
    suspend fun getUserAvailableVoiceActors(
        @Query("pageNumber") page: Int,
        @Query("pageSize") size: Int
    ): Response<PagedResponse<VoiceActorRequest>>
    @Multipart
    @POST("/voice-actors")
    suspend fun createVoiceActor(
        @Part("request") request: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): VoiceActorRequest

    @GET("/enums/all")
    suspend fun getEnums(): Map<String, List<String>>

    @GET("/stories")
    suspend fun getStories(
        @Query("pageNumber") page: Int = 0,
        @Query("pageSize") size: Int = 10
    ): Response<PagedResponses<StoryResponseDTO>>

    @GET("/stories/favourites")
    suspend fun getFavorites(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PagedResponses<StoryResponseDTO>>

    @GET("/stories/private")
    suspend fun getPrivateStories(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PagedResponses<StoryResponseDTO>>

    @GET("stories/history")
    suspend fun getHistoryStories(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PagedResponses<StoryResponseDTO>>

    @GET("/user")
    suspend fun getUser(): UserResponse

    @PUT("user/edit")
    suspend fun editUser(
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String,
        @Query("email") email: String,
        @Query("age") age: Int,
        @Query("currentPassword") currentPassword: String,
        @Query("newPassword") newPassword: String
    ): Response<UserResponse>

    @PUT("/stories/{storyId}/sentences/{sentenceId}")
    suspend fun editSentence(
        @Path("storyId") storyId: IdObject,
        @Path("sentenceId") sentenceId: String,
        @Body request: EditSentenceRequest
    ): StoryResponseDTO
}