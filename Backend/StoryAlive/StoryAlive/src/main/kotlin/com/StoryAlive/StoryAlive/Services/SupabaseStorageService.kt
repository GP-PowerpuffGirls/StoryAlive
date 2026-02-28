package com.StoryAlive.StoryAlive.Services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SupabaseStorageService(
    @Value("\${supabase.url}") private val supabaseUrl: String,
    @Value("\${supabase.key}") private val supabaseKey: String,
    @Value("\${supabase.bucket}") private val bucket: String
) {

    private val client = OkHttpClient()

//    fun uploadPdf(file: MultipartFile, path: String): String {
//
//        val requestBody = file.bytes.toRequestBody("application/pdf".toMediaType())
//
//        val request = Request.Builder()
//            .url("$supabaseUrl/storage/v1/object/$bucket/$path")
//            .addHeader("Authorization", "Bearer $supabaseKey")
//            .addHeader("apikey", supabaseKey)
//            .put(requestBody)
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) {
//                throw RuntimeException("Supabase upload failed: ${response.body?.string()}")
//            }
//        }
//
//        // Public URL
//        return "$supabaseUrl/storage/v1/object/public/$bucket/$path"
//    }

    fun uploadFile(fileBytes: ByteArray, path: String, contentType: String): String {
        val requestBody = fileBytes.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url("$supabaseUrl/storage/v1/object/$bucket/$path")
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Supabase upload failed: ${response.body?.string()}")
            }
        }

        return "$supabaseUrl/storage/v1/object/public/$bucket/$path"
    }
}