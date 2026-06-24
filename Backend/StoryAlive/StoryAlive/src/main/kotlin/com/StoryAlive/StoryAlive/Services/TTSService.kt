package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.Story.RequestStoryUpdateDTO
import com.StoryAlive.StoryAlive.DTOs.Story.StoryCreationDTO
import com.StoryAlive.StoryAlive.DTOs.Story.TTSResponse
import com.StoryAlive.StoryAlive.DTOs.Story.UpdateStoryAudioRequestDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@Service
class TTSService(@Value("\${TTS_MODEL_URL2}") private val modelUrl: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build()

    private val mapper = jacksonObjectMapper()

    fun generateAudioFromStory(storyDto: StoryCreationDTO): TTSResponse {

        val taskId = getTaskId(storyDto)
        val timeoutMillis = 24 * 60 * 60 * 1000L
        val pollInterval = 10_000L // 10 sec
        val startTime = System.currentTimeMillis()

        // initial wait
//        Thread.sleep(60_000L)

        while (true) {

            try {
                val ttsResponse = getAudioFromTTS(taskId)

                if (ttsResponse != null) {
                    return ttsResponse
                }

            } catch (e: java.net.SocketTimeoutException) {
                println("TTS request timed out, retrying...")

            } catch (e: Exception) {
                println("Unexpected error during TTS polling: ${e.message}")
            }

            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw RuntimeException("TTS model processing timed out")
            }

            println("TTS still processing, waiting $pollInterval ms...")
            Thread.sleep(pollInterval)
        }
    }

    fun updateStoryAudio(storyDto: StoryCreationDTO, sentenceId: String, requestStoryUpdateDTO: RequestStoryUpdateDTO): TTSResponse {

        val taskId = getUpdateTaskId(
            storyDto,
            sentenceId,
            requestStoryUpdateDTO
        )
        val timeoutMillis = 24 * 60 * 60 * 1000L
        val pollInterval = 10_000L // 10 sec
        val startTime = System.currentTimeMillis()

        while (true) {
            try {
                val response = getUpdatedAudioFromTTS(taskId)
                if (response != null) return response
            }
            catch (e: java.net.SocketTimeoutException) {
                println("TTS request timed out, retrying...")

            }
            catch (e: Exception) {
                println("Unexpected error during TTS polling: ${e.message}")
            }

            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw RuntimeException("TTS model processing timed out")
            }

            println("TTS still processing, waiting $pollInterval ms...")
            Thread.sleep(pollInterval)
        }
    }

    private fun getAudioFromTTS(taskId: String): TTSResponse? {

        val request = Request.Builder()
            .url("${modelUrl.trimEnd('/')}/results/$taskId")
            .get()
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException(
                    "TTS result request failed: ${response.body?.string()}"
                )
            }

            val jsonString = response.body!!.string()
            val node = mapper.readTree(jsonString)

            return when (node["status"].asText()) {

                "processing" -> null

                "completed" -> {
                    mapper.treeToValue(
                        node,
                        TTSResponse::class.java
                    )
                }

                "failed" ->
                    throw RuntimeException(
                        node["error"]?.asText() ?: "Unknown error"
                    )

                else -> null
            }
        }
    }

    private fun getUpdatedAudioFromTTS(taskId: String): TTSResponse? {

        val request = Request.Builder()
            .url("${modelUrl.trimEnd('/')}/results/$taskId")
            .get()
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException(
                    "TTS result request failed: ${response.body?.string()}"
                )
            }

            val jsonString = response.body!!.string()
            val node = mapper.readTree(jsonString)

            return when(node["status"].asText()) {

                "processing" -> null

                "completed" -> {
                    mapper.treeToValue(
                        node,
                        TTSResponse::class.java
                    )
                }

                "failed" ->
                    throw RuntimeException(
                        node["error"]?.asText() ?: "Unknown error"
                    )

                else -> null
            }
        }
    }

    private fun createTTSRequest(storyDto: StoryCreationDTO): String {

        val json = mapper.writeValueAsString(storyDto)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(modelUrl)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException("TTS request failed: ${response.body?.string()}")
            }

            try {
                return response.body!!.string()
            } catch (e: SocketTimeoutException) {
                println("POST timed out but task may still be running...")
                throw e
            }
        }
    }

    private fun getTaskId(storyDto: StoryCreationDTO): String {

        val postResponse = createTTSRequest(storyDto)
        val postJson = mapper.readTree(postResponse)

        return postJson.get("task_id").asText()
    }

    private fun getUpdateTaskId(storyDto: StoryCreationDTO, sentenceId: String, request: RequestStoryUpdateDTO): String {

        val payload = UpdateStoryAudioRequestDTO(
            story = storyDto,
            sentenceId = sentenceId,
            emotion = request.emotion,
            intensity = request.intensity
        )

        val json = mapper.writeValueAsString(payload)

        val requestBody =
            json.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url("${modelUrl.trimEnd('/')}/update-story-audio")
            .post(requestBody)
            .build()

        client.newCall(httpRequest).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException(
                    "Update story request failed: ${response.body?.string()}"
                )
            }

            val responseBody = response.body!!.string()
            val responseJson = mapper.readTree(responseBody)

            return responseJson["task_id"].asText()
        }
    }
}