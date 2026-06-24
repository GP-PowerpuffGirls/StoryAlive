package com.example.storyalive.network

import com.example.storyalive.model.StoryRequestDTO
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun createStoryRequestBody(request: StoryRequestDTO): MultipartBody.Part {
    val gson = Gson()

    val requestBody = gson.toJson(request)
        .toRequestBody("application/json".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "storyRequestDTO", // ✅ MUST match backend
        null,
        requestBody
    )
}