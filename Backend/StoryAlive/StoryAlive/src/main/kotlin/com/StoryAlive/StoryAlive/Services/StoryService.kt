package com.StoryAlive.StoryAlive.Services

import Story
import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.Story.StoryCreationDTO
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.DTOs.Key.CastKey
import com.StoryAlive.StoryAlive.DTOs.Key.VoiceActorKey
import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import com.StoryAlive.StoryAlive.Models.Audio
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.StoryRepo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

@Service
class StoryService(private val storyRepo: StoryRepo,
                   private val supabaseStorageService: SupabaseStorageService,
                   private val aiService: AIService,
                   private val voiceActorService: VoiceActorService) {
    val mapper = jacksonObjectMapper()

    fun getAllStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        return storyRepo.findAllByIsPrivateFalse(pageable)
    }
    fun getAllPrivateStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable = PageRequest.of(pageNumber, pageSize)

        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails

        val creatorId: ObjectId = currentUser.getUserId()

        return storyRepo.findAllByCreatorIdAndIsPrivateTrue(creatorId, pageable)
    }

    fun createStoryMetaData(storyRequest: StoryRequestDTO): ObjectId {
        val currentUser = getCurrrenctUser()

        val voiceActorsMap: MutableMap<ObjectId, Pair<String, String>> = (if (storyRequest.voiceActors.isNullOrEmpty()) {
            emptyMap()
        } else {
            storyRequest.voiceActors.mapValues { (actorId) ->
                val actor: Optional<VoiceActor> = voiceActorService.getAudioByActorId(actorId)
                if (actor != null && actor.get().audios.isNotEmpty()) {
                    // Use the actor's name along with the cast name
                    Pair(actor.get().actorName, "")
                } else {
                    // Fallback if somehow the actor has no audios (unlikely)
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
        storyRepo.save(newStory)
        return newStory.storyId
    }
    fun createStory(storyId: ObjectId, pdf: MultipartFile): Story{
        val jsonString = aiService.generateStoryFromPdf(pdf.bytes)
        val storyDto: StoryCreationDTO = mapper.readValue(jsonString, StoryCreationDTO::class.java)
        storyDto.storyId = storyId.toString()
        return saveMetaData(storyDto,pdf, jsonString = jsonString)
    }

    //HELPER FUNCTIONS
    fun createStoryDummy(storyId: ObjectId) {
        val currentStory: Story = storyRepo.findById(storyId)
            .orElseThrow { RuntimeException("Story not found with id $storyId") }

        val jsonString = supabaseStorageService.downloadFileFromSupabase(currentStory.jsonPath ?: "")
        val storyDto: StoryCreationDTO = mapper.readValue(jsonString, StoryCreationDTO::class.java)

        val allVoiceActors: List<VoiceActor> = voiceActorService.getAllPublicVoiceActors()

        // Build audio lookup for fast access
        val actorAudioIndex: Map<ObjectId, Map<Pair<Emotion, Intensity>, Audio>> = allVoiceActors.associate { actor ->
            actor.voiceActorId to actor.audios.associateBy { it.emotion to it.intensity }
        }

        // Build mutable pool of actors by VoiceActorKey
        val actorsMap: MutableMap<VoiceActorKey, MutableList<Pair<ObjectId, String>>> = allVoiceActors
            .groupBy { VoiceActorKey(it.isAdult, it.gender) }
            .mapValues { it.value.map { actor -> actor.voiceActorId to actor.actorName }.toMutableList() }
            .toMutableMap()

        // 1️⃣ Assign user-selected actors first
        val mutableVoiceActors = currentStory.voiceActors.toMutableMap()
        currentStory.voiceActors.forEach { (actorId, pair) ->
            val actor = voiceActorService.getAudioByActorId(actorId)
                .orElseThrow { RuntimeException("VoiceActor not found: $actorId") }
            val key = VoiceActorKey(actor.isAdult, actor.gender)
            actorsMap[key]?.removeIf { it.first == actorId }
            mutableVoiceActors[actorId] = pair
        }

        // 2️⃣ Assign remaining actors to cast members
        storyDto.cast.forEach { castMember ->
            val key = VoiceActorKey(castMember.isAdult, castMember.gender)
            val availableActors = actorsMap[key] ?: throw RuntimeException("No available actor for cast member ${castMember.name} with $key")
            val selectedActor = availableActors.removeAt(0)
            mutableVoiceActors[selectedActor.first] = selectedActor.second to castMember.name
        }
        currentStory.voiceActors = mutableVoiceActors

        // 3️⃣ Assign audio to title and sentences
        val castMap: MutableMap<CastKey, Audio> = mutableMapOf()
        storyDto.chapters.forEach { chapter ->
            val sentences = listOf(chapter.title) + chapter.scenes.flatMap { it.sentences }
            sentences.forEach { sentence ->
                val actorId = currentStory.voiceActors.entries.firstOrNull { it.value.second == sentence.speaker }?.key
                    ?: throw RuntimeException("No actor assigned for speaker ${sentence.speaker}")
                val audio = actorAudioIndex[actorId]?.get(sentence.emotion to sentence.intensity)
                    ?: throw RuntimeException("No audio found for ${sentence.speaker} with ${sentence.emotion}/${sentence.intensity}")
                val castKey = CastKey(actorId, sentence.speaker, sentence.emotion, sentence.intensity)
                castMap[castKey] = audio
                sentence.speaker = audio.filepath
            }
        }

        // 4️⃣ Save updated JSON back to Supabase
        val updatedJson = mapper.writeValueAsString(storyDto)
        val jsonPath = supabaseStorageService.saveJsonToCloud(updatedJson, getCurrrenctUser().getUserId())
        currentStory.jsonPath = jsonPath
        storyRepo.save(currentStory)
    }

    fun saveMetaData(storyDto: StoryCreationDTO, pdf: MultipartFile,jsonString: String): Story {
        val currentUser = getCurrrenctUser()
        val currentStory: Story = storyRepo.findById(ObjectId(storyDto.storyId))
            .orElseThrow { RuntimeException("Story not found with id ${storyDto.storyId}") }
        val pdfPath = supabaseStorageService.savePdfToCloud(pdf, currentUser.getUserId())
        val jsonPath = supabaseStorageService.saveJsonToCloud(jsonString, currentUser.getUserId())
        currentStory.pdfPath= pdfPath
        currentStory.jsonPath= jsonPath
        return storyRepo.save(currentStory)
    }
    fun getCurrrenctUser(): CurrentUserDetails{
        val auth = SecurityContextHolder.getContext().authentication
        require(auth != null && auth.principal is CurrentUserDetails)
        return auth.principal as CurrentUserDetails
    }

}