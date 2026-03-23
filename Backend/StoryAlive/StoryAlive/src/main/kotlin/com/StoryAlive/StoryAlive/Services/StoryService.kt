package com.StoryAlive.StoryAlive.Services

import Story
import com.StoryAlive.StoryAlive.DTOs.Story.StoryCreationDTO
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.DTOs.Key.CastKey
import com.StoryAlive.StoryAlive.DTOs.Key.VoiceActorKey
import com.StoryAlive.StoryAlive.Enums.BGMusicEmotion
import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Intensity
import com.StoryAlive.StoryAlive.Enums.LocationName
import com.StoryAlive.StoryAlive.Enums.PreferredRole
import com.StoryAlive.StoryAlive.Models.Audio
import com.StoryAlive.StoryAlive.Models.BackgroundMusic
import com.StoryAlive.StoryAlive.Models.Location
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.StoryRepo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Optional
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach


@Service
class StoryService(private val storyRepo: StoryRepo,
                   private val supabaseStorageService: SupabaseStorageService,
                   private val llmService: LLMService,
                   private val ttsService: TTSService,
                   private val voiceActorService: VoiceActorService,
                   private val bgMusicService: BGMusicService,
                   private val locationService: LocationService,
                   private val userService: UserService) {
    val mapper = jacksonObjectMapper()

    fun getAllStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        return storyRepo.findAllByIsPrivateFalse(pageable)
    }

    fun getAllPrivateStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val creatorId: ObjectId = userService.getCurrrenctUser().getUserId()
        return storyRepo.findAllByCreatorIdAndIsPrivateTrue(creatorId, pageable)
    }

    fun createStory(storyRequest: StoryRequestDTO, pdf: MultipartFile): Story {
        val userId = userService.getCurrrenctUser().getUserId()
        val jsonString = llmService.generateStoryFromPdf(pdf.bytes)
        val storyDto: StoryCreationDTO = mapper.readValue(jsonString, StoryCreationDTO::class.java)
        val currentStory= createStoryMetaData(storyRequest)
        storyDto.storyId = currentStory.storyId.toString()
        assignActorsToCast(currentStory, storyDto)
        if (currentStory.hasBackgroundMusic) {
            assignBGMusicToScene(currentStory, storyDto)
        }
        if (currentStory.hasSfx) {
            assignSfxToScene(storyDto)
        }
        val ttsResponse = ttsService.generateAudioFromStory(storyDto)

        val updatedJson = mapper.writeValueAsString(storyDto)
        val jsonPath = supabaseStorageService.saveJsonToCloud(updatedJson, userId)
        val pdfPath = supabaseStorageService.savePdfToCloud(pdf, userId)

        currentStory.duration = ttsResponse.duration
        currentStory.finalAudioPath = ttsResponse.audioPath
        currentStory.pdfPath = pdfPath
        currentStory.jsonPath = jsonPath
        return storyRepo.save(currentStory)
    }

    fun createStoryDummy(storyId: ObjectId): Story {
        val currentStory: Story = storyRepo.findById(storyId)
            .orElseThrow { RuntimeException("Story not found with id $storyId") }

        val jsonString = supabaseStorageService.downloadFileFromSupabase(currentStory.jsonPath ?: "")
        val storyDto: StoryCreationDTO = mapper.readValue(jsonString, StoryCreationDTO::class.java)
        storyDto.storyId = currentStory.storyId.toString()
        assignActorsToCast(currentStory, storyDto)
        if (currentStory.hasBackgroundMusic) {
            assignBGMusicToScene(currentStory, storyDto)
        }
        if (currentStory.hasSfx) {
            assignSfxToScene(storyDto)
        }
        val updatedJson = mapper.writeValueAsString(storyDto)
        println(" STORY DTO HERE $storyDto")
        val ttsResponse = ttsService.generateAudioFromStory(storyDto)
        val jsonPath = supabaseStorageService.saveJsonToCloud(updatedJson, userService.getCurrrenctUser().getUserId())
        currentStory.duration = ttsResponse.duration
        currentStory.finalAudioPath = ttsResponse.audioPath
        currentStory.jsonPath = jsonPath
        return storyRepo.save(currentStory)
    }

    //HELPER FUNCTIONS
    fun createStoryMetaData(storyRequest: StoryRequestDTO): Story {
        val currentUser = userService.getCurrrenctUser()

        val voiceActorsMap: MutableMap<ObjectId, Pair<String, String>> =
            (if (storyRequest.voiceActors.isNullOrEmpty()) {
                emptyMap()
            } else {
                storyRequest.voiceActors.mapValues { (actorId) ->
                    val actor: Optional<VoiceActor> = voiceActorService.getAudioByActorId(actorId)
                    if (actor.get().audios.isNotEmpty()) {
                        Pair(actor.get().actorName, "")
                    } else {
                        Pair("", "")
                    }
                }
            }) as MutableMap<ObjectId, Pair<String, String>>

        val newStory = Story(
            storyId = ObjectId(),
            creatorId = currentUser.getUserId(),
            duration = 0.0, //from tts
            title = storyRequest.title,
            voiceActors = voiceActorsMap,
            description = storyRequest.description ?: "",
            tags = storyRequest.tags,
            genre = storyRequest.genre,
            isPrivate = storyRequest.isPrivate,
            hasSfx = storyRequest.hasSfx,
            hasBackgroundMusic = storyRequest.hasBackgroundMusic,
            pdfPath = "",
            minimumAge = storyRequest.minimumAge,
            finalAudioPath = "", //from tts
            jsonPath = "", //from llm
            createdAt = java.time.Instant.now(),
            modifiedAt = java.time.Instant.now(),
            numberOfViews = 0
        )
        return newStory
    }

    fun assignBGMusicToScene(currentStory: Story, storyDto: StoryCreationDTO) {
        val allBGMusic: List<BackgroundMusic> = bgMusicService.getAllBGMusicByForKids(currentStory.genre == Genre.KIDS)
        val bgMusicMap: Map<BGMusicEmotion?, List<String>> = allBGMusic.groupBy(
            { it.emotion },
            { it.musicPath }
        )
        for (chapter in storyDto.chapters) {
            for (scene in chapter.scenes) {
                val bgMusicPath = bgMusicMap[scene.bgMusic.emotion]
                if (bgMusicPath != null) {
                    bgMusicMap[scene.bgMusic.emotion]?.random()?.let { path ->
                        scene.bgMusic.musicPath = path
                    }
                }
            }

        }
    }

    fun assignSfxToScene(storyDto: StoryCreationDTO) {
        val allSfx: List<Location> = locationService.getAllLocationsList()
        val sfxMap: Map<LocationName?, String> = allSfx.associateBy(
            { it.locationName },
            { it.sfxPath }
        )
        for (chapter in storyDto.chapters) {
            for (scene in chapter.scenes) {
                if(scene.location.locationName == LocationName.NONE) continue
                val bgMusicPath = sfxMap[scene.location.locationName]
                if (bgMusicPath != null) {
                    scene.bgMusic.musicPath = bgMusicPath
                }
            }
        }
    }

    fun assignActorsToCast(currentStory: Story, storyDto: StoryCreationDTO) {
        val allVoiceActors: List<VoiceActor> = voiceActorService.getAllPublicVoiceActors()

        // Build audio lookup for fast access
        val actorAudioIndex: Map<ObjectId, Map<Pair<Emotion, Intensity>, Audio>> = allVoiceActors.associate { actor ->
            actor.voiceActorId to actor.audios.associateBy { it.emotion to it.intensity }
        }

        // Build mutable pool of actors by VoiceActorKey
        val actorsMap: MutableMap<VoiceActorKey, MutableList<Pair<ObjectId, String>>> = allVoiceActors
            .groupBy { VoiceActorKey(it.isAdult, it.gender, it.preferredRole) }
            .mapValues { it.value.map { actor -> actor.voiceActorId to actor.actorName }.toMutableList() }
            .toMutableMap()

        // Assign user-selected actors first
        val mutableVoiceActors = currentStory.voiceActors.toMutableMap()
        currentStory.voiceActors.forEach { (actorId, pair) ->
            val actor = voiceActorService.getAudioByActorId(actorId)
                .orElseThrow { RuntimeException("VoiceActor not found: $actorId") }
            val key = VoiceActorKey(actor.isAdult, actor.gender, actor.preferredRole)
            actorsMap[key]?.removeIf { it.first == actorId }
            mutableVoiceActors[actorId] = pair
        }

        // Assign remaining actors to cast members
        storyDto.cast.forEach { castMember ->
            if (castMember.name == "راوي") {
                castMember.preferredRole = PreferredRole.NARRATOR
            }
            else{
                castMember.preferredRole = PreferredRole.NONE
            }
            val key = VoiceActorKey(castMember.isAdult, castMember.gender, castMember.preferredRole)
            val availableActors = actorsMap[key]
                ?: throw RuntimeException("No available actor for cast member ${castMember.name} with $key")
            val selectedActor = availableActors.removeAt(0)
            mutableVoiceActors[selectedActor.first] = selectedActor.second to castMember.name
        }
        currentStory.voiceActors = mutableVoiceActors

        // Assign audio to title and sentences
        val castMap: MutableMap<CastKey, Audio> = mutableMapOf()
        storyDto.chapters.forEach { chapter ->
            val sentences = listOf(chapter.title) + chapter.scenes.flatMap { it.sentences }
            sentences.forEach { sentence ->
                val actorId = currentStory.voiceActors.entries.firstOrNull { it.value.second == sentence.speaker }?.key
                    ?: throw RuntimeException("No actor assigned for speaker ${sentence.speaker}")
                val intensitiesPriority = getIntensityFallbacks(sentence.intensity)
                val audio = actorAudioIndex[actorId]?.let { emotionMap ->
                    intensitiesPriority
                        .firstNotNullOfOrNull { intensity ->
                            emotionMap[sentence.emotion to intensity]
                        }
                }
                    ?: actorAudioIndex[actorId]?.get(Emotion.NARRATION to Intensity.LOW)
                    ?: throw RuntimeException(
                        "No audio found for actor $actorId with emotion ${sentence.emotion} and intensity ${sentence.intensity}"
                    )
                val castKey = CastKey(actorId, sentence.speaker, sentence.emotion, sentence.intensity)
                castMap[castKey] = audio
                sentence.speaker = castKey.castName
                sentence.prosodyReference = audio.filepath
            }
        }
    }
    fun getIntensityFallbacks(base: Intensity): List<Intensity> = when (base) {
        Intensity.LOW -> listOf(Intensity.LOW, Intensity.MEDIUM, Intensity.HIGH)
        Intensity.MEDIUM -> listOf(Intensity.MEDIUM, Intensity.LOW, Intensity.HIGH)
        Intensity.HIGH -> listOf(Intensity.HIGH, Intensity.MEDIUM, Intensity.LOW)
    }


}