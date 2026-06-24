package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.Story.BgMusic
import com.StoryAlive.StoryAlive.DTOs.Story.Cast
import com.StoryAlive.StoryAlive.DTOs.Story.Chapter
import com.StoryAlive.StoryAlive.DTOs.Story.GeminiLocation
import com.StoryAlive.StoryAlive.DTOs.Story.GeminiStory
import com.StoryAlive.StoryAlive.DTOs.Story.Location
import com.StoryAlive.StoryAlive.DTOs.Story.Scene
import com.StoryAlive.StoryAlive.DTOs.Story.Sentence
import com.StoryAlive.StoryAlive.DTOs.Story.StoryScript
import com.StoryAlive.StoryAlive.GeminiConfig
import com.StoryAlive.StoryAlive.HttpClientConfig
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory


@Service
class GeminiService(
    private val config: GeminiConfig,
    private val client: OkHttpClient,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(GeminiService::class.java)
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
    private val arabicToEnum = mapOf(
        "مقهى" to "COFFEE_SHOP",
        "شارع" to "STREET",
        "مستشفى" to "HOSPITAL",
        "بيت" to "HOME",
        "حديقة" to "PARK",
        "شاطئ" to "BEACH",
        "بحر" to "SEA",
        "كرونيش" to "CORNICHE",
        "جامعة" to "UNIVERSITY",
        "مول" to "MALL",
        "مسجد" to "MOSQUE",
        "كنيسة" to "CHURCH",
        "سوق" to "MARKET",
        "الشرطة" to "POLICE_STATION",
        "صوت طيور" to "BIRD_SONG",
        "عاصفة" to "STORM",
        "مطر" to "RAIN"
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

                        "evidence" to mapOf("type" to "string"),
                        "preferredRole" to mapOf(
                            "type" to "string",
                            "enum" to listOf(
                                "NARRATOR",
                                "PROTAGONIST",
                                "ANTAGONIST",
                                "SIDE_CHARACTER",
                                "COMIC_RELIEF",
                                "LOVE_INTEREST",
                                "MENTOR",
                                "HERO",
                                "VILLAIN",
                                "TEEN_CHARACTER",
                                "ADULT_CHARACTER",
                                "ELDERLY_CHARACTER",
                                "CREATURE",
                                "MONSTER",
                                "NONE"
                            )
                        )
                    ),
                    "required" to listOf("name_ar", "gender", "isAdult","preferredRole")
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
                                        "required" to listOf("locationName","path")
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
    private fun makeVoiceRef(name: String): String {
        return "voice::$name"
    }

    private fun makeProsodyRef(
        name: String,
        emotion: String,
        intensity: String
    ): String {
        return "prosody::$name::$emotion::$intensity"
    }

    fun extractStory(pdfBytes: ByteArray): String{
        log.info("Starting Gemini extraction")
        log.info("PDF size: {} bytes", pdfBytes.size)
        val pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes)
        log.info("PDF converted to Base64")
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
it MUST be assigned to the narrator "راوي" with emotion = NARRATION and gender = MALE.

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

Determine the preferredRole for every character.

NARRATOR = story narrator
PROTAGONIST = main hero
ANTAGONIST = main opponent
SIDE_CHARACTER = supporting character
COMIC_RELIEF = funny character
LOVE_INTEREST = romantic interest
MENTOR = guide or teacher
HERO = heroic figure
VILLAIN = evil figure
TEEN_CHARACTER = teenager
ADULT_CHARACTER = adult
ELDERLY_CHARACTER = old person
CREATURE = animal or fantasy creature
MONSTER = monster
NONE = unknown

IMPORTANT:
Every cast member must have a preferredRole other than NONE whenever possible.

The narrator "راوي" must always have preferredRole=NARRATOR.

Choose exactly one PROTAGONIST whenever the story has a central character.

Use SIDE_CHARACTER instead of NONE for supporting characters.

Arabic story:
{text}

Return JSON only.
""".trimIndent()
        log.info("Sending request to Gemini")
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
        log.info("Gemini response code: {}", response.code)
        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            log.error(
                "Gemini request failed. Code={}, Body={}",
                response.code,
                errorBody
            )
            throw RuntimeException("Gemini failed: ${response.code} $errorBody")
        }
        val responseText = response.body?.string()
        log.debug(
            "Raw Gemini response (first 1000 chars): {}",
            responseText?.take(1000)
        )
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
        log.info("Gemini JSON extracted")
        log.debug("Gemini JSON: {}", text.take(2000))
        val geminiStory =
            objectMapper.readValue(
                text,
                GeminiStory::class.java
            )
        log.info(
            "Parsed Gemini story successfully. Cast={}, Chapters={}",
            geminiStory.cast.size,
            geminiStory.chapters.size
        )
        val storyScript= buildFinalOutput(geminiStory)
        return objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(storyScript)
    }
    fun buildFinalOutput(gemini: GeminiStory): StoryScript {
        log.info("Building final StoryScript")
        val castMap = linkedMapOf<String, Cast>()

        gemini.cast.forEach { c ->

            val name = c.name_ar ?: c.name?: return@forEach

            if (!castMap.containsKey(name)) {
                castMap[name] = Cast(
                    name = name,
                    gender = c.gender ?: "MALE",
                    isAdult = c.isAdult ?: true,
                    preferredRole = c.preferredRole ?: "NONE",
                    voiceReference = makeVoiceRef(name)
                )
            } else {

                if (c.isAdult == false) {
                    castMap[name] =
                        castMap[name]!!.copy(isAdult = false)
                }
            }
        }

        if (!castMap.containsKey("راوي")) {

            castMap["راوي"] = Cast(
                name = "راوي",
                gender = "MALE",
                isAdult = true,
                preferredRole ="NARRATOR",
                voiceReference = makeVoiceRef("راوي")
            )
        }
        log.info(
            "Cast count after processing: {}",
            castMap.size
        )
        val chaptersOutput = mutableListOf<Chapter>()

        var globalSentenceId = 1

        gemini.chapters.forEachIndexed { chapterIndex, chapter ->

            val chapterId = chapterIndex + 1

            val title = Sentence(
                sentenceId = "t$chapterId",
                speaker = "راوي",
                sentence = chapter.chapter_title ?: "الفصل $chapterId",
                emotion = "NARRATION",
                intensity = "LOW",
                prosodyReference =
                    makeProsodyRef("راوي", "NARRATION", "LOW")
            )
            globalSentenceId++
            val scenesOutput = mutableListOf<Scene>()

            chapter.scenes.forEachIndexed { sceneIndex, scene ->

                val sentences = mutableListOf<Sentence>()

                scene.segments.forEach { segment ->

                    var speaker = segment.speaker ?: "راوي"

                    if (
                        speaker.equals("narration", true) ||
                        speaker.equals("narrator", true)
                    ) {
                        speaker = "راوي"
                    }

                    if (!castMap.containsKey(speaker)) {

                        castMap[speaker] = Cast(
                            name = speaker,
                            gender = "MALE",
                            isAdult = true,
                            preferredRole = "NONE",
                            voiceReference = makeVoiceRef(speaker)
                        )
                    }

                    val emotion =
                        segment.emotion ?: "NARRATION"

                    var intensity =
                        segment.intensity ?: "LOW"

                    if (
                        emotion == "NARRATION" ||
                        emotion == "WHISPER"
                    ) {
                        intensity = "LOW"
                    }

                    sentences += Sentence(
                        sentenceId = globalSentenceId.toString(),
                        speaker = speaker,
                        sentence = segment.sentence ?: "",
                        emotion = emotion,
                        intensity = intensity,
                        prosodyReference =
                            makeProsodyRef(
                                speaker,
                                emotion,
                                intensity
                            )
                    )

                    globalSentenceId++
                }

                if (sentences.isEmpty()) return@forEachIndexed

                val location =
                    scene.location ?: GeminiLocation("NONE", "")

                val locationName =
                    arabicToEnum[location.locationName]
                        ?: if (LOCATION_ENUMS.contains(location.locationName))
                            location.locationName!!
                        else
                            "NONE"

                scenesOutput += Scene(
                    sceneId = sceneIndex + 1,
                    location = Location(
                        locationName = locationName,
                        path = location.path ?: ""
                    ),
                    bgMusic = BgMusic(
                        volume = 0.5,
                        emotion = scene.scene_emotion ?: "PEACEFUL"
                    ),
                    sentences = sentences
                )
            }

            chaptersOutput += Chapter(
                chapterId = chapterId,
                title = title,
                scenes = scenesOutput
            )

        }
        log.info(
            "StoryScript built successfully. Cast={}, Chapters={}",
            castMap.size,
            chaptersOutput.size
        )

        return StoryScript(
            cast = castMap.values.toList(),
            chapters = chaptersOutput
        )
    }

}