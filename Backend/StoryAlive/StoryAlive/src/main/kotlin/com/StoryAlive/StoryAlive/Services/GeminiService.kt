package com.StoryAlive.StoryAlive.Services

import Story
import com.StoryAlive.StoryAlive.GeminiConfig
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.util.Base64
import okhttp3.MediaType.Companion.toMediaType

@Service
class GeminiService(
    private val config: GeminiConfig,
    private val client: OkHttpClient,
    private val objectMapper: ObjectMapper
) {
    private val LOCATION_ENUMS = listOf(
        "COFFEE_SHOP",
        "STREET",
        "HOSPITAL",
        "HOME",
        "PARK",
        "BEACH",
        "SEA",
        "CORNICHE",
        "UNIVERSITY",
        "MALL",
        "MOSQUE",
        "CHURCH",
        "MARKET",
        "POLICE_STATION",
        "BIRD_SONG",
        "STORM",
        "RAIN",
        "NONE"
    )
    private val schema: Map<String, Any> = mapOf(

        "type" to "object",

        "properties" to mapOf(

            "cast" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "name_ar" to mapOf("type" to "string"),

                        "gender" to mapOf(
                            "type" to "string",
                            "enum" to listOf("MALE", "FEMALE")
                        ),

                        "isAdult" to mapOf("type" to "boolean"),

                        "evidence" to mapOf("type" to "string")
                    ),
                    "required" to listOf("name_ar", "gender", "isAdult")
                )
            ),

            "chapters" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(

                        "chapter_title" to mapOf("type" to "string"),

                        "scenes" to mapOf(
                            "type" to "array",
                            "items" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(

                                    "location" to mapOf(
                                        "type" to "object",
                                        "properties" to mapOf(

                                            "locationName" to mapOf(
                                                "type" to "string",
                                                "enum" to LOCATION_ENUMS
                                            ),

                                            "path" to mapOf("type" to "string")
                                        ),
                                        "required" to listOf("locationName", "path")
                                    ),

                                    "scene_emotion" to mapOf(
                                        "type" to "string",
                                        "enum" to listOf(
                                            "ROMANTIC",
                                            "TENSE",
                                            "TRAGIC",
                                            "MYSTERIOUS",
                                            "TRIUMPHANT",
                                            "PEACEFUL",
                                            "ENERGETIC"
                                        )
                                    ),

                                    "segments" to mapOf(
                                        "type" to "array",
                                        "items" to mapOf(
                                            "type" to "object",
                                            "properties" to mapOf(

                                                "speaker" to mapOf("type" to "string"),
                                                "sentence" to mapOf("type" to "string"),

                                                "emotion" to mapOf(
                                                    "type" to "string",
                                                    "enum" to listOf(
                                                        "NARRATION",
                                                        "HAPPINESS",
                                                        "SADNESS",
                                                        "ANGER",
                                                        "FEAR",
                                                        "SURPRISE",
                                                        "WHISPER"
                                                    )
                                                ),

                                                "intensity" to mapOf(
                                                    "type" to "string",
                                                    "enum" to listOf("LOW", "MEDIUM", "HIGH")
                                                )
                                            ),
                                            "required" to listOf(
                                                "speaker",
                                                "sentence",
                                                "emotion",
                                                "intensity"
                                            )
                                        )
                                    )

                                ),
                                "required" to listOf(
                                    "location",
                                    "segments",
                                    "scene_emotion"
                                )
                            )
                        )

                    ),
                    "required" to listOf(
                        "chapter_title",
                        "scenes"
                    )
                )
            )

        ),

        "required" to listOf(
            "cast",
            "chapters"
        )
    )

    fun extractStory(pdfBytes: ByteArray): Story{

        val pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes)

        val prompt = """
You are a cinematic story understanding engine.

Detect scene locations and map them to the following enum values:
COFFEE_SHOP = مقهى
STREET = شارع
HOSPITAL = مستشفى
HOME = بيت
PARK = حديقة
BEACH = شاطئ
SEA = بحر
CORNICHE = كرونيش
UNIVERSITY = جامعة
MALL = مول
MOSQUE = مسجد
CHURCH = كنيسة
MARKET = سوق
POLICE_STATION = الشرطة
BIRD_SONG = صوت طيور
STORM = عاصفة
RAIN = مطر
NONE = none

Return ONLY the enum keys, NOT Arabic words.
If location cannot be mapped → use NONE
IMPORTANT RULE:
If a sentence describes an action, feeling, or scene (NOT spoken words),
it MUST be assigned to the narrator "راوي" with emotion = NARRATION.

Example:
"اتنهّدت حبيبة بهدوء"
→ speaker: "راوي"

Only actual spoken dialogue inside quotes should be assigned to characters.

IMPORTANT:
Do NOT skip any narration text.
Every part of the story must be included either as narration or dialogue.

IMPORTANT RULE:
If emotion = NARRATION or WHISPER then intensity MUST always be LOW.

IMPORTANT:
Every speaker that appears in segments MUST also appear in the cast array.
Do not omit any character.

Arabic story:
{text}

Return JSON only.
""".trimIndent()

        val requestMap = mapOf(
            "contents" to listOf(
                mapOf(
                    "parts" to listOf(
                        mapOf(
                            "inlineData" to mapOf(
                                "mimeType" to "application/pdf",
                                "data" to pdfBase64
                            )
                        ),
                        mapOf(
                            "text" to prompt
                        )
                    )
                )
            ),

            "generationConfig" to mapOf(
                "responseMimeType" to "application/json",
                "responseSchema" to schema
            )
        )
        val requestBody = objectMapper.writeValueAsString(requestMap)

        val request = okhttp3.Request.Builder()
            .url("${config.baseUrl}?key=${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw RuntimeException("Gemini failed: ${response.code} ${response.body?.string()}")
        }
        val responseText = response.body?.string()

        val root = objectMapper.readTree(responseText)

        val candidates = root.path("candidates")

        if (!candidates.isArray || candidates.size()==0) {
            throw RuntimeException("No candidates returned from Gemini")
        }

        val text = candidates[0]
            .path("content")
            .path("parts")[0]
            .path("text")
            .asText()

        return objectMapper.readValue(text, Story::class.java)

    }

}