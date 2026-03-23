package com.StoryAlive.StoryAlive.Services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class SupabaseStorageService(
    @Value("\${supabase.url}") private val supabaseUrl: String,
    @Value("\${supabase.key}") private val supabaseKey: String,
    @Value("\${supabase.bucket}") private val bucket: String
) {

    private val client = OkHttpClient()

    fun downloadFileFromSupabase(url: String): ByteArray {

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Failed to fetch file: ${response.body?.string()}")
            }

            return response.body!!.bytes()
        }
    }

    fun savePdfToCloud(pdf: MultipartFile, userId: ObjectId):String {
        if (pdf.contentType != "application/pdf") {
            throw IllegalArgumentException("Only PDF files are allowed")
        }

        val safeName = pdf.originalFilename!!.replace("\\s+".toRegex(), "_")
        val path = "pdf-files/${userId}/${UUID.randomUUID()}_${safeName}"

        val pdfUrl = uploadFile(
            fileBytes = pdf.bytes,
            path = path,
            contentType = "application/pdf",
            usedBucket = "files"
        )
        return pdfUrl
    }

    fun saveAudioToCloud(audio: MultipartFile, actorId: ObjectId, usedBucket: String): String {

        if (audio.contentType?.startsWith("audio/") != true) {
            throw IllegalArgumentException("Only audio files are allowed")
        }
        val safeName = audio.originalFilename!!.replace("\\s+".toRegex(), "_")
        val path = "${actorId}/${UUID.randomUUID()}_$safeName"

        val audioUrl = uploadFile(
            fileBytes = audio.bytes,
            path = path,
            contentType = audio.contentType!!,
            usedBucket = usedBucket
        )
        return audioUrl
    }
    fun saveAudioToCloud(audioBytes: ByteArray, fileName: String, storyId: ObjectId): String {

        val safeName = fileName.replace("\\s+".toRegex(), "_")
        val path = "${storyId}/final/${UUID.randomUUID()}_$safeName"

        val audioUrl = uploadFile(
            fileBytes = audioBytes,
            path = path,
            contentType = "audio/mpeg",
            usedBucket = "story-audio-files"
        )

        return audioUrl
    }

    fun saveJsonToCloud(jsonContent: String, userId: ObjectId): String {
        val bytes = jsonContent.toByteArray()
        val path = "json-files/$userId/${UUID.randomUUID()}_story.json"
        return uploadFile(bytes, path, "application/json", usedBucket = "files")
    }

    fun uploadFile(fileBytes: ByteArray, path: String, contentType: String, usedBucket: String): String {
        val requestBody = fileBytes.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url("$supabaseUrl/storage/v1/object/$usedBucket/$path")
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Supabase upload failed: ${response.body?.string()}")
            }
        }

        return "$supabaseUrl/storage/v1/object/public/$usedBucket/$path"
    }

}