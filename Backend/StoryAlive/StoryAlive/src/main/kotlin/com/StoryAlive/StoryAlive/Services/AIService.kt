package com.StoryAlive.StoryAlive.Services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AIService(@Value("\${ai.model.url}") private val modelUrl: String) {
    private val client = OkHttpClient()

    fun createStoryFromPdf(pdfBytes: ByteArray): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "story.pdf",
                pdfBytes.toRequestBody("application/pdf".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url(modelUrl)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("AI model request failed: ${response.body?.string()}")
            }
            val json = response.body!!.string()
            return json;
        }
    }

}

