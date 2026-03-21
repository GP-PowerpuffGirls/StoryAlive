package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.Story.StoryCreationDTO
import com.StoryAlive.StoryAlive.DTOs.Story.TTSResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TTSService(@Value("\${TTS_MODEL_URL}") private val modelUrl: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build()

    private val mapper = jacksonObjectMapper()

    fun generateAudioFromStory(storyDto: StoryCreationDTO): TTSResponse {

        val taskId = getTaskId(storyDto)

        val timeoutMillis = 10 * 60 * 1000L
        val pollInterval = 60_000L
        val startTime = System.currentTimeMillis()

        // initial wait
        //Thread.sleep(60_000L)

        while (true) {
            println("entered polling loop")
            val ttsResponse = getAudioFromTTS(taskId)
            if (ttsResponse != null) {
                return ttsResponse
            }

            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw RuntimeException("TTS model processing timed out")
            }

            println("TTS still processing, waiting $pollInterval ms...")
            Thread.sleep(pollInterval)
        }
    }

//    private fun getAudioFromTTS(taskId: String): TTSResponse? {
//
//        val request = Request.Builder()
//            .url("${modelUrl.trimEnd('/')}/results/$taskId")
//            .get()
//            .build()
//
//        client.newCall(request).execute().use { response ->
//
//            if (!response.isSuccessful) {
//                throw RuntimeException("TTS result request failed: ${response.body?.string()}")
//            }
//
//            val jsonString = response.body!!.string()
//            val node = mapper.readTree(jsonString)
//
//            if (node.has("status") && node.get("status").asText() == "processing") {
//                return null
//            }
//
//            return mapper.readValue(jsonString, TTSResponse::class.java)
//        }

    private fun getAudioFromTTS(taskId: String): TTSResponse? {
        val request = Request.Builder()
            .url("${modelUrl.trimEnd('/')}/results/$taskId")
            .get()
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException("TTS result request failed: ${response.body?.string()}")
            }

            val jsonString = response.body!!.string()
            val node = mapper.readTree(jsonString)

            println("node is "+ node.toString())

            // Still processing
            if (node.has("status") && node.get("status").asText() == "processing") {
                return null
            }

            // Task failed
            if (node.has("status") && node.get("status").asText() == "failed") {
                throw RuntimeException("TTS task failed")
            }

            // Task completed: manually map fields

            val fileName = node.get("fileName")?.asText()
                ?: throw RuntimeException("Missing fileName")
            val duration = node.get("duration")?.asDouble()
                ?: throw RuntimeException("Missing duration")
            val audioBase64 = node.get("audioBase64")?.asText()
                ?: throw RuntimeException("Missing audioBase64")

            return TTSResponse(fileName, duration, audioBase64)

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

            return response.body!!.string()
        }
    }

    private fun getTaskId(storyDto: StoryCreationDTO): String {

        val postResponse = createTTSRequest(storyDto)
        val postJson = mapper.readTree(postResponse)

        return postJson.get("task_id").asText()
    }
}