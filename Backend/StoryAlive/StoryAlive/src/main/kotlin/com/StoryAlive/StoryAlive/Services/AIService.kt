package com.StoryAlive.StoryAlive.Services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AIService(@Value("\${ai.model.url}") private val modelUrl: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build()

    private val mapper = jacksonObjectMapper()

    fun generateStoryFromPdf(pdfBytes: ByteArray): String {

        val taskId = getTaskId(pdfBytes)

        val timeoutMillis = 10 * 60 * 1000L
        val pollInterval = 240_000L // 4 minutes
        val startTime = System.currentTimeMillis()

        // Initial wait before polling
        Thread.sleep(240_000L)

        while (true) {

            val jsonString = getJsonFromLLM(taskId)
            val node = mapper.readTree(jsonString)

            if (!node.has("status") || node.get("status").asText() != "processing") {
                return jsonString
            }

            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw RuntimeException("AI model processing timed out")
            }

            println("Task still processing, waiting $pollInterval ms before next poll...")
            Thread.sleep(pollInterval)
        }
    }

    fun getJsonFromLLM(taskId: String): String {
        val request = Request.Builder()
            .url("https://habiba-hamed-llm-storyalive.hf.space/results/$taskId")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("AI model request failed: ${response.body?.string()}")
            }
            return response.body!!.string()
        }
    }

    private fun createStoryFromPdf(pdfBytes: ByteArray): String {

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
            return response.body!!.string()
        }
    }

    private fun getTaskId(pdf: ByteArray): String {
        val postResponse = createStoryFromPdf(pdf)
        val postJson = mapper.readTree(postResponse)
        return postJson.get("task_id").asText()
    }
}